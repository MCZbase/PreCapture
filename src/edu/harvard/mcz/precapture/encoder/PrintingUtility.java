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
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

import edu.harvard.mcz.precapture.PreCaptureProperties;
import edu.harvard.mcz.precapture.PreCaptureSingleton;
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
	
	/**
	 * Send the generated PDF file to a printer.  (file to print is from LabelEncoder.getPrintFile().
	 * 
	 * @param printDefinition Used to find paper size
	 * @param paperWidthPoints
	 * @param paperHeightPoints
	 * @throws PrintFailedException if printing fails for any reason.
	 */
	public static void sendPDFToPrinter(LabelDefinitionType printDefinition, int paperWidthPoints, int paperHeightPoints) throws PrintFailedException { 
		try { 
			
		    // send generated PDF to printer.
			
			FileInputStream pdfInputStream = new FileInputStream(PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_LABELPRINTFILE));

			DocFlavor psInFormat = DocFlavor.INPUT_STREAM.PDF;
			
			// No printers listed... Don't Try autosense instead of PDF
		    // DocFlavor psInFormat = DocFlavor.INPUT_STREAM.AUTOSENSE;
			// Ends up listing printers that can't take the PDF,
			// Need instead to fail over to using a pdf printing library 
			// and having the pdf printing library pull up the printer dialog.
			
			Doc myDoc = new SimpleDoc(pdfInputStream, psInFormat, null); 
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
			boolean printed = false;
			if (services.length ==0 ) {
				log.debug("No PDF printing services found.");
				log.error("Failing over to print using awt PrintJob");

				try { 
					pdfInputStream.close();
					
					pdfInputStream = new FileInputStream(PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_LABELPRINTFILE));
					
					// trying pdfbox instead of pdf-renderer
					PDDocument pdfDocument = PDDocument.load(pdfInputStream);
					PrintingUtility.print(pdfDocument);
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

							log.error("Failing over to print using awt PrintJob");

							try { 
								pdfInputStream.close();
								
								pdfInputStream = new FileInputStream(PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_LABELPRINTFILE));
								
								// Send PDF to printer using PDFBox PDF printing support.
								PDDocument pdfDocument = PDDocument.load(pdfInputStream);
								PrintingUtility.print(pdfDocument);
								pdfDocument.close();
								printed = true;
								// Note, can't get pdf-renderer to print without re-scaling and shrinking the document.
								
							} catch (Exception e) { 
			                    log.error(e.getMessage(), e);
							}
						} 
					}
				}
				pdfInputStream.close();
			} 
			if (!printed) { 
				log.error("No available printing services");
				throw new PrintFailedException("Unable to locate or use a printer, print the file '" + PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_LABELPRINTFILE) +"'");
			}
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
			throw new PrintFailedException("Unable to find PDF file to print " + e.getMessage());
		} catch (Exception e) { 
			log.error(e.getMessage());
			if (e!=null && e.getCause()!=null) { 
			    log.error(e.getCause().getMessage());
			}
			throw new PrintFailedException("No labels to print." + e.getMessage());
		}
	}
	
	private static void print(PDDocument document) throws PrinterException  {
		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPageable(new PDFPageable(document));
		job.print();
	}
		
}
