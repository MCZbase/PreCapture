/**
 * FolderPanel.java
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import edu.harvard.mcz.precapture.PreCaptureSingleton;
import edu.harvard.mcz.precapture.encoder.LabelEncoder;
import edu.harvard.mcz.precapture.exceptions.PrintFailedException;

/**
 * JPanel to hold information for printing a single label (e.g. a single folder label).
 * 
 * @author mole
 *
 */
public class FolderPanel extends JPanel {
	private static final Log log = LogFactory.getLog(FolderPanel.class);

	//private ArrayList<FieldPlusText> textFields;
	private ContainerLabel containerLabel;
	
	private JTextField textNumberToPrint;

	/** 
	 * Default no argument constructor, constructs a new FolderPanel instance.
	 */
	public FolderPanel() {
		init();
		
	}
	
	private void init() { 
		this.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_2 = new JPanel();
		this.add(panel_2, BorderLayout.NORTH);
		panel_2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_2.setBackground(Color.WHITE);
		panel_2.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				ColumnSpec.decode("186px"),
				ColumnSpec.decode("61px:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("15px"),}));
		
		JLabel lblFilterTaxaBy = new JLabel("Filter Taxa by:");
		panel_2.add(lblFilterTaxaBy, "2, 2");
		
		JLabel lblFamily = new JLabel("Family");
		panel_2.add(lblFamily, "3, 2, right, default");
		
		JComboBox comboBox = new JComboBox();
		panel_2.add(comboBox, "4, 2, fill, default");
		
		JLabel lblNewLabel = new JLabel("Genus");
		panel_2.add(lblNewLabel, "3, 4, right, default");
		
		JComboBox comboBox_1 = new JComboBox();
		panel_2.add(comboBox_1, "4, 4, fill, default");
		
		
		int fieldCount = PreCaptureSingleton.getInstance().getMappingList().getFieldInList().size();
		
		ArrayList<RowSpec> rowSpec = new ArrayList<RowSpec>();
	    rowSpec.add(FormFactory.RELATED_GAP_ROWSPEC);
	    rowSpec.add(FormFactory.DEFAULT_ROWSPEC);
	    for (int i=0; i<=fieldCount; i++) { 
	        rowSpec.add(FormFactory.RELATED_GAP_ROWSPEC);
	        rowSpec.add(FormFactory.DEFAULT_ROWSPEC);
	    }
	    rowSpec.add(FormFactory.RELATED_GAP_ROWSPEC);
	    rowSpec.add(FormFactory.DEFAULT_ROWSPEC);
		
		JPanel panel_3 = new JPanel();
		this.add(panel_3, BorderLayout.CENTER);
		panel_3.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			    (RowSpec[]) rowSpec.toArray((RowSpec[])Array.newInstance(RowSpec.class, rowSpec.size()))
			));
		
		JLabel lblTaxon = new JLabel("Taxon");
		panel_3.add(lblTaxon, "2, 2, right, default");
		
		JComboBox comboBox_2 = new JComboBox();
		panel_3.add(comboBox_2, "4, 2, 3, 1, fill, default");
	
		ArrayList<FieldPlusText> textFields = new ArrayList<FieldPlusText>(); 
		
	    for (int i=0; i<fieldCount; i++) { 
	    	
	    	textFields.add(new FieldPlusText(PreCaptureSingleton.getInstance().getMappingList().getFieldInList().get(i), new JTextField()));
		
	    	int col = 2 + (i+1) * 2;
	    	
		    JLabel label = new JLabel(textFields.get(i).getField().getLabel());
		    panel_3.add(label, "2, " + Integer.toString(col) + ", right, default");
		
		    textFields.get(i).getTextField().setColumns(10);
		    panel_3.add(textFields.get(i).getTextField(), "4, " + Integer.toString(col) +  " , 3, 1, fill, default");
		
	    } 

	    ContainerLabel folderLabel = new ContainerLabel(textFields);
	    
		JLabel lblPrintCount = new JLabel("Number To Print");
	    int col = 2 + (fieldCount+1) * 2;
		panel_3.add(lblPrintCount, "2, "+ Integer.toString(col)+", right, default");
		
		textNumberToPrint = new JTextField();
		panel_3.add(textNumberToPrint, "4, "+ Integer.toString(col)+", 3, 1, fill, default");
		
		containerLabel = new ContainerLabel(textFields);

	}
	
	public int getNumberToPrint() { 
		int result = 1;
		try { 
		    result = Integer.parseInt(textNumberToPrint.getText());
		} catch (Exception e) { 
			// not initialized or
			// non integer in numberToPrint 
		}
		return result;
	}
	
	public void invokePrintOne() { 
	    try {
	    	//TODO: See: http://stackoverflow.com/questions/4609667/how-to-print-a-pdf-created-with-itext
	    	containerLabel.setNumberToPrint(getNumberToPrint());
	    	ArrayList<ContainerLabel> containers = new ArrayList<ContainerLabel>();
	    	containers.add(containerLabel);
			LabelEncoder.printList(containers);
		} catch (PrintFailedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void invokeAddToList() { 
	    containerLabel.setNumberToPrint(getNumberToPrint());
		PreCaptureSingleton.getInstance().getCurrentLabelList().addLabelToList(containerLabel.clone());
		containerLabel.resetToBlank();
	}
	
	/** 
	 * Reset all of the text field values to empty strings.
	 */
	public void resetValues() { 
		
		//int fieldCount = PreCaptureSingleton.getInstance().getMappingList().getFieldInList().size();
		
		int fieldCount = containerLabel.getFields().size();
	    for (int i=0; i<fieldCount; i++) { 
	    	containerLabel.getFields().get(i).getTextField().setText("");
	    }
		
	}
}
