/**
 * UnitTrayLabelComboBoxModel.java
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.precapture.data.UnitTrayLabel;
import edu.harvard.mcz.precapture.data.UnitTrayLabelLifeCycle;

/**
 * @author mole
 *
 */
public class UnitTrayLabelComboBoxModel implements ComboBoxModel {
	private static final Log log = LogFactory
			.getLog(UnitTrayLabelComboBoxModel.class);
	
	ArrayList<UnitTrayLabel> model;
	ArrayList<ListDataListener> dataListeners;
	UnitTrayLabel selectedItem = null;

	/** 
	 * Default no argument constructor, constructs a new UnitTrayLabelComboBoxModel instance.
	 */
	public UnitTrayLabelComboBoxModel() {
        model = new ArrayList<UnitTrayLabel>();
        dataListeners = new ArrayList<ListDataListener>();
	}

	public UnitTrayLabelComboBoxModel(List<UnitTrayLabel> unitTrayLabelList) { 
		model = (ArrayList<UnitTrayLabel>) unitTrayLabelList;
		if (model==null) { 
            model = new ArrayList<UnitTrayLabel>();
		}
        dataListeners = new ArrayList<ListDataListener>();
        if (model.size()>0) { 
        	selectedItem = model.get(0);
        }
	}
	
	
	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getSize()
	 */
	@Override
	public int getSize() {
		return model.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	@Override
	public Object getElementAt(int index) {
		return UnitTrayLabelLifeCycle.getScientificName(model.get(index));
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
	 */
	@Override
	public void addListDataListener(ListDataListener l) {
        dataListeners.add(l);
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
	 */
	@Override
	public void removeListDataListener(ListDataListener l) {
        dataListeners.remove(l);
	}

	/* (non-Javadoc)
	 * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
	 */
	@Override
	public void setSelectedItem(Object anItem) {
		Iterator<UnitTrayLabel> i = model.iterator();
		boolean done = false;
		while (i.hasNext() && !done) {
			UnitTrayLabel label = i.next();
			if (UnitTrayLabelLifeCycle.getScientificName(label).equals(anItem)) { 
			    selectedItem = label;
			    done = true;
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.ComboBoxModel#getSelectedItem()
	 */
	@Override
	public Object getSelectedItem() {
		return UnitTrayLabelLifeCycle.getScientificName(selectedItem);
	}
	
	public UnitTrayLabel getSelectedContainerLabel() { 
		return selectedItem;
	}
}
