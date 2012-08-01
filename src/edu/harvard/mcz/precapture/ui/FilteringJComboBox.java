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

import edu.harvard.mcz.precapture.data.UnitTrayLabel;
import edu.harvard.mcz.precapture.data.UnitTrayLabelLifeCycle;

/**
 * @author mole
 *
 */
public class FilteringJComboBox extends JComboBox implements FocusListener {
	private static final long serialVersionUID = -7988464282872345110L;
	private static final Log log = LogFactory.getLog(FilteringJComboBox.class);

	
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
    	// listen for loss of focus on the text field
    	this.getEditor().getEditorComponent().addFocusListener(this);
        this.setEditable(true);
        final JTextField textfield = (JTextField) this.getEditor().getEditorComponent();
        textfield.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent keyEvent) {
            	log.debug(keyEvent);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        filter(textfield.getText());
                    }
                });
            }
        });

    }

    public void setUTLModel(UnitTrayLabelComboBoxModel model) { 
    	super.setModel(model);
    }
    
    protected void filter(String enteredText) {
    	if (enteredText==null|| enteredText.length()==0) { 
    		// If entry is blank, show full list.
    		// TODO: Filter by family/genus.
    		UnitTrayLabelLifeCycle uls = new UnitTrayLabelLifeCycle();
    		super.setModel(new UnitTrayLabelComboBoxModel(uls.findAll()));
    	}
        if (!this.isPopupVisible()) {
            this.showPopup();
        }
        log.debug("Filtering on " + enteredText);

        boolean isExactMatch = false;
        UnitTrayLabelComboBoxModel filterArray= new UnitTrayLabelComboBoxModel();
        filterArray.removeAllElements();
        log.debug("Model size: " + super.getModel().getSize());
        for (int i = 0; i < super.getModel().getSize(); i++) {
        	log.debug(((UnitTrayLabelComboBoxModel)super.getModel()).getDataElementAt(i).toString());
            if (((UnitTrayLabelComboBoxModel)super.getModel()).getDataElementAt(i).toString().toLowerCase().contains(enteredText.toLowerCase())) {
            	log.debug("Matched");
                filterArray.addElement(((UnitTrayLabelComboBoxModel)super.getModel()).getDataElementAt(i));
            }
            if (((UnitTrayLabelComboBoxModel)super.getModel()).getDataElementAt(i).toString().equalsIgnoreCase(enteredText)) { 
            	log.debug("Exact Match");
            	isExactMatch = true;
        	    super.getModel().setSelectedItem(((UnitTrayLabelComboBoxModel)super.getModel()).getDataElementAt(i));
            }
        }
        if (filterArray.getSize() > 0) {
        	UnitTrayLabelComboBoxModel model = (UnitTrayLabelComboBoxModel) this.getModel();
            model.removeAllElements();
            Iterator<UnitTrayLabel> i = filterArray.getModel().iterator();
            while (i.hasNext()) { 
                model.addElement(i.next());
            }
            JTextField textfield = (JTextField) this.getEditor().getEditorComponent();
            textfield.setText(enteredText);
            super.setModel(model);
        }
        this.hidePopup();
        if (isExactMatch) { 
            super.firePopupMenuCanceled();
        } else { 
            this.showPopup();
        }
    }

	/* (non-Javadoc)
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusLost(FocusEvent e) {
		// when focus is lost on the text field (editor box part of the combo box),
		// set the value of the text field to the selected item on the list, if any.
		log.debug(e.toString());
		if (super.getModel().getSelectedItem()!=null) { 
		    JTextField textfield = (JTextField) this.getEditor().getEditorComponent();
            textfield.setText(super.getModel().getSelectedItem().toString());
		}
	}
    
}