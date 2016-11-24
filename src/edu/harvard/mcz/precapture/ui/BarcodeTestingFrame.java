/**
 * BarcodeTestingFrame.java
 * edu.harvard.mcz.precapture.ui
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
package edu.harvard.mcz.precapture.ui;


import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.Cursor;
import java.awt.Rectangle;
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
import com.jgoodies.forms.factories.FormFactory;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

import edu.harvard.mcz.precapture.PreCaptureProperties;
import edu.harvard.mcz.precapture.PreCaptureSingleton;
import edu.harvard.mcz.precapture.decoder.LabelDecoder;
import edu.harvard.mcz.precapture.encoder.LabelEncoder;
import edu.harvard.mcz.precapture.exceptions.BarcodeCreationException;
import edu.harvard.mcz.precapture.exceptions.BarcodeReadException;
import edu.harvard.mcz.precapture.test.BarcodeTestResult;
import edu.harvard.mcz.precapture.test.BarcodeTestResultsTableModel;
import edu.harvard.mcz.precapture.xml.labels.LabelDefinitionListType;
import edu.harvard.mcz.precapture.xml.labels.LabelDefinitionType;

import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
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
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
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
		
		int successes = 0;
		int failures = 0;
		
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
		BarcodeTestResultsTableModel testResults = new BarcodeTestResultsTableModel();
		for (int x=0; x<stop; x++) { 
			BarcodeTestResult testResult = new BarcodeTestResult();
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
			int byteSize = 0;
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
			byteSize = json.getBytes().length;
			testResult.setBytes(byteSize);
			log.debug("JSON Length:" + json.length());
			encoder = new LabelEncoder(containerLabel);
			int scaleWidth = 0;
			int imageNeedsWidth = 0;
			String errorMessage = null;
			try {
				// Create a barcode image
				BufferedImage image = encoder.getBufferedImage();
				testResult.setOriginalImage(new ImageIcon(image));
				// Figure out the scaling of the image into the space available on the PDF.
				imageNeedsWidth = image.getWidth();
				log.debug("Image Needs " + imageNeedsWidth + " pixel width (at 72dpi).");
				log.debug("Available space is " + barcodeCellWidthPoints);
				testResult.setOriginalWidth(imageNeedsWidth);
				
				scaleWidth = (int) (image.getWidth()*(barcodeCellWidthPoints/image.getWidth()));  
				log.debug("Scaled to " + scaleWidth);
				testResult.setScaledToWidth(scaleWidth);
				int scaleHeight = (int) (image.getHeight()*(barcodeCellWidthPoints/image.getHeight()));
				log.debug("Trying " + scaleWidth + "x" + scaleHeight + " Bytes=" + byteSize);
				BufferedImage rescaledImage = LabelEncoder.resizeImage(image, scaleWidth * 2, scaleHeight *2 ,4);
				
				// Rescale the barcode image and write it into a PDF.
				Document document = new Document();
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				PdfWriter.getInstance(document, outputStream);
				
				Image iTextImage = encoder.getImage();
				iTextImage.scaleToFit(scaleWidth, scaleHeight);
				document.open();
				document.add(iTextImage);
				document.close();
				
				// Convert the PDF page containing the barcode to an image
		        ByteBuffer byteBuffer = ByteBuffer.wrap(outputStream.toByteArray());
				PDFFile pdfFile = new PDFFile(byteBuffer);
		        PDFPage page = pdfFile.getPage(1);
		        Rectangle rect = new Rectangle(0, 0, (int)page.getBBox().getWidth(), (int)page.getBBox().getHeight());
		        java.awt.Image pageImage = page.getImage(rect.width, rect.height, rect, null, true, true);	
				ImageIcon rescaledIcon = new ImageIcon(pageImage);
				
				// Test to see if the result of the round trip to PDF is readable.
				ImageIcon rescaledIconLocal = new ImageIcon(new BufferedImage(rescaledImage.getColorModel(), rescaledImage.getRaster(), true, null));
				testResult.setScaledImage(rescaledIconLocal);
				if (x==0) { 
				   //labelImage.setIcon(new ImageIcon(rescaledImage));
					final ImageIcon rescaledIconLocalF = rescaledIconLocal;
					SwingUtilities.invokeLater(new Runnable() {
						public void run() { 
					        labelImage.setIcon(rescaledIconLocalF);
						}
					});
				}
				BufferedImage toDecode = (BufferedImage) rescaledIcon.getImage();
				decodedJson = LabelDecoder.decodeImage(toDecode);
				if (decodedJson.equals(json)) { 
				   successes ++;
				   lastSuccess = "Success at scale " + scaleWidth + "x" + scaleHeight + " Scaling=" + barcodeCellWidthPoints/image.getWidth();
				   report.append("Success.  Bytes=" + byteSize +  " Scaled to Fit=" + scaleWidth + " Original Size= " + imageNeedsWidth ).append("\n");
				   log.debug(lastSuccess);
				   testResult.setMessage("Success");
				} else { 
					throw new BarcodeReadException("JSON encoded and decoded strings didn't match.");
				}
			} catch (WriterException wex) { 
				log.debug(wex.getMessage());
				report.append("Generation Failure.  Bytes=" + byteSize +  " Scaled to Fit=" + scaleWidth + " Original Size= " + imageNeedsWidth).append(wex.getMessage()).append("\n");
				testResult.setMessage("Generation Failed " + wex.getMessage());
				failures++;
				if (failures==1) { 
					errorMessage="Failed to generate QRCode, too many characters: " + json.length();
				}
			} catch (BarcodeReadException e) {
				report.append("Failure.  Bytes=" + byteSize +  " Scaled to Fit=" + scaleWidth + " Original Size= " + imageNeedsWidth).append(e.getMessage()).append("\n");
				log.debug("Failure. Bytes=" + byteSize +" Last " + lastSuccess);
				StringBuffer message = new StringBuffer();
				message.append(e.getMessage());
				if (e.getCause()!=null) { 
					message.append(e.getCause().getMessage());
				}
				testResult.setMessage("Failed " + message);
				failures++;
				if (failures==1) { 
					errorMessage = "Bytes = " + byteSize + ", Last " + lastSuccess;
				}
			} catch (BarcodeCreationException e) {
				report.append("Failure.  Bytes=" + byteSize +  " Scaled to Fit=" + scaleWidth + " Original Size= " + imageNeedsWidth).append(e.getMessage()).append("\n");
				log.debug("Failure to create barcode. Bytes=" + byteSize +" Last " + lastSuccess);
				StringBuffer message = new StringBuffer();
				message.append(e.getMessage());
				if (e.getCause()!=null) { 
					message.append(e.getCause().getMessage());
				}
				testResult.setMessage("Failed " + message);
				failures++;
				if (failures==1) { 
					errorMessage = "Bytes = " + byteSize + ", Last " + lastSuccess;
				}
			} catch (IOException e) {
				report.append("Failure [IO Exception].  Bytes=" + byteSize +  " Scaled to Fit=" + scaleWidth + " Original Size= " + imageNeedsWidth).append(e.getMessage()).append("\n");
				log.debug("Failure to create barcode. Bytes=" + byteSize +" Last " + lastSuccess);
				StringBuffer message = new StringBuffer();
				message.append(e.getMessage());
				if (e.getCause()!=null) { 
					message.append(e.getCause().getMessage());
				}
				testResult.setMessage("Failed " + message);
				failures++;
				if (failures==1) { 
					errorMessage = "Bytes = " + byteSize + ", Last " + lastSuccess;
				}
			} catch (DocumentException e) {
				report.append("Failure [document exception].  Bytes=" + byteSize +  " Scaled to Fit=" + scaleWidth + " Original Size= " + imageNeedsWidth).append(e.getMessage()).append("\n");
				log.debug("Failure to create barcode. Bytes=" + byteSize +" Last " + lastSuccess);
				StringBuffer message = new StringBuffer();
				message.append(e.getMessage());
				if (e.getCause()!=null) { 
					message.append(e.getCause().getMessage());
				}
				testResult.setMessage("Failed " + message);
				failures++;
				if (failures==1) { 
				     errorMessage = "Bytes = " + byteSize + ", Last " + lastSuccess;
				}
			} finally {
				if (errorMessage!=null) {
					final String errorMessageF = errorMessage;
				SwingUtilities.invokeLater(new Runnable() { 
					public void run() { 
					      labelFirstError.setText(errorMessageF);
					}
				});
				}
			}
		    testResults.addResult(testResult);
		}
		final int successesF = successes;
		final int failuresF = failures;
		final BarcodeTestResultsTableModel testResultsF = testResults;
		SwingUtilities.invokeLater(new Runnable() { 
			public void run() { 
		        progressBarF.setValue(stopF);
		        labelResult.setText("Successes=" + successesF + ", failures=" + failuresF);
		        table.setModel(testResultsF);
		        table.setRowHeight(300);
		        table.doLayout();
			}
		});
		} // end label definition is not null
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
