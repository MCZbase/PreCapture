/**
 * SPDX-License-Identifier: GPL-2.0-only
 * CsvUtility.java
 * edu.harvard.mcz.precapture.data
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
package edu.harvard.mcz.precapture.data;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.DuplicateHeaderMode;

/**
 * Apache Commons CSV settings shared by the import and export of the taxon
 * authority file and inventory, configured to read and write csv the same
 * way as the javacsv library previously used: comma delimited, double quote
 * text qualifier, values quoted only when needed, \n line endings, blank lines
 * skipped, and surrounding white space trimmed from unquoted values.  Unlike
 * javacsv, a UTF-8 byte order mark at the start of a file is recognized.
 */
public final class CsvUtility {

	/** Format for writing csv files. */
	public static final CSVFormat WRITE_FORMAT = CSVFormat.DEFAULT.builder()
			.setRecordSeparator("\n")
			.get();

	/** Format for reading csv files that have a header line. */
	public static final CSVFormat READ_WITH_HEADER_FORMAT = CSVFormat.DEFAULT.builder()
			.setHeader()
			.setSkipHeaderRecord(true)
			.setIgnoreSurroundingSpaces(true)
			.setIgnoreEmptyLines(true)
			.setAllowMissingColumnNames(true)
			.setDuplicateHeaderMode(DuplicateHeaderMode.ALLOW_ALL)
			.get();

	/** The UTF-8 encoding of the byte order mark U+FEFF. */
	private static final byte[] UTF8_BOM = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };

	private CsvUtility() { }

	/**
	 * Open a csv file with a header line for reading.
	 *
	 * @param filename the file to read.
	 * @param charset the character encoding of the file.
	 * @return a parser, the caller is responsible for closing it.
	 * @throws IOException on an error opening the file.
	 */
	public static CSVParser openForReading(String filename, Charset charset) throws IOException {
		return CSVParser.parse(openReader(filename, charset), READ_WITH_HEADER_FORMAT);
	}

	/**
	 * Open a text file for reading.  If the file starts with a UTF-8 byte order
	 * mark (as written by Excel's "CSV UTF-8" format) the mark is skipped and the
	 * file is read as UTF-8, otherwise it is read with the given character encoding.
	 *
	 * @param filename the file to read.
	 * @param charset the character encoding of the file if it has no byte order mark.
	 * @return a reader, the caller is responsible for closing it.
	 * @throws IOException on an error opening the file.
	 */
	public static Reader openReader(String filename, Charset charset) throws IOException {
		InputStream in = new BufferedInputStream(Files.newInputStream(Paths.get(filename)));
		try {
			in.mark(UTF8_BOM.length);
			byte[] start = new byte[UTF8_BOM.length];
			int read = in.readNBytes(start, 0, start.length);
			if (read == UTF8_BOM.length && Arrays.equals(start, UTF8_BOM)) {
				charset = StandardCharsets.UTF_8;
			} else {
				in.reset();
			}
			return new InputStreamReader(in, charset);
		} catch (IOException | RuntimeException e) {
			in.close();
			throw e;
		}
	}

	/**
	 * Open a csv file for writing, replacing any existing content.
	 *
	 * @param filename the file to write.
	 * @param charset the character encoding to write.
	 * @return a printer, the caller is responsible for closing it.
	 * @throws IOException on an error opening the file.
	 */
	public static CSVPrinter openForWriting(String filename, Charset charset) throws IOException {
		return new CSVPrinter(Files.newBufferedWriter(Paths.get(filename), charset), WRITE_FORMAT);
	}

	/**
	 * Count the records (lines, including the header) in a csv file.
	 *
	 * @param filename the file to read.
	 * @param charset the character encoding of the file.
	 * @return the number of records in the file.
	 * @throws IOException on an error reading the file.
	 */
	public static int countRecords(String filename, Charset charset) throws IOException {
		int count = 0;
		try (CSVParser parser = CSVParser.parse(openReader(filename, charset), CSVFormat.DEFAULT)) {
			for (@SuppressWarnings("unused") CSVRecord record : parser) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Obtain the value of a column by its header name, returning an empty string
	 * if the file has no such column or the record has no value for it.
	 *
	 * @param record the record from which to obtain the value.
	 * @param name the header of the column.
	 * @return the value, or an empty string.
	 */
	public static String get(CSVRecord record, String name) {
		if (record.isMapped(name) && record.isSet(name)) {
			return record.get(name);
		}
		return "";
	}

}
