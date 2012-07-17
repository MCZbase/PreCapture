/**
 * InventoryTableModel.java
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

import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.precapture.data.Inventory;

/**
 * @author mole
 *
 */
public class InventoryTableModel extends AbstractTableModel {
	
	private static final Log log = LogFactory.getLog(InventoryTableModel.class);
	private static final long serialVersionUID = 8493794928548892989L;

	private ArrayList<Inventory> inventoryList;
	
	/** 
	 * Default no argument constructor, constructs a new InventoryTableModel instance.
	 */
	public InventoryTableModel() {
		inventoryList = new ArrayList<Inventory>();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return inventoryList.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 4;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object result = null;
		switch (columnIndex) { 
		case 0:
			result = inventoryList.get(rowIndex).getCabinet();
			break;
		case 1:
			result = inventoryList.get(rowIndex).getTaxon();
			break;
		case 2:
			result = inventoryList.get(rowIndex).getThickness();
			break;
		case 3:
			result = inventoryList.get(rowIndex).getSheetsPerUnitThickness();
			break;
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		String result = "";
		switch (column) { 
		case 0:
			result = "Cabinet";
			break;
		case 1:
			result = "Taxon";
			break;
		case 2:
			result = "Inches of New England";
			break;
		case 3:
			result = "Sheets per inch";
			break;
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
		// TODO Auto-generated method stub
		return super.isCellEditable(rowIndex, columnIndex);
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
