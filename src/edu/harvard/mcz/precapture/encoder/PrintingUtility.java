/**
 * PrintingUtility.java
 * edu.harvard.mcz.precapture.encoder
 * Copyright © 2012 President and Fellows of Harvard College
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
 * Author: mole
 */
package edu.harvard.mcz.precapture.encoder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.Sides;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;

import edu.harvard.mcz.precapture.exceptions.PrintFailedException;
import edu.harvard.mcz.precapture.xml.labels.LabelDefinitionType;

/**
 * @author mole
 *
 */
public class PrintingUtility {
	private static final Log log = LogFactory.getLog(PrintingUtility.class);

	/** 
	 * Default no argument constructor, constructs a new PrintingUtility instance.
	 */
	public PrintingUtility() {

	}
	
	public static void sendPDFToPrinter(LabelDefinitionType printDefinition, int paperWidthPoints, int paperHeightPoints) throws PrintFailedException { 
		try { 
			
		    // send generated PDF to printer.
			
			FileInputStream pdfInputStream = new FileInputStream(LabelEncoder.getPrintFile());

			//DocFlavor psInFormat = DocFlavor.INPUT_STREAM.PDF;
			
			// TODO: No printers listed... Try autosense instead of PDF
		    DocFlavor psInFormat = DocFlavor.INPUT_STREAM.AUTOSENSE;
			
			Doc myDoc = new SimpleDoc(pdfInputStream, DocFlavor.INPUT_STREAM.PDF, null); 
			PrintRequestAttributeSet atset = new HashPrintRequestAttributeSet();
			atset.add(new Copies(1));
			// Set paper size
			if (paperWidthPoints==612 && paperHeightPoints==792) { 
				atset.add(MediaSizeName.NA_LETTER);
			} else { 
				float x = printDefinition.getPaperWidth();
				float y = printDefinition.getPaperHeight();
				if (printDefinition.getUnits().toString().toLowerCase().equals("inches")) {
					MediaSizeName mediaSizeName = MediaSize.findMedia(x, y, Size2DSyntax.INCH);
					if (mediaSizeName==null) { 
						// TODO: Handle non-standard paper sizes.  The following doesn't provide
						// what is needed.
					    atset.add(new MediaPrintableArea(0,0,x, y, MediaPrintableArea.INCH));
					} else { 
					    atset.add(mediaSizeName);
					}
				}
				if (printDefinition.getUnits().toString().toLowerCase().equals("cm")) { 
					x=x*10f;
					y=y*10f;
					atset.add(MediaSize.findMedia(x, y, Size2DSyntax.INCH));
				}
				if (printDefinition.getUnits().toString().toLowerCase().equals("points")) { 
					x=x/72f;
					y=y/72f;
					atset.add(MediaSize.findMedia(x, y, Size2DSyntax.INCH));
				}
			}
			atset.add(Sides.ONE_SIDED);
			PrintService[] services = PrintServiceLookup.lookupPrintServices(psInFormat, atset);
			log.debug("Number of matching printing services =  " + services.length);
//			if (services.length == 0) {
//				log.debug("Relaxing print lookup criteria");
//				// relax the criteria
//			    atset = new HashPrintRequestAttributeSet();
//			    services = PrintServiceLookup.lookupPrintServices(psInFormat, atset);
//			    log.debug("Number of matching printing services =  " + services.length);
//			}
//			if (services.length == 0) {
//				// Try a specific printer
//				String targetPrinterName = "HP Color LaserJet";
//			    log.debug("Trying printer by name: " + targetPrinterName);
//				HashPrintServiceAttributeSet pnameAttrSet = new HashPrintServiceAttributeSet(new PrinterName(targetPrinterName, null));
//				services = PrintServiceLookup.lookupPrintServices(null, pnameAttrSet); 
//			    log.debug("Number of matching printing services =  " + services.length);
//			}
			boolean printed = false;
			if (services.length ==0 ) {
				log.debug("No PDF printing services found.");
				log.error("Failing over to print using a pdf printing library");

				try { 
					pdfInputStream.close();
					
					pdfInputStream = new FileInputStream(LabelEncoder.getPrintFile());
					
					// trying pdfbox instead of pdf-renderer
					PDDocument pdfDocument = PDDocument.load(pdfInputStream);
					pdfDocument.print();
					pdfDocument.close();
					printed = true;
				} catch (Exception e) { 
			        log.error(e.getMessage(), e);
				}
			} else { 
				log.debug("Available printing services " + services.length);
				for (int i =0; i< services.length; i++) { 
					log.debug(services[i].getName());
				}
				Object selectedService = JOptionPane.showInputDialog(null,
						"Send labels to which printer?", "Input",
						JOptionPane.INFORMATION_MESSAGE, null,
						services, services[0]);
				if (selectedService!=null) { 
					DocPrintJob job = ((PrintService)selectedService).createPrintJob();
			        log.debug("Printing to " + ((PrintService)selectedService).getName() );
					try {
						job.print(myDoc, atset);
						printed = true;
					} catch (PrintException pe) {
						log.error("Printing Error: " + pe.getMessage());
						if (pe.getClass().getName().equals("sun.print.PrintJobFlavorException")) { 

							log.error("Failing over to print using a pdf printing library");

							try { 
								pdfInputStream.close();
								
								pdfInputStream = new FileInputStream(LabelEncoder.getPrintFile());
								
								// trying pdfbox instead of pdf-renderer
								PDDocument pdfDocument = PDDocument.load(pdfInputStream);
								pdfDocument.print();
								pdfDocument.close();
								printed = true;
								
								// Can't get pdf-renderer to print without scaling the document down.
//								FileChannel fc = pdfInputStream.getChannel();
//								ByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
//
//								PDFFile curFile=null;
//								PDFPrintPage pages=null;
//								curFile = new PDFFile(bb); 
//								pages = new PDFPrintPage(curFile);
//								PrinterJob printJob = PrinterJob.getPrinterJob();
//
//								printJob.setPrintService((PrintService)selectedService);
//								
//								printJob.setJobName("PreCaptureApp Labels");
//								Book book = new Book();
//								PageFormat pformat = PrinterJob.getPrinterJob().defaultPage();
//								log.debug(pformat.getHeight()); 
//								log.debug(pformat.getWidth()); 
//								log.debug(pformat.getImageableHeight()); 
//								log.debug(pformat.getImageableWidth());
//								log.debug(pformat.getOrientation());
//								
//								pformat.setOrientation(PageFormat.PORTRAIT);
//								Paper pPaper = pformat.getPaper();
//								pPaper.setImageableArea(1.0, 1.0, pPaper.getWidth() - 2.0, pPaper.getHeight() - 2.0);
//								pformat.setPaper(pPaper);
//								
//								log.debug(pformat.getHeight()); 
//								log.debug(pformat.getWidth()); 
//								log.debug(pformat.getImageableHeight()); 
//								log.debug(pformat.getImageableWidth());
//								log.debug(pformat.getOrientation());								
//								
//								book.append(pages, pformat, curFile.getNumPages());
//								printJob.setPageable(book);
//
//								//PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
//								printJob.printDialog(atset);
//								printJob.print(atset);
							} catch (Exception e) { 
			                    log.error(e.getMessage(), e);
							}
						} 
					}
				}
				pdfInputStream.close();
			} 
			if (!printed) { 
				JOptionPane.showMessageDialog(null, "Unable to locate or use a printer, print the file 'labels.pdf'");
				log.error("No available printing services");
			}
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
			throw new PrintFailedException("Unable to find PDF file to print " + e.getMessage());
		} catch (Exception e) { 
			log.error(e.getMessage());
			if (e!=null && e.getCause()!=null) { 
			    log.error(e.getCause().getMessage());
			}
			e.printStackTrace();
			throw new PrintFailedException("No labels to print." + e.getMessage());
		}
	}
	

	
	
}
