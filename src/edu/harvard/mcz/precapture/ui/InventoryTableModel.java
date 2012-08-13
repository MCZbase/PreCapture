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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.precapture.data.Inventory;
import edu.harvard.mcz.precapture.data.InventoryLifeCycle;
import edu.harvard.mcz.precapture.data.UnitTrayLabelLifeCycle;
import edu.harvard.mcz.precapture.exceptions.SaveFailedException;

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
	
	public InventoryTableModel(ArrayList<Inventory> list) { 
		inventoryList = list;
	}

	public int getRowCount() {
		return inventoryList.size();
	}

	public int getColumnCount() {
		return 4;
	}

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
		Class<?> result = null;
		Inventory inv = new Inventory();
		switch (columnIndex) { 
		case 0:
			result = inv.getCabinet().getClass();
			break;
		case 1:
			result = inv.getTaxon().getClass();
			break;
		case 2:
			result = Float.class; // inv.getThickness().getClass();
			break;
		case 3:
			result = Float.class; //inv.getSheetsPerUnitThickness().getClass();
			break;
		}
		return result;
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
		try {
			switch (columnIndex) { 
			case 0:
				inventoryList.get(rowIndex).setCabinet((String)aValue);
				break;
			case 1:
				inventoryList.get(rowIndex).setTaxon((String)aValue);
				break;
			case 2:
				inventoryList.get(rowIndex).setThickness(((Float)aValue).floatValue());
				break;
			case 3:
				inventoryList.get(rowIndex).setSheetsPerUnitThickness(((Float)aValue).floatValue());
				break;
			}
			saveList();
		} catch (ClassCastException ex) {
			// TODO alert user
			ex.printStackTrace();
		} catch (SaveFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean saveList() throws SaveFailedException { 
		boolean result = false;
		
		InventoryLifeCycle ils = new InventoryLifeCycle();
		
		Iterator<Inventory> i = inventoryList.iterator();
		while (i.hasNext()) { 
			ils.attachDirty(i.next());
		}
		
		return result;
	}
	
    

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#fireTableDataChanged()
	 */
	@Override
	public void fireTableDataChanged() {
		super.fireTableDataChanged();
		try {
			saveList();
		} catch (SaveFailedException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public void addRow() {
		log.debug("Adding new Inventory record");
		log.debug("List size:" + inventoryList.size());
		Inventory newInventoryRecord = new Inventory();
		if (inventoryList.size()>0) { 
	        newInventoryRecord.setTaxon(inventoryList.get(inventoryList.size()-1).getTaxon());
		} 
		inventoryList.add(newInventoryRecord);
		log.debug("List size:" + inventoryList.size());
		fireTableDataChanged();
	}
	
	public void writeToFile(File filename) throws SaveFailedException {
		log.debug("Writing inventory to " + filename.getName());
		if (!filename.exists() || filename.canWrite()) { 
			try {
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(filename));
				StringBuffer line = new StringBuffer();
				line.append("\"").append("Cabinet").append("\",");
				line.append("\"").append("Taxon").append("\",");
				line.append("\"").append("Thickness").append("\",");
				line.append("\"").append("SheetsPerUnitThickness").append("\"\r\n");
				stream.write(line.toString().getBytes());
				Iterator<Inventory> i = inventoryList.iterator();
				while (i.hasNext()) {
					Inventory inventory = i.next();
					line = new StringBuffer();
					line.append("\"").append(inventory.getCabinet().replace("\"", "\"\"")).append("\",");
					line.append("\"").append(inventory.getTaxon().replace("\"", "\"\"")).append("\",");
					line.append("\"").append(Float.toString(inventory.getThickness())).append("\",");
					line.append("\"").append(Float.toString(inventory.getSheetsPerUnitThickness())).append("\"\r\n");
					stream.write(line.toString().getBytes());
				}
				stream.close();
			} catch (FileNotFoundException e) {
				log.error(e.getMessage());
				throw new SaveFailedException("Unable to write to file " + filename.getName() + e.getMessage());
			} catch (IOException e) {
				log.error(e.getMessage());
				throw new SaveFailedException("Unable to write to file " + filename.getName() + e.getMessage());
			}
		}
	}

	/**
	 * Creates a new row with the taxon and cabinet of another row.
	 * 
	 * @param clickedOnRow
	 */
	public void cloneRow(int clickedOnRow) {
		Inventory inventory =  inventoryList.get(clickedOnRow);
		Inventory newInventoryRecord = new Inventory();
		newInventoryRecord.setCabinet(inventory.getCabinet());
		newInventoryRecord.setTaxon(inventory.getTaxon());
		inventoryList.add(newInventoryRecord);
		fireTableDataChanged();
	}

	/**
	 * @param clickedOnRow
	 * @throws SaveFailedException 
	 */
	public void deleteRow(int clickedOnRow) throws SaveFailedException {
		InventoryLifeCycle ils = new InventoryLifeCycle();
		ils.delete(inventoryList.get(clickedOnRow));
		inventoryList.get(clickedOnRow);
	    inventoryList.remove(clickedOnRow);
		fireTableDataChanged();
	}
	
}
