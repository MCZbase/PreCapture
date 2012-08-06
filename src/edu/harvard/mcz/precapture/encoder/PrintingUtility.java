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
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.Sides;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

			DocFlavor psInFormat = DocFlavor.INPUT_STREAM.PDF;
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
					atset.add(MediaSize.findMedia(x, y, Size2DSyntax.INCH));
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
			if (services.length > 0) {

				Object selectedService = JOptionPane.showInputDialog(null,
						"Send labels to which printer?", "Input",
						JOptionPane.INFORMATION_MESSAGE, null,
						services, services[0]);

				if (selectedService!=null) { 
					DocPrintJob job = ((PrintService)selectedService).createPrintJob();
					try {
						job.print(myDoc, atset);
					} catch (PrintException pe) {
						log.error("Printing Error: " + pe.getMessage());
					}
				}
				log.debug("Available printing services " + services.length);
				for (int i =0; i< services.length; i++) { 
					log.debug(services[i].getName());
				}
			} else { 
				log.error("No available printing services");
			}
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
			throw new PrintFailedException("Unable to find PDF file to print " + e.getMessage());
		} catch (Exception e) { 
			log.error(e.getMessage());
			throw new PrintFailedException("No labels to print." + e.getMessage());
		}
	}
	
}
