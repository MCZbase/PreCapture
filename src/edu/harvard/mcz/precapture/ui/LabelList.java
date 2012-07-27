/**
 * LabelList.java
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

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.precapture.PreCaptureSingleton;

/**
 * A list of container labels, each of which has a number to print and a list of fields.
 * 
 * A LabelList is a list of ContainerLabel, each of which has a list of FieldPlusText.
 * 
 * A ContainerLabel corresponds with a MappingList, and each Field in the MappingList
 * corresponds with a FieldPlusText.
 * 
 * @author mole
 *
 */
public class LabelList extends AbstractTableModel {
	
	private static final long serialVersionUID = -4269045283222334588L;

	private static final Log log = LogFactory.getLog(LabelList.class);

	private ArrayList<ContainerLabel> labels;
	
	private ContainerLabel exampleItem;
	private int fieldCount = 0;
	
	/** 
	 * Default no argument constructor, constructs a new LabelList instance.
	 */
	public LabelList() {
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

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return labels.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return PreCaptureSingleton.getInstance().getMappingList().getFieldInList().size() + 1;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
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
		// TODO Auto-generated method stub
		return super.getColumnClass(columnIndex);
	}


	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		//TODO: make editable (all?)
		return false;
	}


	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		super.setValueAt(aValue, rowIndex, columnIndex);
	}
	
	
}
