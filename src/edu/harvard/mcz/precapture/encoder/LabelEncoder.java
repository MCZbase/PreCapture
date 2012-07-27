/**
 * LabelEncoder.java
 * edu.harvard.mcz.precapture.encoder
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
package edu.harvard.mcz.precapture.encoder;

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
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import edu.harvard.mcz.precapture.PreCaptureProperties;
import edu.harvard.mcz.precapture.PreCaptureSingleton;
import edu.harvard.mcz.precapture.exceptions.PrintFailedException;
import edu.harvard.mcz.precapture.ui.ContainerLabel;
import edu.harvard.mcz.precapture.xml.Field;
import edu.harvard.mcz.precapture.xml.labels.LabelDefinitionListType;
import edu.harvard.mcz.precapture.xml.labels.LabelDefinitionType;

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

	/** Test fields for darwin core concepts that should be italicized in presentation.
	 * @deprecated moved to configuration, IsItalic.
	 * @param aField field to test.
	 * @return true if field vocabularyTerm is one that should be italicized.
	 * 
	 */
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
		if (containers.isEmpty()) {
			log.debug("No labels to print.");
		} else { 
			LabelDefinitionType printDefinition = null;
			LabelDefinitionListType printDefs = PreCaptureSingleton.getInstance().getPrintFormatDefinitionList();
			List<LabelDefinitionType> printDefList = printDefs.getLabelDefinition();
			Iterator<LabelDefinitionType> il = printDefList.iterator();
			while (il.hasNext()) { 
				LabelDefinitionType def = il.next();
				if (def.getTitle().equals(PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_SELECTED_PRINT_DEFINITION))) { 
					printDefinition = def;
				}
			}
			if (printDefinition==null) {
				//TODO add error handling dialog for users
				log.error("No selected print format defintion found.");
			} else { 

				log.debug(printDefinition.getTitle());
				log.debug(printDefinition.getTextOrentation().toString());
				
				//TODO: Refactor to use properties to determine number of labels per page and layout.
				LabelEncoder encoder = new LabelEncoder(containers.get(0));
				Image image = encoder.getImage();
				try {
					Document document = new Document();
					PdfWriter.getInstance(document, new FileOutputStream("labels.pdf"));
					// Convert units in print definition to points (72 points/inch, 28.346456 points/cm)
					
					int paperWidthPoints = 612;  // 8.5"
					int paperHeightPoints = 792;  // 11"
					int marginsPoints = 36; // 0.5"
					int labelWidthPoints = 540;  // 7.5" 
					int labelHeightPoints = 720; // 10"
					int numColumns = 1;   // goes with above
					
					numColumns = printDefinition.getColumns();
					
					if (printDefinition.getUnits().toString().toLowerCase().equals("inches")) { 
					    paperWidthPoints = (int)Math.floor(printDefinition.getPaperWidth()*72f);
					    paperWidthPoints = (int)Math.floor(printDefinition.getPaperWidth()*72f);
					    marginsPoints = (int)Math.floor(printDefinition.getMargins()*72f);
					    labelWidthPoints = (int)Math.floor(printDefinition.getLabelWidth()*72f);
					    labelHeightPoints = (int)Math.floor(printDefinition.getLabelHeight()*72f);
					}
					if (printDefinition.getUnits().toString().toLowerCase().equals("cm")) { 
					    paperWidthPoints = (int)Math.floor(printDefinition.getPaperWidth()*28.346456f);
					    paperWidthPoints = (int)Math.floor(printDefinition.getPaperWidth()*28.346456f);
					    marginsPoints = (int)Math.floor(printDefinition.getMargins()*28.346456f);
					    labelWidthPoints = (int)Math.floor(printDefinition.getLabelWidth()*28.346456f);
					    labelHeightPoints = (int)Math.floor(printDefinition.getLabelHeight()*28.346456f);
					}
					if (printDefinition.getUnits().toString().toLowerCase().equals("points")) { 
					    paperWidthPoints = (int)Math.floor(printDefinition.getPaperWidth()*1f);
					    paperWidthPoints = (int)Math.floor(printDefinition.getPaperWidth()*1f);
					    marginsPoints = (int)Math.floor(printDefinition.getMargins()*1f);
					    labelWidthPoints = (int)Math.floor(printDefinition.getLabelWidth()*1f);
					    labelHeightPoints = (int)Math.floor(printDefinition.getLabelHeight()*1f);
					}
					
					if (paperWidthPoints==612 && paperHeightPoints==792) { 
					    document.setPageSize(PageSize.LETTER);
					} else { 
						document.setPageSize(new Rectangle(paperWidthPoints,paperHeightPoints));
					}
					document.setMargins(marginsPoints, marginsPoints, marginsPoints, marginsPoints);
					document.open();
					
					// Sanity check
					if (paperWidthPoints<=0) { paperWidthPoints = 612; }  
					if (paperHeightPoints<=0) { paperHeightPoints = 792; }  
					if (marginsPoints<0) { marginsPoints = 0; }  
					if (labelWidthPoints<=0) { labelWidthPoints = 540; }  
					if (labelHeightPoints<=0) { labelHeightPoints = 720; }  
					if (paperWidthPoints+(marginsPoints*2)<labelWidthPoints) { 
						labelWidthPoints = paperWidthPoints+(marginsPoints*2);
						log.debug("Adjusting label width to fit printable page width");
					}
					if (paperHeightPoints+(marginsPoints*2)<labelHeightPoints) { 
						labelHeightPoints = paperHeightPoints+(marginsPoints*2);
						log.debug("Adjusting label height to fit printable page height");
					}
					
					// calculate how many columns will fit on the paper.
					int columns = (int)Math.floor((paperWidthPoints - (marginsPoints*2))/labelWidthPoints);
					// if specified column count is smaller, use the specified.
					if (numColumns<columns) { 
						columns = numColumns;
						log.debug("Fewer columns specified in definition than will fit on page, using specified column count of " + numColumns);
					}
					
					// define two table cells per column, one for text one for barcode.
				    int subCellColumnCount = columns * 2;
					
				    // set the table, with an absolute width and relative widths of the cells in the table;
					PdfPTable table = setupTable(paperWidthPoints, marginsPoints, labelWidthPoints, columns, subCellColumnCount);
					// figure out the width of the cells containing the barcodes.
					float ratio = ((float)REL_WIDTH_BARCODE_CELL)/(((float)REL_WIDTH_BARCODE_CELL)+((float)REL_WIDTH_TEXT_CELL));
		            float barcodeCellWidthPoints = (float) Math.floor(labelWidthPoints * ratio);
		            log.debug("Width of barcode cell in points: " + barcodeCellWidthPoints);

					//Rectangle pageSizeRectangle = new Rectangle(paperWidthPoints, paperHeightPoints);
					//table.setWidthPercentage(cellWidthsPoints, pageSizeRectangle);
					//table.setTotalWidth(cellWidthsPoints);
					
					// Calculate how many cells fit on a page (two cells per label).
					int labelsPerColumn = (int)Math.floor((paperHeightPoints-(marginsPoints*2))/labelHeightPoints);
					int cellsPerPage = subCellColumnCount * labelsPerColumn; 
					log.debug("Labels per column = " + labelsPerColumn);
					log.debug("Cells per page = " + cellsPerPage);
			
					Iterator<ContainerLabel> iterLabels = containers.iterator();
					
					int cellCounter = 0;  // counts number of cells filled on a page.
					int counter = 0;      // counts number of pre capture label data rows to print (each of which may request more than one copy).
					
					// TODO: Doesn't fit on page.
					
					while (iterLabels.hasNext()) {
						// Loop through all of the container labels found to print 
						label = iterLabels.next();
						log.debug("Label: " + counter + " " + label.toString());
						for (int toPrint=0; toPrint<label.getNumberToPrint(); toPrint++) {
							// For each container label, loop through the number of requested copies 
							// Generate a text and a barcode cell for each, and add to array for page
							int toPrintPlus = toPrint + 1;  // for pretty counter in log.
							log.debug("Copy " + toPrintPlus + " of " + label.getNumberToPrint());

							PdfPCell cell = label.toPDFCell(printDefinition);
							cell.setFixedHeight(labelHeightPoints);

							PdfPCell cell_barcode = new PdfPCell();
							cell_barcode.setBorderColor(BaseColor.LIGHT_GRAY);
							cell_barcode.disableBorderSide(PdfPCell.LEFT);
							cell_barcode.setVerticalAlignment(PdfPCell.ALIGN_TOP);
							cell_barcode.setFixedHeight(labelHeightPoints);

							encoder = new LabelEncoder(label);
							image = encoder.getImage();
							image.setAlignment(Image.ALIGN_TOP);
							image.setAlignment(Image.ALIGN_LEFT);
							image.scaleToFit(barcodeCellWidthPoints, labelHeightPoints);
							cell_barcode.addElement(image);

							table.addCell(cell);
							table.addCell(cell_barcode);

							cellCounter = cellCounter + 2;  // we've added two cells to the page (two cells per label).
							log.debug("Cells " + cellCounter + " of " + cellsPerPage + " cells per page.");
							
							// If we have hit a full set of labels for the page, add them to the document
							// in each column, filling left to right
							if (cellCounter>=cellsPerPage-1) {
								log.debug("Page is full");
								log.debug("Table has " + table.getNumberOfColumns() + " columns and " + table.getRows().size() + " rows ");
								// Reset to begin next page
								cellCounter = 0;
								table.setLockedWidth(true);
								document.add(table);
								log.debug("Adding new page");
								document.newPage();
								table = setupTable(paperWidthPoints, marginsPoints, labelWidthPoints, columns, subCellColumnCount);
							}
						} // end loop through toPrint (for a taxon/precapture label data row)
						counter ++;  // Increment number of pre capture label data rows.
					} // end while results has next (for all taxa requested)
					// get any remaining cells in pairs
					if (cellCounter>0) { 
						log.debug("Adding remaining cells in partial page");
						if (cellCounter<=cellsPerPage) { 
							for (int i=cellCounter; i<=cellsPerPage; i++) { 
								PdfPCell emptyCell = new PdfPCell();
								emptyCell.setBorder(PdfPCell.NO_BORDER);
								table.addCell(emptyCell);
							}
						}
						log.debug("Table has " + table.getNumberOfColumns() + " columns and " + table.getRows().size() + " rows ");
						table.setLockedWidth(true);
						document.add(table);
					}
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
			}
			log.debug("printList Done. Success = " + result);
		} 
		return result;
	}

	// TODO: Move to print definition configuration;
	public static final int REL_WIDTH_TEXT_CELL = 2;
	public static final int REL_WIDTH_BARCODE_CELL = 3;
	
	private static PdfPTable setupTable(int paperWidthPoints, int marginsPoints, int labelWidthPoints, int columns, int subCellColumnCount) throws DocumentException { 
		PdfPTable table = new PdfPTable(subCellColumnCount);
		table.setLockedWidth(true);   // force use of totalWidth in points, rather than percentWidth.
		float percentWidth = ((((float)paperWidthPoints)-(2f*((float)marginsPoints)))/((float)paperWidthPoints))*100f;
		//percentWidth = 100f;
		log.debug("Table Width Percent = " + percentWidth);
		table.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
		float[] cellWidthsRatio = new float[subCellColumnCount];
		int cellNumber = 0;
		for (int c=0;c<columns;c++) { 
		    cellWidthsRatio[cellNumber] = REL_WIDTH_TEXT_CELL; // width of text cell
		    cellNumber++;
		    cellWidthsRatio[cellNumber] = REL_WIDTH_BARCODE_CELL; // width of barcode cell
		    cellNumber++;
		}
		table.setTotalWidth(paperWidthPoints - 2* marginsPoints);
		// must set total width before setting relative cell widths.
		table.setWidths(cellWidthsRatio);
		log.debug("Width:" + table.getTotalWidth());
		return table;
	}
		
	
}
