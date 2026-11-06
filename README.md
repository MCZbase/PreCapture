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

## Upgrading

PreCapture keeps its data (taxon authority file and inventory) in an H2 database 
in the directory it is run from.  Databases created by earlier versions of 
PreCapture (PRECAPTURE2.h2.db from releases up to 1.1.2, or PRECAPTURE2.mv.db 
from 2022-2023 development builds) use older H2 file formats.  On startup 
PreCapture detects these and offers to convert them, which downloads the 
earlier H2 version from Maven Central; the original file is kept with a .bak suffix.

To build PreCaptureApp.jar run:

mvn package
