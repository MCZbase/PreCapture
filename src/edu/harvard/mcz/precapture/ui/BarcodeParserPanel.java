package edu.harvard.mcz.precapture.ui;

import javax.swing.JPanel;

import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.GridBagConstraints;

import javax.swing.JTextArea;

import java.awt.Insets;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import edu.harvard.mcz.precapture.PreCaptureProperties;
import edu.harvard.mcz.precapture.PreCaptureSingleton;
import edu.harvard.mcz.precapture.decoder.LabelDecoder;
import edu.harvard.mcz.precapture.encoder.LabelEncoder;
import edu.harvard.mcz.precapture.encoder.PrintingUtility;
import edu.harvard.mcz.precapture.exceptions.PrintFailedException;
import edu.harvard.mcz.precapture.xml.labels.LabelDefinitionListType;
import edu.harvard.mcz.precapture.xml.labels.LabelDefinitionType;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;

public class BarcodeParserPanel extends JPanel {
	
	private static final Log log = LogFactory.getLog(BarcodeParserPanel.class);
	
	private JTextField textField;
	private JTextArea textArea;
	
	private ContainerLabel parsedBarcodeDataForLabel;

	/**
	 * Create the panel.
	 */
	public BarcodeParserPanel() {
		
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[] {0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblScanBarcode = new JLabel("Scan Barcode:");
		GridBagConstraints gbc_lblScanBarcode = new GridBagConstraints();
		gbc_lblScanBarcode.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblScanBarcode.insets = new Insets(0, 0, 5, 5);
		gbc_lblScanBarcode.gridx = 0;
		gbc_lblScanBarcode.gridy = 0;
		add(lblScanBarcode, gbc_lblScanBarcode);
		
		textArea = new JTextArea();
		textArea.setRows(3);
		textArea.setColumns(6);
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.gridheight = 2;
		gbc_textArea.insets = new Insets(0, 0, 5, 0);
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 1;
		gbc_textArea.gridy = 0;
		add(textArea, gbc_textArea);
		
		JButton btnParse = new JButton("Parse");
		btnParse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Convert from JSON to ContanerLabel object and display the
				// scientific name found in  JSON
				log.debug(textArea.getText());
				parsedBarcodeDataForLabel = LabelDecoder.createLabelFromJson(textArea.getText());
				log.debug(parsedBarcodeDataForLabel.metadataToJSON());
				if (parsedBarcodeDataForLabel!=null && parsedBarcodeDataForLabel.getFields()!=null) { 
				    // TODO: Scientific name may not be populated, build from other DarwinCore terms if not.
				    for (int i=0; i<parsedBarcodeDataForLabel.getFields().size(); i++) {
					    if (parsedBarcodeDataForLabel.getFields().get(i).getField().getVocabularyTerm().equals("dwc:scientificName")) {
						    textField.setText(parsedBarcodeDataForLabel.getFields().get(i).getTextField().getText());
					    }
				    }
				    // TODO: Authorship
				}
			}
		});
		GridBagConstraints gbc_btnParse = new GridBagConstraints();
		gbc_btnParse.insets = new Insets(0, 0, 5, 5);
		gbc_btnParse.gridx = 0;
		gbc_btnParse.gridy = 1;
		add(btnParse, gbc_btnParse);
		
		JLabel lblScientificName = new JLabel("Scientific Name:");
		GridBagConstraints gbc_lblScientificName = new GridBagConstraints();
		gbc_lblScientificName.anchor = GridBagConstraints.EAST;
		gbc_lblScientificName.insets = new Insets(0, 0, 5, 5);
		gbc_lblScientificName.gridx = 0;
		gbc_lblScientificName.gridy = 2;
		add(lblScientificName, gbc_lblScientificName);
		
		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 2;
		add(textField, gbc_textField);
		textField.setColumns(10);
		
		JButton btnPrintToAnnotation = new JButton("Print To Annotation Label");
		btnPrintToAnnotation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Print the ContainerLabel extracted from the JSON on an Annotation label.
				
				// TODO: Add pick of label definition to form, here simply reusing configured definition.
				
				// TODO: Refactor the code that follows in this method into LabelEncoder.printAnnotationLabels()
				
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
					log.error("No selected print format defintion found.");
					//TODO change from message to error handling dialog that allows picking a print format.
					JOptionPane.showMessageDialog(null, "Unable to print.  No print format is selected.");
				} else {
					Document document = new Document();
					// TODO: Refactor units code, simply copying and pasting here, generalize a method inside LabelEncoder
					
					try {
						PdfWriter.getInstance(document, new FileOutputStream(PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_LABELPRINTFILE)));
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (DocumentException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
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
						paperHeightPoints = (int)Math.floor(printDefinition.getPaperHeight()*72f);
						marginsPoints = (int)Math.floor(printDefinition.getMargins()*72f);
						labelWidthPoints = (int)Math.floor(printDefinition.getLabelWidth()*72f);
						labelHeightPoints = (int)Math.floor(printDefinition.getLabelHeight()*72f);
					}
					if (printDefinition.getUnits().toString().toLowerCase().equals("cm")) { 
						paperWidthPoints = (int)Math.floor(printDefinition.getPaperWidth()*28.346456f);
						paperHeightPoints = (int)Math.floor(printDefinition.getPaperHeight()*28.346456f);
						marginsPoints = (int)Math.floor(printDefinition.getMargins()*28.346456f);
						labelWidthPoints = (int)Math.floor(printDefinition.getLabelWidth()*28.346456f);
						labelHeightPoints = (int)Math.floor(printDefinition.getLabelHeight()*28.346456f);
					}
					if (printDefinition.getUnits().toString().toLowerCase().equals("points")) { 
						paperWidthPoints = (int)Math.floor(printDefinition.getPaperWidth()*1f);
						paperHeightPoints = (int)Math.floor(printDefinition.getPaperHeight()*1f);
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
					
					try {
						// Having set up a table to hold label bits, populate and print.
						
						// TODO: May not work as expected yet, as only one cell is added, not the barcode cell as well...
				
						PdfPTable table = LabelEncoder.setupTable(paperWidthPoints, marginsPoints, labelWidthPoints, 1, 1, 3f,2f);
				        table.addCell(parsedBarcodeDataForLabel.toPDFCell(printDefinition));
				        document.add(table);
				        
						document.close();
						
						// send to printer
						PrintingUtility.sendPDFToPrinter(printDefinition, paperWidthPoints, paperHeightPoints);				        
				        
					} catch (DocumentException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (PrintFailedException e1) {
						log.error(e1.getMessage(), e1);
						// TODO: Uncomment on refactoring back into LabelEncoder.printAnnotationLabels()
						//throw new PrintFailedException("Error building/printing PDF document. " + e1.getMessage());
					}
				}
			}
		});
		GridBagConstraints gbc_btnPrintToAnnotation = new GridBagConstraints();
		gbc_btnPrintToAnnotation.anchor = GridBagConstraints.EAST;
		gbc_btnPrintToAnnotation.gridx = 1;
		gbc_btnPrintToAnnotation.gridy = 3;
		add(btnPrintToAnnotation, gbc_btnPrintToAnnotation);

	}
}
