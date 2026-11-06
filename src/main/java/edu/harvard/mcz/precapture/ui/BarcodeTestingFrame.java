/**
 * SPDX-License-Identifier: GPL-2.0-only
 * BarcodeTestingFrame.java
 * edu.harvard.mcz.precapture.ui
 * Copyright © 2012-2026 President and Fellows of Harvard College
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
 * Author: Claude Opus 5.5 (Anthropic)
 */
package edu.harvard.mcz.precapture.ui;


import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.FlowLayout;

import com.google.zxing.WriterException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import edu.harvard.mcz.precapture.PreCaptureProperties;
import edu.harvard.mcz.precapture.PreCaptureSingleton;
import edu.harvard.mcz.precapture.decoder.LabelDecoder;
import edu.harvard.mcz.precapture.encoder.LabelEncoder;
import edu.harvard.mcz.precapture.exceptions.BarcodeCreationException;
import edu.harvard.mcz.precapture.exceptions.BarcodeReadException;
import edu.harvard.mcz.precapture.ui.BarcodeTestResult;
import edu.harvard.mcz.precapture.ui.BarcodeTestResultsTableModel;
import edu.harvard.mcz.precapture.xml.labels.LabelDefinitionListType;
import edu.harvard.mcz.precapture.xml.labels.LabelDefinitionType;

import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JProgressBar;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

/**
 * @author mole
 *
 */
public class BarcodeTestingFrame extends JFrame {
	private static final long serialVersionUID = 5135117926693857582L;
	private static final Log log = LogFactory.getLog(BarcodeTestingFrame.class);
	
	private BarcodeTestingFrame frame;
	
	private JProgressBar progressBar;
	private JLabel labelFirstError;
	/** Resolutions, in dots per inch, at which the test PDF page is rendered to an 
	 * image to test reading the barcode: screen resolution (72) and typical printer 
	 * resolutions. */
	public static final int[] RENDER_RESOLUTIONS = { 72, 150, 300 };

	private JLabel labelResult;
	private JTable table;
	private JLabel labelImage;

