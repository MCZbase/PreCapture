/**
 * FilteringJComboBox.java
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

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.precapture.PreCaptureProperties;
import edu.harvard.mcz.precapture.PreCaptureSingleton;
import edu.harvard.mcz.precapture.data.UnitTrayLabel;
import edu.harvard.mcz.precapture.data.UnitTrayLabelLifeCycle;

/**
 * @author mole
 *
 */
public class FilteringJComboBox extends JComboBox implements FocusListener {
	private static final long serialVersionUID = -7988464282872345110L;
	private static final Log log = LogFactory.getLog(FilteringJComboBox.class);

	private String familyLimit;
	private String genusLimit;
	
	/** 
	 * Default no argument constructor, constructs a new FilteringJComboBox instance.
	 */
	public FilteringJComboBox() {
        super.setModel(new UnitTrayLabelComboBoxModel());
        init();
	}

	/**
	 * 
	 * 
	 * @param valueList
	 */
    public FilteringJComboBox(UnitTrayLabelComboBoxModel model) {
        super(model);
        init();
    } 
    
    private void init() { 
        familyLimit = "";
        genusLimit = "";
    	// listen for loss of focus on the text field
    	this.getEditor().getEditorComponent().addFocusListener(this);
        this.setEditable(true);
        final JTextField textfield = (JTextField) this.getEditor().getEditorComponent();
        textfield.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent keyEvent) {
            	log.debug(keyEvent);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        filter(textfield.getText(), true);
                    }
                });
            }
        });

    }

    public void setUTLModel(UnitTrayLabelComboBoxModel model) { 
    	super.setModel(model);
    }
    
    public void resetFilter(boolean changePopupState) { 
    	filter(null, changePopupState);
    }
    
    protected void filter(String enteredText, boolean changePopupState) {
    	if (enteredText == null || enteredText.length() == 0) {
    		// If entry is blank, show full list.
    		// TODO: Filter by family/genus.
    		UnitTrayLabelLifeCycle uls = new UnitTrayLabelLifeCycle();
    		if (familyLimit==null && genusLimit==null) { 
    		    super.setModel(new UnitTrayLabelComboBoxModel(uls.findAll()));
    		} else { 
    			UnitTrayLabel pattern = new UnitTrayLabel();
    			if (familyLimit!=null && familyLimit.length()>0) {
    				pattern.setFamily(familyLimit);
    			} else { 
    			   if (genusLimit!=null && genusLimit.length() >0) { 
    				   pattern.setGenus(genusLimit);
    			   }
    			}
    		    super.setModel(new UnitTrayLabelComboBoxModel(uls.findByExample(pattern)));
    		}
    	}
    	if (changePopupState && !this.isPopupVisible()) {
    		this.showPopup();
    	}

    	int lengthThreshold = Integer.valueOf(PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_FILTER_LENGTH_THRESHOLD));
    	if (enteredText != null && enteredText.length() >= lengthThreshold) {

    		log.debug("Filtering on " + enteredText);

    		boolean isExactMatch = false;
    		UnitTrayLabelComboBoxModel filterArray = new UnitTrayLabelComboBoxModel();
    		filterArray.removeAllElements();
    		log.debug("Model size: " + super.getModel().getSize());
    		for (int i = 0; i < super.getModel().getSize(); i++) {
    			log.debug(((UnitTrayLabelComboBoxModel) super.getModel())
    					.getDataElementAt(i).toString());
    			if (((UnitTrayLabelComboBoxModel) super.getModel())
    					.getDataElementAt(i).toString().toLowerCase()
    					.contains(enteredText.toLowerCase())) {
    				log.debug("Matched");
    				filterArray.addElement(((UnitTrayLabelComboBoxModel) super
    						.getModel()).getDataElementAt(i));
    			}
    			if (((UnitTrayLabelComboBoxModel) super.getModel())
    					.getDataElementAt(i).toString()
    					.equalsIgnoreCase(enteredText)) {
    				log.debug("Exact Match");
    				isExactMatch = true;
    				super.getModel().setSelectedItem(
    						((UnitTrayLabelComboBoxModel) super.getModel())
    						.getDataElementAt(i));
    			}
    		}
    		if (filterArray.getSize() > 0) {
    			UnitTrayLabelComboBoxModel model = (UnitTrayLabelComboBoxModel) this
    					.getModel();
    			model.removeAllElements();
    			Iterator<UnitTrayLabel> i = filterArray.getModel().iterator();
    			while (i.hasNext()) {
    				model.addElement(i.next());
    			}
    			JTextField textfield = (JTextField) this.getEditor()
    					.getEditorComponent();
    			textfield.setText(enteredText);
    			super.setModel(model);
    		}
    		if (changePopupState) { 
    		this.hidePopup();
    		if (isExactMatch) {
    			super.firePopupMenuCanceled();
    		} else {
    			this.showPopup();
    		}
    		} 
    	}

    }

	public void focusGained(FocusEvent e) {
		super.getModel().setSelectedItem("");
		JTextField textfield = (JTextField) this.getEditor().getEditorComponent();
        textfield.setText("");
	}

	public void focusLost(FocusEvent e) {
		// when focus is lost on the text field (editor box part of the combo box),
		// set the value of the text field to the selected item on the list, if any.
		log.debug(e.toString());
		if (super.getModel().getSelectedItem()!=null) { 
		    JTextField textfield = (JTextField) this.getEditor().getEditorComponent();
            textfield.setText(super.getModel().getSelectedItem().toString());
		}
	}

	/**
	 * Sets the familial filter limit criterion on the picklist.
	 * 
	 * @param selectedItem the family to limit the picklist to.
	 */
	public void setFamilyLimit(Object selectedFamily) {
		if (selectedFamily!=null && selectedFamily.toString().length()>0) { 
			this.familyLimit=selectedFamily.toString();
			this.genusLimit=null;
		} else { 
			selectedFamily = null;
		}
		resetFilter(false);
	}
	
	/**
	 * Sets the generic filter limit criterion on the picklist.
	 * 
	 * @param selectedGenus the genus to limit the picklist to.
	 */
	public void setGenusLimit(Object selectedGenus) { 
		if (selectedGenus!=null && selectedGenus.toString().length()>0 ) { 
			this.genusLimit=selectedGenus.toString();
			this.familyLimit=null;
		} else { 
			genusLimit = null;
		}
		resetFilter(false);
	}
    
}