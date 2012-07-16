/**
 * LabelEncoder.java
 * edu.harvard.mcz.imagecapture.encoder
 * Copyright © 2009 President and Fellows of Harvard College
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
 * 
 * Author: Paul J. Morris
 */
package edu.harvard.mcz.imagecapture.encoder;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.ServiceUI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import edu.harvard.mcz.imagecapture.exceptions.PrintFailedException;
import edu.harvard.mcz.imagecapture.ui.ContainerLabel;
import edu.harvard.mcz.imagecapture.xml.Field;

/** LabelEncoder
 * 
 * @author Paul J. Morris
 *
 */
public class LabelEncoder {
	
	private static final Log log = LogFactory.getLog(LabelEncoder.class);
	
	private ContainerLabel label;
	
	public LabelEncoder (ContainerLabel containerLabel)  {
		label = containerLabel;
	}
	
	public static boolean useItalic(Field aField) { 
		boolean result = false;
		if (aField.getVocabularyTerm().equals("dwc:genus")) { result = true; } 		
		if (aField.getVocabularyTerm().equals("dwc:specificEpithet")) { result = true; } 		
		if (aField.getVocabularyTerm().equals("dwc:infraspecificEpithet")) { result = true; } 		
		return result;
	}

	private BitMatrix getQRCodeMatrix() { 
		BitMatrix result = null;
		QRCodeWriter writer = new QRCodeWriter();
		try {
			// String data = label.toJSONString();
			StringBuffer data = new StringBuffer();
			data.append(label.toJSON());
			Hashtable<EncodeHintType, ErrorCorrectionLevel> hints = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();  // set ErrorCorrectionLevel here
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			result = writer.encode(data.toString(), BarcodeFormat.QR_CODE, 200, 200, hints);
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	public Image getImage() { 
		BitMatrix barcode = getQRCodeMatrix();
		BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(barcode);
		Image image = null;
		try {
			image = Image.getInstance(bufferedImage,null);
		} catch (BadElementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return image;
	}
	
	@SuppressWarnings("hiding")
	public static boolean printList(ArrayList<ContainerLabel> containers) throws PrintFailedException { 
		log.debug("Invoked printList ");
		boolean result = false;
		ContainerLabel label = new ContainerLabel();
		LabelEncoder encoder = new LabelEncoder(containers.get(0));
		Image image = encoder.getImage();
		int counter = 0;
		try {
			Document document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream("labels.pdf"));
			document.setPageSize(PageSize.LETTER);
			document.open();
			
			PdfPTable table = new PdfPTable(4);
			table.setWidthPercentage(100f);
			//table.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
			float[] cellWidths = { 30f, 20f, 30f, 20f } ;
			table.setWidths(cellWidths);
			
			Iterator<ContainerLabel> i = containers.iterator();
			PdfPCell cell = null;
			PdfPCell cell_barcode = null;
			// Create two lists of 12 cells, the first 6 of each representing
			// the left hand column of 6 labels, the second 6 of each 
			// representing the right hand column.  
			// cells holds the text for each label, cells_barcode the barcode.
			ArrayList<PdfPCell> cells = new ArrayList<PdfPCell>(12);
			ArrayList<PdfPCell> cells_barcode = new ArrayList<PdfPCell>(12);
			for (int x=0; x<12; x++) { 
				cells.add(null);
				cells_barcode.add(null);
			}
			int cellCounter = 0;
			while (i.hasNext()) {
				// Loop through all of the taxa (unit tray labels) found to print 
				label = i.next();
				//for (int toPrint=0; toPrint<label.getNumberToPrint(); toPrint++) {
				for (int toPrint=0; toPrint<1; toPrint++) {
					// For each taxon, loop through the number of requested copies 
					// Generate a text and a barcode cell for each, and add to array for page
					log.debug("Label " + toPrint + " of " + 1 );

					cell = label.toPDFCell();
					
					cell_barcode = new PdfPCell();
					cell_barcode.setBorderColor(Color.LIGHT_GRAY);
					cell_barcode.disableBorderSide(PdfPCell.LEFT);
					cell_barcode.setVerticalAlignment(PdfPCell.ALIGN_TOP);

					encoder = new LabelEncoder(containers.get(cellCounter));
					image = encoder.getImage();
					image.setAlignment(Image.ALIGN_TOP);
					cell_barcode.addElement(image);

					cells.add(cellCounter, cell);
					cells_barcode.add(cellCounter,cell_barcode);
					
					cellCounter++;
					// If we have hit a full set of 12 labels, add them to the document
					// in two columns, filling left column first, then right
					if (cellCounter==12) {
						// add a page of 12 cells in columns of two.
						for (int x=0;x<6;x++) {
							if (cells.get(x)==null) {
								PdfPCell c = new PdfPCell();
								c.setBorder(0);
								table.addCell(c);
						        table.addCell(c);
							} else { 
					            table.addCell(cells.get(x));
					            table.addCell(cells_barcode.get(x));
							}
							if (cells.get(x+6)==null) {
								PdfPCell c = new PdfPCell();
								c.setBorder(0);
								table.addCell(c);
						        table.addCell(c);
							} else { 
					            table.addCell(cells.get(x+6));
					            table.addCell(cells_barcode.get(x+6));
							}
						} 
						// Reset to begin next page
						cellCounter = 0;
						document.add(table);
						table = new PdfPTable(4);
						table.setWidthPercentage(100f);
						table.setWidths(cellWidths);
						for (int x=0;x<12;x++) { 
							cells.set(x, null);
							cells_barcode.set(x, null);
						}
				    }
				} // end loop through toPrint (for a taxon)
				counter ++;
			} // end while results has next (for all taxa requested)
			// get any remaining cells in pairs
			for (int x=0;x<6;x++) {
				if (cells.get(x)==null) {
					PdfPCell c = new PdfPCell();
					c.setBorder(0);
					table.addCell(c);
			        table.addCell(c);
				} else { 
		            table.addCell(cells.get(x));
		            table.addCell(cells_barcode.get(x));
				}
				if (cells.get(x+6)==null) {
					PdfPCell c = new PdfPCell();
				    c.setBorder(0);
					table.addCell(c);
			        table.addCell(c);
				} else { 
		            table.addCell(cells.get(x+6));
		            table.addCell(cells_barcode.get(x+6));
				}
			} 
			// add any remaining cells
			document.add(table);
			try { 
				
	    	    //TODO: Send to printer. 
			    //See: http://stackoverflow.com/questions/4609667/how-to-print-a-pdf-created-with-itext
				
			    document.close();
			    
			} catch (Exception e) { 
				throw new PrintFailedException("No labels to print." + e.getMessage());
			}
			// Check to see if there was content in the document.
			if (counter==0) { 
				result = false;
			} else { 
			    // Printed to pdf ok.
				result = true;
				// Increment number printed.
				i = containers.iterator();
				while (i.hasNext()) { 
					label = i.next();
					//for (int toPrint=0; toPrint<label.getNumberToPrint(); toPrint++) {
					//	label.setPrinted(label.getPrinted() + 1);
					//}
					//label.setNumberToPrint(0);
                    //try {
					//	uls.attachDirty(label);
					//} catch (SaveFailedException e) {
						// TODO Auto-generated catch block
					//	e.printStackTrace();
					//}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new PrintFailedException("File not found.");
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new PrintFailedException("Error buiding PDF document.");
		} catch (OutOfMemoryError e ) { 
			System.out.println("Out of memory error. " + e.getMessage());
			System.out.println("Failed.  Too many labels.");
			throw new PrintFailedException("Ran out of memory, too many labels at once.");
		} 
		log.debug("printList Done");
		return result;
	}
	
}