	/** 
	 * Default no argument constructor, constructs a new BarcodeTestingFrame instance.
	 */
	public BarcodeTestingFrame() {
		frame = this;
		setIconImage(Toolkit.getDefaultToolkit().getImage(BarcodeTestingFrame.class.getResource("/edu/harvard/mcz/precapture/resources/icon.png")));
		setTitle("Test Barcode Label Reading");
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		getContentPane().add(panel, BorderLayout.SOUTH);
		
		JButton btnRunTests = new JButton("Run Tests");
		btnRunTests.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread worker = new Thread() {
					public void run() {
						try {
							Cursor cursor = frame.getCursor();
							frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
							runTests();
							frame.setCursor(cursor);
						} catch (Exception e) {
							log.error(e.getMessage());
							e.printStackTrace();
						}
					}
				};
				worker.start();
			}
		});
		panel.add(btnRunTests);
		
		JButton btnOk = new JButton("Done");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: Stop running tests.
				setVisible(false);
			}
		});
		btnOk.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(btnOk);
		
		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),}));
		
		JLabel lblNewLabel_2 = new JLabel("Test how many characters will fit in your QRCode and Printing Formats");
		panel_1.add(lblNewLabel_2, "4, 2");
		
		JLabel lblFormat = new JLabel("Format:");
		panel_1.add(lblFormat, "2, 4");
		
		JLabel lblNewLabel = new JLabel((PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_SELECTED_PRINT_DEFINITION)));
		panel_1.add(lblNewLabel, "4, 4");
		
		labelImage = new JLabel("");
		panel_1.add(labelImage, "6, 4");
		
		JLabel lblProgress = new JLabel("Progress:");
		panel_1.add(lblProgress, "2, 6");
		
		progressBar = new JProgressBar();
		panel_1.add(progressBar, "4, 6, fill, default");
		
		JLabel lblFirsterror = new JLabel("FirstError");
		panel_1.add(lblFirsterror, "2, 8");
		
		labelFirstError = new JLabel("");
		panel_1.add(labelFirstError, "4, 8");
		
		JLabel lblResult = new JLabel("Result");
		panel_1.add(lblResult, "2, 10");
		
		labelResult = new JLabel("");
		panel_1.add(labelResult, "4, 10");
		
		JScrollPane scrollPane = new JScrollPane();
		panel_1.add(scrollPane, "2, 12, 3, 1, fill, fill");
		
		table = new JTable(new BarcodeTestResultsTableModel());
		scrollPane.setViewportView(table);

		pack();
		setVisible(true);
	}

	private void runTests() {
		ArrayList<FieldPlusText> textFields = new ArrayList<FieldPlusText>(); 
        int fieldCount = PreCaptureSingleton.getInstance().getMappingList().getFieldInList().size();
	    for (int i=0; i<fieldCount; i++) { 
	    	textFields.add(new FieldPlusText(PreCaptureSingleton.getInstance().getMappingList().getFieldInList().get(i), new JTextField()));
	    } 
		ContainerLabel containerLabel = new ContainerLabel(textFields);
		containerLabel.setNumberToPrint(1);
		
		String json = containerLabel.toJSON();
		String decodedJson = null;
		
		LabelEncoder encoder = new LabelEncoder(containerLabel);
		
		// 
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
		if (printDefinition!=null) { 
		int labelWidthPoints = 540;  // 7.5" 
		int labelHeightPoints = 720; // 10"
		
		if (printDefinition.getUnits().toString().toLowerCase().equals("inches")) { 
		    labelWidthPoints = (int)Math.floor(printDefinition.getLabelWidth()*72f);
		    labelHeightPoints = (int)Math.floor(printDefinition.getLabelHeight()*72f);
		}
		if (printDefinition.getUnits().toString().toLowerCase().equals("cm")) { 
		    labelWidthPoints = (int)Math.floor(printDefinition.getLabelWidth()*28.346456f);
		    labelHeightPoints = (int)Math.floor(printDefinition.getLabelHeight()*28.346456f);
		}
		if (printDefinition.getUnits().toString().toLowerCase().equals("points")) { 
		    labelWidthPoints = (int)Math.floor(printDefinition.getLabelWidth()*1f);
		    labelHeightPoints = (int)Math.floor(printDefinition.getLabelHeight()*1f);
		}		
		float ratio = (2f)/((2f)+(3f));
        float barcodeCellWidthPoints = (float) Math.floor(labelWidthPoints * ratio);
        log.debug("Width of label in points: " + labelWidthPoints);
        log.debug("Width of barcode cell in points: " + barcodeCellWidthPoints);
		
		
		// test with each field populated with an increasing number of characters
		Iterator<FieldPlusText> i = containerLabel.getFields().iterator();
		int counter = 0;
		
		
		// estimate how many characters per field should be the upper limit for test.
		int stop = 1000/containerLabel.getFields().size();
		
		StringBuffer report = new StringBuffer();
		
		final JProgressBar progressBarF = progressBar;
		final int stopF = stop;
		SwingUtilities.invokeLater(new Runnable() { 
			public void run() { 
		        progressBarF.setMaximum(stopF);
			}
		});
		String lastSuccess = "";
		String firstError = null;
		int[] successes = new int[RENDER_RESOLUTIONS.length];
		int[] failures = new int[RENDER_RESOLUTIONS.length];
		BarcodeTestResultsTableModel testResults = new BarcodeTestResultsTableModel();
		for (int x=0; x<stop; x++) { 
			final int xF = x;
			SwingUtilities.invokeLater(new Runnable() { 
				public void run() { 
			        progressBarF.setValue(xF);
			        log.debug(xF);
			        frame.repaint();
				}
			});
			i = containerLabel.getFields().iterator();
			counter = 0;
			while (i.hasNext()) {
			    StringBuffer text = new StringBuffer(); 
			    text.append("è");  // a unicode character
			    text.append(Integer.toString(counter).charAt(0));
			    for (int j=0; j<x; j++) { 
				   text.append(Integer.toString(j));
			    }
				counter ++;
				i.next().getTextField().setText(text.toString());
			}
			json = containerLabel.toJSON();
			int byteSize = json.getBytes().length;
			log.debug("JSON Length:" + json.length());
			encoder = new LabelEncoder(containerLabel);
			int scaleWidth = 0;
			int scaleHeight = 0;
			int imageNeedsWidth = 0;
			ImageIcon originalIcon = null;
			ImageIcon scaledIcon = null;
			byte[] pdf = null;
			String generationFailure = null;
			String generationFirstError = null;
			try {
				// Create a barcode image
				BufferedImage image = encoder.getBufferedImage();
				originalIcon = new ImageIcon(image);
				// Figure out the scaling of the image into the space available on the PDF.
				imageNeedsWidth = image.getWidth();
				log.debug("Image Needs " + imageNeedsWidth + " pixel width (at 72dpi).");
				log.debug("Available space is " + barcodeCellWidthPoints);
				scaleWidth = (int) (image.getWidth()*(barcodeCellWidthPoints/image.getWidth()));  
				log.debug("Scaled to " + scaleWidth);
				scaleHeight = (int) (image.getHeight()*(barcodeCellWidthPoints/image.getHeight()));
				log.debug("Trying " + scaleWidth + "x" + scaleHeight + " Bytes=" + byteSize);
				BufferedImage rescaledImage = LabelEncoder.resizeImage(image, scaleWidth * 2, scaleHeight *2 ,4);
				scaledIcon = new ImageIcon(new BufferedImage(rescaledImage.getColorModel(), rescaledImage.getRaster(), true, null));
				if (x==0) { 
					final ImageIcon scaledIconF = scaledIcon;
					SwingUtilities.invokeLater(new Runnable() {
						public void run() { 
					        labelImage.setIcon(scaledIconF);
						}
					});
				}
				
				// Rescale the barcode image and write it into a PDF.
				Document document = new Document();
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				PdfWriter.getInstance(document, outputStream);
				Image iTextImage = encoder.getImage();
				iTextImage.scaleToFit(scaleWidth, scaleHeight);
				document.open();
				document.add(iTextImage);
				document.close();
				pdf = outputStream.toByteArray();
			} catch (WriterException wex) { 
				log.debug(wex.getMessage());
				generationFailure = "Generation Failed " + wex.getMessage();
				generationFirstError = "Failed to generate QRCode, too many characters: " + json.length();
			} catch (BarcodeCreationException | DocumentException e) {
				log.debug("Failure to create barcode. Bytes=" + byteSize +" Last " + lastSuccess);
				generationFailure = "Failed " + messageOf(e);
			}
			
			// Convert the PDF page containing the barcode to an image at each resolution, 
			// and test whether the barcode can be read from it.
			for (int r=0; r<RENDER_RESOLUTIONS.length; r++) { 
				int dpi = RENDER_RESOLUTIONS[r];
				BarcodeTestResult testResult = new BarcodeTestResult();
				testResult.setBytes(byteSize);
				testResult.setResolution(dpi);
				testResult.setOriginalWidth(imageNeedsWidth);
				testResult.setScaledToWidth(scaleWidth);
				testResult.setOriginalImage(originalIcon);
				testResult.setScaledImage(scaledIcon);
				String failure = generationFailure;
				if (failure == null) { 
					try { 
						BufferedImage pageImage;
						try (PDDocument pdfDocument = Loader.loadPDF(pdf)) { 
							pageImage = new PDFRenderer(pdfDocument).renderImageWithDPI(0, dpi);
						}
						decodedJson = LabelDecoder.decodeImage(pageImage);
						if (!decodedJson.equals(json)) { 
							failure = "Failed JSON encoded and decoded strings didn't match.";
						}
					} catch (BarcodeReadException | IOException e) { 
						failure = "Failed " + messageOf(e);
					}
				}
				if (failure == null) { 
					successes[r]++;
					lastSuccess = "Success at scale " + scaleWidth + "x" + scaleHeight + " Scaling=" + barcodeCellWidthPoints/imageNeedsWidth + " at " + dpi + " dpi";
					report.append("Success.  Bytes=" + byteSize + " Resolution=" + dpi + " Scaled to Fit=" + scaleWidth + " Original Size= " + imageNeedsWidth ).append("\n");
					log.debug(lastSuccess);
					testResult.setMessage("Success");
				} else { 
					failures[r]++;
					report.append("Failure.  Bytes=" + byteSize + " Resolution=" + dpi + " Scaled to Fit=" + scaleWidth + " Original Size= " + imageNeedsWidth).append(failure).append("\n");
					log.debug("Failure. Bytes=" + byteSize + " Resolution=" + dpi + " Last " + lastSuccess);
					testResult.setMessage(failure);
					if (firstError == null) { 
						firstError = generationFirstError != null ? generationFirstError : "Bytes = " + byteSize + ", " + dpi + " dpi, Last " + lastSuccess;
						final String firstErrorF = firstError;
						SwingUtilities.invokeLater(new Runnable() { 
							public void run() { 
							      labelFirstError.setText(firstErrorF);
							}
						});
					}
				}
			    testResults.addResult(testResult);
			}
			if (generationFirstError != null) { 
				// The payload exceeds the capacity of a QR code, larger payloads will fail 
				// the same way, so stop here.
				log.debug("Stopping at " + byteSize + " bytes, too much data for a QR code.");
				break;
			}
		}
		StringBuilder summary = new StringBuilder();
		for (int r=0; r<RENDER_RESOLUTIONS.length; r++) { 
			if (r > 0) { 
				summary.append("; ");
			}
			summary.append(RENDER_RESOLUTIONS[r]).append(" dpi: successes=").append(successes[r]).append(", failures=").append(failures[r]);
		}
		final String summaryF = summary.toString();
		final BarcodeTestResultsTableModel testResultsF = testResults;
		SwingUtilities.invokeLater(new Runnable() { 
			public void run() { 
		        progressBarF.setValue(stopF);
		        labelResult.setText(summaryF);
		        table.setModel(testResultsF);
		        table.setRowHeight(300);
		        table.doLayout();
			}
		});
		} // end label definition is not null
	}
	
	/**
	 * @param e an exception
	 * @return the message of the exception followed by that of its cause, if any.
	 */
	private static String messageOf(Exception e) { 
		StringBuffer message = new StringBuffer();
		message.append(e.getMessage());
		if (e.getCause()!=null) { 
			message.append(e.getCause().getMessage());
		}
		return message.toString();
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BarcodeTestingFrame frame = new BarcodeTestingFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
