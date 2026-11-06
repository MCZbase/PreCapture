/**
 * ContainerListTableModel.java
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

import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.precapture.PreCaptureSingleton;
import edu.harvard.mcz.precapture.data.Inventory;
import edu.harvard.mcz.precapture.data.InventoryLifeCycle;
import edu.harvard.mcz.precapture.data.UnitTrayLabel;
import edu.harvard.mcz.precapture.exceptions.SaveFailedException;

/**
 * A list of container labels, each of which has a number to print and a list of fields.
 * 
 * A ContainerListTableModel is a list of ContainerLabel, each of which has a list of FieldPlusText 
 * and a number to print.
 * 
 * A ContainerLabel corresponds with a MappingList, and each Field in the MappingList
 * corresponds with a FieldPlusText.  
 * 
 * @author mole
 *
 */
public class ContainerListTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = -4269045283222334588L;

	private static final Log log = LogFactory.getLog(ContainerListTableModel.class);

	private ArrayList<ContainerLabel> labels;
	
	private ContainerLabel exampleItem;
	private int fieldCount = 0;
	
	/** 
	 * Default no argument constructor, constructs a new ContainerListTableModel instance.
	 */
	public ContainerListTableModel() {
        labels = new ArrayList<ContainerLabel>();
		ArrayList<FieldPlusText> textFields = new ArrayList<FieldPlusText>(); 
        fieldCount = PreCaptureSingleton.getInstance().getMappingList().getFieldInList().size();
	    for (int i=0; i<fieldCount; i++) { 
	    	textFields.add(new FieldPlusText(PreCaptureSingleton.getInstance().getMappingList().getFieldInList().get(i), new JTextField()));
	    } 

	    exampleItem = new ContainerLabel(textFields);
        
	}
	

	/**
	 * @return the labels
	 */
	public ArrayList<ContainerLabel> getLabels() {
		return labels;
	}
	
	/**
	 * Return a copy of the labels in a sorted, filtered order.
	 * 
	 * @param rowSorter row sorter by which to sort and filter the labels
	 * @return
	 */
	public ArrayList<ContainerLabel> getSortedLabels(RowSorter<? extends TableModel> rowSorter) {
		if (rowSorter==null) {
			return labels;
		}
		ArrayList<ContainerLabel> sortLabels = new ArrayList<ContainerLabel>(rowSorter.getViewRowCount());
		while (sortLabels.size()<rowSorter.getViewRowCount()) { sortLabels.add(null); }
		Iterator<ContainerLabel> i = labels.iterator();
		int index = 0;
		while (i.hasNext()) { 
			ContainerLabel label = i.next();
			if (rowSorter.convertRowIndexToView(index)>=0) { 
			   sortLabels.add(rowSorter.convertRowIndexToView(index),label);
			}
			index++;
		}
		for (int j=0; j< sortLabels.size(); j++) { 
			if (sortLabels.get(j)==null) { sortLabels.remove(j); }
		}
		return sortLabels;
	}

	
	/** Adds a a container label to the list and fires table data changed.
	 * 
	 * @param label
	 */
	public void addLabelToList(ContainerLabel label) { 
		this.getLabels().add(label);
		this.fireTableDataChanged();
	}
	
	/** Removes all labels currently on list and fires table data changed.
	 * 
	 */
	public void clearList() { 
		labels = new ArrayList<ContainerLabel>();
		this.fireTableDataChanged();
	}

	public int getRowCount() {
		return labels.size();
	}

	public int getColumnCount() {
		return PreCaptureSingleton.getInstance().getMappingList().getFieldInList().size() + 1;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		// Determine if this is a column in the mapping list or the number to print column.
		Object result = labels.get(rowIndex).getNumberToPrint();
		if (columnIndex < PreCaptureSingleton.getInstance().getMappingList().getFieldInList().size()) { 
		    result = labels.get(rowIndex).getFields().get(columnIndex).getTextField().getText();
		}
		return result;
	}


	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		// Determine if this is a column in the mapping list or the number to print column.
		String result = "Number To Print";
		if (column < PreCaptureSingleton.getInstance().getMappingList().getFieldInList().size()) { 
			result = exampleItem.getFields().get(column).getField().getLabel();
		}
		return result;
	}


	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}


	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}


	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// Determine if this is a column in the mapping list or the number to print column.
		if (columnIndex < PreCaptureSingleton.getInstance().getMappingList().getFieldInList().size()) { 
		     labels.get(rowIndex).getFields().get(columnIndex).getTextField().setText(aValue.toString());
		} else { 
			 labels.get(rowIndex).setNumberToPrint(Integer.valueOf(aValue.toString()));
		}
	}
	
	/**
	 * Creates a new row with the taxon and cabinet of another row.
	 * 
	 * @param clickedOnRow
	 */
	public void addRow() {
		ContainerLabel newContainerRecord = new ContainerLabel();
        fieldCount = PreCaptureSingleton.getInstance().getMappingList().getFieldInList().size();
	    for (int i=0; i<fieldCount; i++) { 
	    	newContainerRecord.getFields().add(new FieldPlusText(PreCaptureSingleton.getInstance().getMappingList().getFieldInList().get(i), new JTextField()));
	    } 
		labels.add(newContainerRecord);
		fireTableDataChanged();
	}	
	
	/**
	 * Creates a new row with the taxon and cabinet of another row.
	 * 
	 * @param clickedOnRow
	 */
	public void cloneRow(int clickedOnRow) {
		ContainerLabel container =  labels.get(clickedOnRow);
		ContainerLabel newContainerRecord = new ContainerLabel();
		newContainerRecord.setNumberToPrint(container.getNumberToPrint());
		Iterator<FieldPlusText> i = container.getFields().iterator();
		while(i.hasNext()) {
			FieldPlusText f = i.next();
			newContainerRecord.getFields().add(f.clone());
		} 
		labels.add(newContainerRecord);
		fireTableDataChanged();
	}

	/**
	 * @param clickedOnRow
	 * @throws SaveFailedException 
	 */
	public void deleteRow(int clickedOnRow) throws SaveFailedException {
		//InventoryLifeCycle ils = new InventoryLifeCycle();
		//ils.delete(inventoryList.get(clickedOnRow));
		labels.get(clickedOnRow);
	    labels.remove(clickedOnRow);
		fireTableDataChanged();
	}
	
	
}
