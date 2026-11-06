/**
 * TestCsvUtility.java
 * edu.harvard.mcz.precapture.test
 * Copyright © 2026 President and Fellows of Harvard College
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of Version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.harvard.mcz.precapture.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.harvard.mcz.precapture.data.CsvUtility;

/**
 * Tests that CsvUtility reads and writes csv the way the javacsv library, 
 * which it replaced, did.
 */
public class TestCsvUtility {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testWrite() throws IOException {
		File file = folder.newFile("out.csv");
		try (CSVPrinter printer = CsvUtility.openForWriting(file.getPath(), StandardCharsets.UTF_8)) { 
			printer.printRecord("Family", "Genus", "Authorship");
			printer.printRecord("Orchid, aceae", " lead", "L. \"x\"");
			printer.printRecord("#hash", null, "Üä trail ");
			printer.printRecord("", "multi\nline", "plain");
		}
		String expected = "Family,Genus,Authorship\n"
				+ "\"Orchid, aceae\",\" lead\",\"L. \"\"x\"\"\"\n"
				+ "\"#hash\",,\"Üä trail \"\n"
				+ "\"\",\"multi\nline\",plain\n";
		assertEquals(expected, new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8));
	}

	@Test
	public void testRead() throws IOException {
		File file = folder.newFile("in.csv");
		String content = "Family,Genus,Tribe\n"
				+ "  Orchidaceae  , \"Malaxis\" ,\"  q  \"\n"
				+ "\n"
				+ "Rosaceae,Rosa\n"
				+ "\"a,b\",c,Ü\n";
		Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
		assertEquals(4, CsvUtility.countRecords(file.getPath(), StandardCharsets.UTF_8));
		List<String> values = new ArrayList<String>();
		try (CSVParser parser = CsvUtility.openForReading(file.getPath(), StandardCharsets.UTF_8)) { 
			assertEquals("[Family, Genus, Tribe]", parser.getHeaderNames().toString());
			for (CSVRecord record : parser) { 
				values.add("[" + CsvUtility.get(record, "Family") + "][" + CsvUtility.get(record, "Genus") + "][" 
						+ CsvUtility.get(record, "Tribe") + "][" + CsvUtility.get(record, "NotAColumn") + "]");
			}
		}
		assertEquals("[[Orchidaceae][Malaxis][  q  ][], [Rosaceae][Rosa][][], [a,b][c][Ü][]]", values.toString());
	}

	@Test
	public void testReadWithByteOrderMark() throws IOException {
		// Excel's "CSV UTF-8" format starts the file with a UTF-8 byte order mark
		byte[] bom = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
		byte[] content = "Family,Genus\nOrchidaceae,Malaxis Ü\n".getBytes(StandardCharsets.UTF_8);
		byte[] withBom = new byte[bom.length + content.length];
		System.arraycopy(bom, 0, withBom, 0, bom.length);
		System.arraycopy(content, 0, withBom, bom.length, content.length);
		File file = folder.newFile("bom.csv");
		Files.write(file.toPath(), withBom);
		// read as UTF-8 even when ISO-8859-1 is expected
		for (Charset charset : new Charset[] { StandardCharsets.UTF_8, StandardCharsets.ISO_8859_1 }) {
			assertEquals(2, CsvUtility.countRecords(file.getPath(), charset));
			try (CSVParser parser = CsvUtility.openForReading(file.getPath(), charset)) {
				assertEquals("[Family, Genus]", parser.getHeaderNames().toString());
				CSVRecord record = parser.iterator().next();
				assertEquals("Orchidaceae", CsvUtility.get(record, "Family"));
				assertEquals("Malaxis Ü", CsvUtility.get(record, "Genus"));
			}
		}
		// a file shorter than a byte order mark
		File shortFile = folder.newFile("short.csv");
		Files.write(shortFile.toPath(), "a\n".getBytes(StandardCharsets.UTF_8));
		assertEquals(1, CsvUtility.countRecords(shortFile.getPath(), StandardCharsets.UTF_8));
	}

}
