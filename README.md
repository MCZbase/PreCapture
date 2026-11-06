# PreCapture
PreCapture application used for creating human and machine readable labels for natural science collections storage containers to support object to image to data workflows.  Used by the MCZ and the NEVP TCN.

[![DOI](https://zenodo.org/badge/68395772.svg)](https://zenodo.org/badge/latestdoi/68395772)

This PreCapture barcode application refactored from the DataShot application code base with support from the US National Science Foundation ADBC TCN for New England Vascular Plants NSF #1208835.

Licensed under Version 2 of the GNU General Public License

Copyright © 2012-2016 President and Fellows of Harvard College

## Sources

Source code is at: https://github.com/MCZbase/PreCapture
Former source code repository is at: https://sourceforge.net/p/datashot/code/HEAD/tree/trunk/precapture_app/ 

## Building

Building and running requires Java 17 or later.

To build PreCaptureApp.jar run:

mvn package

The tests are run by mvn package, and on GitHub for every push by the Test 
workflow (.github/workflows/test.yml).

## Releasing

Release versions are numbered e.g. 1.1.3 and tagged v1.1.3.  To make a release:

1. In pom.xml, set the version to the release version, removing -SNAPSHOT 
   (1.1.3-SNAPSHOT becomes 1.1.3), and change the tag in the scm section from 
   HEAD to the release tag (v1.1.3).  Commit and push.
2. Publish a GitHub release with the tag v1.1.3 on that commit.  This runs the 
   Release executable jar workflow (.github/workflows/release.yml), which checks 
   that the version and scm tag in pom.xml match the release tag, builds and 
   tests that version, and attaches PreCaptureApp-1.1.3.jar, an executable jar 
   including all dependencies (run with java -jar PreCaptureApp-1.1.3.jar), and 
   its SHA-256 checksum to the release.
3. In pom.xml, set the version to the next -SNAPSHOT version (1.1.4-SNAPSHOT) 
   and the scm tag back to HEAD.  Commit and push.

The workflow can also be run from the Actions tab for an existing release tag, 
to rebuild and replace the jar.

## Upgrading

PreCapture keeps its data (taxon authority file and inventory) in an H2 database, 
by default PRECAPTURE2.mv.db in the directory it is run from (set config.database 
in PreCapture.properties to use another location).  Databases created by earlier 
versions of PreCapture (PRECAPTURE2.h2.db from releases up to 1.1.2, or 
PRECAPTURE2.mv.db from 2022-2023 development builds) use older H2 file formats.  
On startup PreCapture detects these and offers to convert them, which downloads 
the earlier H2 version from Maven Central; the original file is kept with a .bak 
suffix.  If Maven Central can't be reached, PreCapture can instead start with a 
new, empty, database, keeping the old one, after which the taxon authority file 
can be reloaded (Configuration, Data Sources).

## Barcode capacity and print resolution

The amount of text a label's QR code can hold, and still be read, depends on the 
size the code is printed at and the resolution it is printed (or scanned) at.  
File, Run Tests opens a stress test for the selected print format: it fills every 
field of a label with progressively longer text, encodes the label as a QR code 
(error correction level H), places the code in a PDF scaled to the format's 
barcode cell, renders the PDF page to an image at 72, 150 and 300 dpi, and 
checks whether the code can be read back from each image.

Results for the default format, Two Half Sheets on 8.5x11, where the barcode 
cell is 93 points (about 1.3 inches) wide.  Payload is the size of the encoded 
label text; QR width is the size of the code in modules (the black and white 
squares), including the 4 module quiet zone on each side.  Consecutive payload 
sizes with the same results are combined.

| Payload (bytes) | QR width (modules incl. quiet zone) | 72 dpi | 150 dpi | 300 dpi |
|---:|---:|---|---|---|
| 160–244 | 77–89 | ❌ fail | ✅ read | ✅ read |
| 256–280 | 93 | ✅ read | ✅ read | ✅ read |
| 304 | 97 | ❌ fail | ✅ read | ✅ read |
| 328 | 101 | ❌ fail | ❌ fail | ✅ read |
| 352–376 | 105 | ❌ fail | ✅ read | ✅ read |
| 400 | 109 | ❌ fail | ❌ fail | ✅ read |
| 424 | 113 | ❌ fail | ✅ read | ✅ read |
| 448–496 | 117–121 | ❌ fail | ❌ fail | ✅ read |
| 520 | 125 | ❌ fail | ✅ read | ✅ read |
| 544–616 | 129–133 | ❌ fail | ❌ fail | ✅ read |
| 640 | 137 | ❌ fail | ✅ read | ✅ read |
| 664–688 | 141 | ❌ fail | ✅ read | ❌ fail |
| 712–736 | 145 | ❌ fail | ❌ fail | ✅ read |
| 760–784 | 149 | ❌ fail | ❌ fail | ❌ fail |
| 808–832 | 153 | ❌ fail | ❌ fail | ✅ read |
| 856–952 | 157–161 | ❌ fail | ❌ fail | ❌ fail |
| 976–1048 | 165–169 | ❌ fail | ❌ fail | ✅ read |
| 1072 | 173 | ❌ fail | ❌ fail | ❌ fail |
| 1096–1216 | 177–181 | ❌ fail | ❌ fail | ✅ read |
| 1240–1264 | 185 | ❌ fail | ❌ fail | ❌ fail |
| 1288 | – | too much data | too much data | too much data |

In this test, codes are read reliably up to 304 bytes at 150 dpi and up to 640 
bytes at 300 dpi.  At 72 dpi (screen resolution) only codes that are exactly 93 
modules wide, one pixel per module, are read.  Beyond those sizes the results are 
not monotonic: whether a code can be read depends on how its modules fall on the 
pixel grid, as a module that is not a whole number of pixels wide is blurred 
unevenly (e.g. 141 modules in 93 points at 300 dpi is 2.75 pixels per module).  
1288 bytes exceeds the capacity of a QR code at error correction level H, at any 
resolution.

Caveats: this simulates printing, it is not a test of printed labels.  The QR 
code is embedded in the PDF as an image with one pixel per module, which PDFBox 
resamples when rendering the page; a printer driver resamples differently, 
printers and scanners add their own blur, and scanning resolution matters as 
much as printing resolution.  Treat the table as indicative, with the 300 dpi 
column closest to a typical laser printer, and test real labels with the 
scanner and software that will read them, particularly for payloads above about 
300 bytes or for other print formats.

### Example: a payload of about 300 bytes

The payload is the label's fields as JSON, with short keys defined by the field 
mapping (config.fieldmap).  This folder label for a variety, using the default 
NEVP TCN mapping, is 304 bytes; in the table above it can be read at 150 and 300 
dpi, but not at 72 dpi:

```json
{"m1p":"[NEVP TCN]","m2v":"1.1","g":"Symphyotrichum","s":"novi-belgii","ir":"var.","i":"villicaule","sn":"Symphyotrichum novi-belgii var. villicaule (A.G. Jones) Labrecque & Brouillet","a":"(A.G. Jones) Labrecque & Brouillet","st":"Massachusetts","ct":"Barnstable","sr":"GH Case 2041, Shelf 3","cc":"GH"}
```

| Key | Field | Value |
|---|---|---|
| m1p, m2v | Field mapping project and version | [NEVP TCN], 1.1 (fixed, 32 bytes with the braces) |
| g, s | Genus, Specific Epithet | Symphyotrichum novi-belgii |
| ir, i | Infraspecific Rank, Infraspecific Epithet | var. villicaule |
| sn | Scientific Name | Symphyotrichum novi-belgii var. villicaule (A.G. Jones) Labrecque & Brouillet |
| a | Authorship | (A.G. Jones) Labrecque & Brouillet |
| st, ct | State, County | Massachusetts, Barnstable |
| sr | Storage Location | GH Case 2041, Shelf 3 |
| cc | Herbarium Acronym | GH |

Fields that are left empty (here Identification Qualifier and Collector) are not 
included.  Each field adds about 8 bytes for its key and quotation marks, and 
characters outside ASCII take 2 or more bytes.  The Scientific Name repeats the 
genus, epithets and authorship, about 80 bytes here.  For comparison, the same 
label for a species with a short authorship, Acer rubrum L., is 165 bytes, well 
within what can be read at 150 dpi.

Scientific Name is not filled in when a taxon is picked from the taxon authority 
file, it is only included if it is typed in, so leaving it blank keeps the 
payload smaller; without it this label is 219 bytes.  To remove the field 
altogether, copy NEVP_TCN_PrecaptureFields.xml (in 
src/main/resources/edu/harvard/mcz/precapture/resources/), delete the 
FieldInList element for Scientific Name (code sn), and set config.fieldmap in 
PreCapture.properties to the path of the copy.  The field is then neither 
entered, printed in the label text, nor encoded in the barcode; the genus, 
epithets and authorship are still printed from their own fields.  Keep the 
SupportedProject and Version unchanged, so that labels printed with either 
mapping can be read with the other.  Note that File, Make Annotation Labels, which 
reads a label's barcode, displays the Scientific Name from the barcode, and 
shows it blank for labels without one.
