/**
 * ContainerEntryPanel.java
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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import edu.harvard.mcz.precapture.PreCaptureProperties;
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
public class ContainerEntryPanel extends JPanel {
	
	private static final long serialVersionUID = -7835985799794074075L;

	private static final Log log = LogFactory.getLog(ContainerEntryPanel.class);

	//private ArrayList<FieldPlusText> textFields;
	private ContainerLabel containerLabel;
	private FilteringJComboBox comboBoxTaxonPicker;
	private JTextField textNumberToPrint;
	private ArrayList<FieldPlusText> textFields;
	private JComboBox comboBoxFamilyFilter;
	private JComboBox comboBoxGenericFilter;

	/** 
	 * Default no argument constructor, constructs a new ContainerEntryPanel instance.
	 */
	public ContainerEntryPanel() {
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
				if (! comboBoxFamilyFilter.getSelectedItem().equals("")) { 
					comboBoxTaxonPicker.setSelectedItem("");
					((JTextField)comboBoxGenericFilter.getEditor().getEditorComponent()).setText("");
				    comboBoxGenericFilter.setSelectedItem("");
				}
				comboBoxFamilyFilter.grabFocus();
			}
		});
		
		JLabel lblNewLabel = new JLabel("Genus");
		panel_2.add(lblNewLabel, "3, 4, right, default");
		
		comboBoxGenericFilter = new JComboBox(uls.findDistinctGenera(true).toArray());
		comboBoxGenericFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comboBoxTaxonPicker.setGenusLimit(comboBoxGenericFilter.getSelectedItem());
				if (!comboBoxGenericFilter.getSelectedItem().equals("")) { 
					comboBoxTaxonPicker.setSelectedItem("");
					((JTextField)comboBoxFamilyFilter.getEditor().getEditorComponent()).setText("");
					comboBoxFamilyFilter.setSelectedItem("");
				}
				comboBoxGenericFilter.grabFocus();
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
		log.debug("About to create model for taxon picker");
		comboBoxTaxonPicker.setUTLModel(new UnitTrayLabelComboBoxModel(uls.findAll(),true));
		log.debug("Created model for taxon picker");
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
				extractData(utl);
				if (textNumberToPrint.getText().isEmpty()) { 
					textNumberToPrint.setText("1");
				}
			}
		});
		panel_3.add(comboBoxTaxonPicker, "4, 2, 3, 1, fill, default");
	
		log.debug("Iterating through field list");
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
		    if (textFields.get(i).getField().getDefaultValue().size()>1) { 
		    	// Combo box would go here.  
		    	// also would need handling in the bit that is extracting data from the field list
		    	// Probably means that FieldsPlusText needs to include a combobox as well as a textfield.
	        } 
		    
		    
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
		textNumberToPrint.setText(Integer.toString(containerLabel.getNumberToPrint()));
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
	
	/**
	 * Extract the data from a selected picklist item (a UnitTrayLabel instance) 
	 * into the form fields by vocabulary term.
	 * 
	 * Contains the hardcoded mapping between supported vocabulary terms and the
	 * fields in the taxon authority file (UnitTrayLabel).
	 * 
	 * @param utl
	 */
	private void extractData(UnitTrayLabel utl) { 
		boolean useQuadranomials = false;
		boolean putSubspeciesInInfra = false;
		if (PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_USEQUADRANOMIALS).equals("true")) { 
			useQuadranomials = true;
		}
		if (PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_PUT_SUBSPECIES_IN_INFRA).equals("true")) { 
			putSubspeciesInInfra = true;
		}
		if (utl!=null) { 
			Iterator<FieldPlusText> i = textFields.iterator();
			while (i.hasNext()) { 
				FieldPlusText field = i.next();
				log.debug(field.getField().getVocabularyTerm());
				if (field.getField().getVocabularyTerm().equalsIgnoreCase("dwc:family")) { 
					field.getTextField().setText(utl.getFamily());
				}
				if (field.getField().getVocabularyTerm().equalsIgnoreCase("hTaxon:subfamily")) { 
					field.getTextField().setText(utl.getSubfamily());
				}
				if (field.getField().getVocabularyTerm().equalsIgnoreCase("hTaxon:tribe")) { 
					field.getTextField().setText(utl.getTribe());
				}
				if (field.getField().getVocabularyTerm().equalsIgnoreCase("dwc:genus")) { 
					field.getTextField().setText(utl.getGenus());
				}
				if (field.getField().getVocabularyTerm().equalsIgnoreCase("dwc:specificEpithet")) { 
					field.getTextField().setText(utl.getSpecificEpithet());
				}
				if (useQuadranomials) { 
					// put subspecies into subspecies concept, put infraspecific name and rank into infra fields.
					if (field.getField().getVocabularyTerm().equalsIgnoreCase("abcd:SubspeciesEpithet")) { 
						field.getTextField().setText(utl.getSubspecificEpithet());
					}
					if (field.getField().getVocabularyTerm().equalsIgnoreCase("hispid:isprk")) { 
						field.getTextField().setText(utl.getInfraspecificRank());
					}
					if (field.getField().getVocabularyTerm().equalsIgnoreCase("dwc:infraspecificEpithet")) { 
						field.getTextField().setText(utl.getInfraspecificEpithet());
					}
				} else if (putSubspeciesInInfra) {
					// Assemble a trinomial.
					// if there is an infraspecific name, don't include the subspecies.
					// if there isnt't an infraspecific name, put subspecies into the subspecies fields
					String rank = utl.getInfraspecificRank();
					String epithet = utl.getInfraspecificEpithet();
					if (utl.getSubspecificEpithet()!=null && utl.getSubspecificEpithet().length()>0 && (utl.getInfraspecificEpithet()==null || utl.getInfraspecificEpithet().length()==0)) { 
						rank = PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_TRINOMIAL_SUBSP_RANK);
						// rank = "subspecies";
						epithet = utl.getSubspecificEpithet();
					}
					if (rank==null) { rank = ""; } 
					if (epithet==null) { epithet = ""; }
					if (field.getField().getVocabularyTerm().equalsIgnoreCase("hispid:isprk")) { 
						field.getTextField().setText(rank);
					}
					if (field.getField().getVocabularyTerm().equalsIgnoreCase("dwc:infraspecificEpithet")) { 
						field.getTextField().setText(epithet);
					}
					if (field.getField().getVocabularyTerm().equalsIgnoreCase("dwc:infraspecificRank")) { 
						field.getTextField().setText(rank);
					}
				} else {
					// Assemble a trinomial.
					// if there is an infraspecific name, don't include the subspecies.
					// if there isnt't an infraspecific name, put subspecies into the infra fields.
					String rank = utl.getInfraspecificRank();
					String epithet = utl.getInfraspecificEpithet();
					if (utl.getSubspecificEpithet()!=null && utl.getSubspecificEpithet().length()>0 && (utl.getInfraspecificEpithet()==null || utl.getInfraspecificEpithet().length()==0)) { 
						// subspecies has a value, infraspecific name does not, put subspecies into subspecies, leave out rank.
						if (field.getField().getVocabularyTerm().equalsIgnoreCase("abcd:SubspeciesEpithet")) { 
							field.getTextField().setText(utl.getSubspecificEpithet());
						}
					} else { 
						// there is a value in infraspecific name and rank, leave out subspecies if quadranomial,
						// but if rank is subspecies, put subspecies in infraspecific into subspecies field.
						if (rank==null) { rank = ""; } 
						if (epithet==null) { epithet = ""; }
						String subspeciesRank = PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_TRINOMIAL_SUBSP_RANK);
						if (rank.equals(subspeciesRank)) { 
							// if rank is subspecies, put infra name into subspecies
							if (field.getField().getVocabularyTerm().equalsIgnoreCase("abcd:SubspeciesEpithet")) { 
							    field.getTextField().setText(epithet);
							}
						} else { 
							// put infra name and rank into infra name and rank fields.
							if (field.getField().getVocabularyTerm().equalsIgnoreCase("hispid:isprk")) { 
								field.getTextField().setText(rank);
							} 
							if (field.getField().getVocabularyTerm().equalsIgnoreCase("dwc:infraspecificEpithet")) { 
								field.getTextField().setText(epithet);
							}
							if (field.getField().getVocabularyTerm().equalsIgnoreCase("dwc:infraspecificRank")) { 
								field.getTextField().setText(rank);
							}
						}
					}
				}
				if (field.getField().getVocabularyTerm().equalsIgnoreCase("dwc:scientificNameAuthorship")) { 
					field.getTextField().setText(utl.getAuthorship());
				}
				
				// PJM: 2012Sept12: preventing this block from running for now.
				if (0==1 && field.getField().getVocabularyTerm().equalsIgnoreCase("dwc:scientificName")) {
					// PJM 2012 Sept 12
					// Was a todo item: Handle putting hybrids into the scientific name field
					// Discussion with Patrick suggests that the NEVP data set will only contain 
					// nothotaxa (named hybrids), and it will be up to individual institutions 
					// to capture hybrids between taxa.
                    // Hybrids need more refinement downstream as well in Symbiota, so posponing work
					// on them for now.
					
					// TODO: Add logic to include either atomic concepts or full scientific name, 
					// but not both.
					
					// Support for assembly of full name into dwc:scientificName, if used.
					StringBuffer assembledName = new StringBuffer();
					assembledName.append(utl.getGenus()).append(" "); 
					assembledName.append(utl.getSpecificEpithet()).append(" "); 
				    if (utl.getInfraspecificEpithet().isEmpty() || PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_USEQUADRANOMIALS).equals("true")) { 
					    assembledName.append(utl.getSubspecificEpithet()).append(" "); 
				    }
					assembledName.append(utl.getInfraspecificRank()).append(" "); 
					assembledName.append(utl.getInfraspecificEpithet()).append(" "); 
					String aName = assembledName.toString().trim();
					// TODO: This regex isn't removing duplicated spaces.
					aName = aName.replaceAll("/ +/", " ");
					field.getTextField().setText(aName);
				}
			}
		}
	}
	
	/**
	 * Given a UnitTrayLabel, populate a list of FieldPlusText with the data mapped over by vocabulary term.
	 * 
	 * @param utl UnitTrayLabel to obtain data from
	 * @return an ArrayList of FieldPlusText containing data matched in UnitTrayLabel by vocabulary term.
	 */
	public static ArrayList<FieldPlusText> extractDataToList(UnitTrayLabel utl) {
		ArrayList<FieldPlusText> textFieldsOut = new ArrayList<FieldPlusText>();
		int fieldCount = PreCaptureSingleton.getInstance().getMappingList().getFieldInList().size();
		for (int j=0; j<fieldCount; j++) {
			FieldPlusText f = new FieldPlusText(PreCaptureSingleton.getInstance().getMappingList().getFieldInList().get(j), new JTextField());
			textFieldsOut.add(f);
		}
		boolean useQuadranomials = false;
		boolean putSubspeciesInInfra = false;
		if (PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_USEQUADRANOMIALS).equals("true")) { 
			useQuadranomials = true;
		}
		if (PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_PUT_SUBSPECIES_IN_INFRA).equals("true")) { 
			putSubspeciesInInfra = true;
		}		
		if (utl!=null) { 
			Iterator<FieldPlusText> i = textFieldsOut.iterator();
			while (i.hasNext()) { 
				FieldPlusText field = i.next();
				log.debug(field.getField().getVocabularyTerm());
				if (field.getField().getVocabularyTerm().equalsIgnoreCase("dwc:family")) { 
					field.getTextField().setText(utl.getFamily());
					log.debug(field.getField().getVocabularyTerm());
					log.debug(utl.getFamily());
				}
				if (field.getField().getVocabularyTerm().equalsIgnoreCase("hTaxon:subfamily")) { 
					field.getTextField().setText(utl.getSubfamily());
				}
				if (field.getField().getVocabularyTerm().equalsIgnoreCase("hTaxon:tribe")) { 
					field.getTextField().setText(utl.getTribe());
				}
				if (field.getField().getVocabularyTerm().equalsIgnoreCase("abcd:PreviousUnit/Owner")) { 
					field.getTextField().setText(utl.getCollection());
				}
				if (field.getField().getVocabularyTerm().equalsIgnoreCase("obo:OBI_0000967")) { 
					field.getTextField().setText(utl.getDrawerNumber());
				}				
				if (field.getField().getVocabularyTerm().equalsIgnoreCase("dwc:genus")) { 
					field.getTextField().setText(utl.getGenus());
				}
				if (field.getField().getVocabularyTerm().equalsIgnoreCase("dwc:specificEpithet")) { 
					field.getTextField().setText(utl.getSpecificEpithet());
				}
				if (useQuadranomials) { 
					// put subspecies into subspecies concept, put infraspecific name and rank into infra fields.
					if (field.getField().getVocabularyTerm().equalsIgnoreCase("abcd:SubspeciesEpithet")) { 
						field.getTextField().setText(utl.getSubspecificEpithet());
					}
					if (field.getField().getVocabularyTerm().equalsIgnoreCase("hispid:isprk")) { 
						field.getTextField().setText(utl.getInfraspecificRank());
					}
					if (field.getField().getVocabularyTerm().equalsIgnoreCase("dwc:infraspecificEpithet")) { 
						field.getTextField().setText(utl.getInfraspecificEpithet());
					}
				} else if (putSubspeciesInInfra){
					// Assemble a trinomial.
					// if there is an infraspecific name, don't include the subspecies.
					// if there isnt't an infraspecific name, put subspecies into the infra fields.
					String rank = utl.getInfraspecificRank();
					String epithet = utl.getInfraspecificEpithet();
					if (utl.getSubspecificEpithet()!=null && utl.getSubspecificEpithet().length()>0 && (utl.getInfraspecificEpithet()==null || utl.getInfraspecificEpithet().length()==0)) { 
						rank = PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_TRINOMIAL_SUBSP_RANK);
						//rank = "subspecies";
						epithet = utl.getSubspecificEpithet();
					}
					if (rank==null) { rank = ""; } 
					if (epithet==null) { epithet = ""; }
					if (field.getField().getVocabularyTerm().equalsIgnoreCase("hispid:isprk")) { 
						field.getTextField().setText(rank);
					}
					if (field.getField().getVocabularyTerm().equalsIgnoreCase("dwc:infraspecificEpithet")) { 
						field.getTextField().setText(epithet);
					}
					if (field.getField().getVocabularyTerm().equalsIgnoreCase("dwc:infraspecificRank")) { 
						field.getTextField().setText(rank);
					}					
				} else { 
					// Assemble a trinomial.
					// if there is an infraspecific name, don't include the subspecies.
					// if there isnt't an infraspecific name, put subspecies into the infra fields.
					String rank = utl.getInfraspecificRank();
					String epithet = utl.getInfraspecificEpithet();
					if (utl.getSubspecificEpithet()!=null && utl.getSubspecificEpithet().length()>0 && (utl.getInfraspecificEpithet()==null || utl.getInfraspecificEpithet().length()==0)) { 
						// subspecies has a value, infraspecific name does not, put subspecies into subspecies, leave out rank.
						if (field.getField().getVocabularyTerm().equalsIgnoreCase("abcd:SubspeciesEpithet")) { 
							field.getTextField().setText(utl.getSubspecificEpithet());
						}
					} else { 
						// there is a value in infraspecific name and rank, leave out subspecies if quadranomial,
						// but if rank is subspecies, put subspecies in infraspecific into subspecies field.
						if (rank==null) { rank = ""; } 
						if (epithet==null) { epithet = ""; }
						String subspeciesRank = PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_TRINOMIAL_SUBSP_RANK);
						if (rank.equals(subspeciesRank)) { 
							// if rank is subspecies, put infra name into subspecies
							if (field.getField().getVocabularyTerm().equalsIgnoreCase("abcd:SubspeciesEpithet")) { 
							    field.getTextField().setText(epithet);
							}
						} else { 
							// put infra name and rank into infra name and rank fields.
							if (field.getField().getVocabularyTerm().equalsIgnoreCase("hispid:isprk")) { 
								field.getTextField().setText(rank);
							} 
							if (field.getField().getVocabularyTerm().equalsIgnoreCase("dwc:infraspecificEpithet")) { 
								field.getTextField().setText(epithet);
							}
							if (field.getField().getVocabularyTerm().equalsIgnoreCase("dwc:infraspecificRank")) { 
								field.getTextField().setText(rank);
							}
						}
					}					
				}
				if (field.getField().getVocabularyTerm().equalsIgnoreCase("dwc:scientificNameAuthorship")) { 
					field.getTextField().setText(utl.getAuthorship());
				}
				
				if (field.getField().getVocabularyTerm().equalsIgnoreCase("dwc:scientificName")) {
					// Support for assembly of full name into dwc:scientificName, if used.
					StringBuffer assembledName = new StringBuffer();
					assembledName.append(utl.getGenus()).append(" "); 
					assembledName.append(utl.getSpecificEpithet()).append(" "); 
				    if (utl.getInfraspecificEpithet().isEmpty() || PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_USEQUADRANOMIALS).equals("true")) { 
					    assembledName.append(utl.getSubspecificEpithet()).append(" "); 
				    }
					assembledName.append(utl.getInfraspecificRank()).append(" "); 
					assembledName.append(utl.getInfraspecificEpithet()).append(" "); 
					String aName = assembledName.toString().trim();
					// TODO: This regex isn't removing duplicated spaces.
					aName = aName.replaceAll("/ +/", " ");
					field.getTextField().setText(aName);
				}
			}
		}
		return textFieldsOut;
	}
	
}
