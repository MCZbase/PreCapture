/**
 * FolderEntryPanel.java
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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.FocusManager;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.text.DefaultEditorKit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import edu.harvard.mcz.precapture.PreCaptureSingleton;
import edu.harvard.mcz.precapture.data.UnitTrayLabel;
import edu.harvard.mcz.precapture.data.UnitTrayLabelLifeCycle;
import edu.harvard.mcz.precapture.encoder.LabelEncoder;
import edu.harvard.mcz.precapture.exceptions.PrintFailedException;

/**
 * JPanel to hold information for printing a single container label (e.g. a single folder label).
 * 
 * @author mole
 *
 */
public class FolderEntryPanel extends JPanel {
	
	private static final long serialVersionUID = -7835985799794074075L;

	private static final Log log = LogFactory.getLog(FolderEntryPanel.class);

	//private ArrayList<FieldPlusText> textFields;
	private ContainerLabel containerLabel;
	private FilteringJComboBox comboBoxTaxonPicker;
	private JTextField textNumberToPrint;
	private ArrayList<FieldPlusText> textFields;
	private JComboBox comboBoxFamilyFilter;
	private JComboBox comboBoxGenericFilter;

	/** 
	 * Default no argument constructor, constructs a new FolderEntryPanel instance.
	 */
	public FolderEntryPanel() {
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
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("15px"),}));
		
		JLabel lblFilterTaxaBy = new JLabel("Filter Taxa by:");
		panel_2.add(lblFilterTaxaBy, "2, 2");
		
		JLabel lblFamily = new JLabel("Family");
		panel_2.add(lblFamily, "3, 2, right, default");
		
		UnitTrayLabelLifeCycle uls = new UnitTrayLabelLifeCycle();
		comboBoxFamilyFilter = new JComboBox(uls.findDistinctFamilies(true).toArray());
		panel_2.add(comboBoxFamilyFilter, "4, 2, fill, default");
		comboBoxFamilyFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comboBoxTaxonPicker.setFamilyLimit(comboBoxFamilyFilter.getSelectedItem());
				comboBoxTaxonPicker.setSelectedItem("");
			}
		});
		
		JLabel lblNewLabel = new JLabel("Genus");
		panel_2.add(lblNewLabel, "3, 4, right, default");
		
		comboBoxGenericFilter = new JComboBox(uls.findDistinctGenera(true).toArray());
		comboBoxGenericFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: Filter by genus
				comboBoxTaxonPicker.setGenusLimit(comboBoxGenericFilter.getSelectedItem());
				comboBoxTaxonPicker.setSelectedItem("");
			}
		});
		panel_2.add(comboBoxGenericFilter, "4, 4, fill, default");
		
		
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
		
		comboBoxTaxonPicker = new FilteringJComboBox();
		comboBoxTaxonPicker.setUTLModel(new UnitTrayLabelComboBoxModel(uls.findAll()));
		comboBoxTaxonPicker.setEditable(true);
		// if list has names with too many characters, the width of the combo box forces the panel to be
		// too large to work effectively as part of a split pane, so limit to a reasonable size.
		comboBoxTaxonPicker.setMaximumSize(new Dimension(300,comboBoxTaxonPicker.getMaximumSize().height));
		comboBoxTaxonPicker.setPreferredSize(new Dimension(300,comboBoxTaxonPicker.getPreferredSize().height));
		comboBoxTaxonPicker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log.debug(e.getActionCommand());
				comboBoxTaxonPicker.getSelectedIndex();
				UnitTrayLabel utl = ((UnitTrayLabelComboBoxModel)comboBoxTaxonPicker.getModel()).getSelectedContainerLabel();
				log.debug(utl.getSpecificEpithet());
				Iterator<FieldPlusText> i = textFields.iterator();
				while (i.hasNext()) { 
					FieldPlusText field = i.next();
					log.debug(field.getField().getVocabularyTerm());
					if (field.getField().getVocabularyTerm().equalsIgnoreCase("dwc:genus")) { 
						field.getTextField().setText(utl.getGenus());
					}
					if (field.getField().getVocabularyTerm().equalsIgnoreCase("dwc:specificEpithet")) { 
						field.getTextField().setText(utl.getSpecificEpithet());
					}
					if (field.getField().getVocabularyTerm().equalsIgnoreCase("hispid:isprk")) { 
						field.getTextField().setText(utl.getInfraspecificRank());
					}
					if (field.getField().getVocabularyTerm().equalsIgnoreCase("dwc:infraspecificEpithet")) { 
						field.getTextField().setText(utl.getInfraspecificEpithet());
					}
					if (field.getField().getVocabularyTerm().equalsIgnoreCase("dwc:scientificNameAuthorship")) { 
						field.getTextField().setText(utl.getAuthorship());
					}
				}
				if (textNumberToPrint.getText().isEmpty()) { 
					textNumberToPrint.setText("1");
				}
			}
		});
		panel_3.add(comboBoxTaxonPicker, "4, 2, 3, 1, fill, default");
	
		textFields = new ArrayList<FieldPlusText>(); 
	
		char[] keys = {'1','2','3','4','5','6','7','8','9','0'};
		
	    for (int i=0; i<fieldCount; i++) { 
	    	
	    	textFields.add(new FieldPlusText(PreCaptureSingleton.getInstance().getMappingList().getFieldInList().get(i), new JTextField()));
		
	    	int col = 2 + (i+1) * 2;
	    	
	    	String focusKey = "";
	    	if (i<10) { 
	    		 focusKey = " " + keys[i];
	    	}
	    	
		    JLabel label = new JLabel(textFields.get(i).getField().getLabel() + focusKey);
		    if (i<10) { 
		       textFields.get(i).getTextField().setFocusAccelerator(keys[i]);
		       label.setDisplayedMnemonic(keys[i]);
		    }
		    
		    // TODO: Look to see if the field has a list of default values, if so use
		    // a combo box instead of a text field.  
		    
		    textFields.get(i).getTextField().setColumns(10);
		    panel_3.add(label, "2, " + Integer.toString(col) + ", right, default");
		    panel_3.add(textFields.get(i).getTextField(), "4, " + Integer.toString(col) +  " , 3, 1, fill, default");
		
	    } 

	    //ContainerLabel folderLabel = new ContainerLabel(textFields);
	    
		JLabel lblPrintCount = new JLabel("Number To Print");
		lblPrintCount.setDisplayedMnemonic('N');
	    int col = 2 + (fieldCount+1) * 2;
		panel_3.add(lblPrintCount, "2, "+ Integer.toString(col)+", right, default");
		
		textNumberToPrint = new JTextField();
		textNumberToPrint.setFocusAccelerator('N');
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
	
	/**
	 * Produce a PDF document containing the current containerLabel and send it to the printer.
	 */
	public void invokePrintOne() { 
	    try {
	    	containerLabel.setNumberToPrint(getNumberToPrint());
	    	ArrayList<ContainerLabel> containers = new ArrayList<ContainerLabel>();
	    	containers.add(containerLabel);
			LabelEncoder.printList(containers);
		} catch (PrintFailedException e1) {
			JOptionPane.showMessageDialog(this, "Printing failed. " + e1.getMessage(), "Print Failed error.", JOptionPane.ERROR_MESSAGE);
			log.error(e1.getMessage());
		}
	}
	
	/**
	 * Add the current container label to the list of container labels for printing, then
	 * reset the fields on the UI to blank.
	 */
	public void invokeAddToList() { 
	    containerLabel.setNumberToPrint(getNumberToPrint());
		PreCaptureSingleton.getInstance().getCurrentLabelList().addLabelToList(containerLabel.clone());
		containerLabel.resetToBlank();
	}
	
	/** 
	 * Reset all of the text field values to empty strings, or defaults if provided.
	 */
	public void resetValues() { 
		
		//int fieldCount = PreCaptureSingleton.getInstance().getMappingList().getFieldInList().size();
		
		int fieldCount = containerLabel.getFields().size();
	    for (int i=0; i<fieldCount; i++) { 
	    	containerLabel.getFields().get(i).getTextField().setText("");
	    	log.debug(containerLabel.getFields().get(i).getField().getDefaultValue().size());
	    	if (containerLabel.getFields().get(i).getField().getDefaultValue().size()==1) { 
	    		containerLabel.getFields().get(i).getTextField().setText(
	    				containerLabel.getFields().get(i).getField().getDefaultValue().get(0)
	    				);
	    		log.debug(containerLabel.getFields().get(i).getField().getDefaultValue().get(0));
	    	}
	    }
		
	}
}
