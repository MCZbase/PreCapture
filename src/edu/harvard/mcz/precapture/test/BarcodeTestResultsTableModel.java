/**
 * BarcodeTestResultsTableModel.java
 * edu.harvard.mcz.precapture.test
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
package edu.harvard.mcz.precapture.test;

import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mole
 *
 */
public class BarcodeTestResultsTableModel extends AbstractTableModel {
	private static final Log log = LogFactory
			.getLog(BarcodeTestResultsTableModel.class);

	private ArrayList<BarcodeTestResult> results;
	
	/** 
	 * Default no argument constructor, constructs a new BarcodeTestResultsTableModel instance.
	 */
	public BarcodeTestResultsTableModel() {
        results = new ArrayList<BarcodeTestResult>();
	}

	public BarcodeTestResultsTableModel(ArrayList<BarcodeTestResult> resultList) { 
		results = resultList;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return results.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 6;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object result = null;
		switch (columnIndex) { 
		case 0:
			result = results.get(rowIndex).getBytes();
			break;
		case 1:
			result = results.get(rowIndex).getOriginalWidth();
			break;
		case 2:
			result = results.get(rowIndex).getScaledToWidth();
			break;
		case 3:
			result = results.get(rowIndex).getMessage();
			break;
		case 4:
			result = results.get(rowIndex).getOriginalImage();
			break;
		case 5:
			result = results.get(rowIndex).getScaledImage();
			break;
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int columnIndex) {
		String result = null;
		switch (columnIndex) { 
		case 0:
			result = "Bytes";
			break;
		case 1:
			result = "InitialWidth";
			break;
		case 2:
			result = "ScaledWidth";
			break;
		case 3:
			result = "ErrorMessage";
			break;
		case 4:
			result = "InitialImage";
			break;
		case 5:
			result = "ScaledImage";
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
		switch (columnIndex) { 
		case 0:
			result = Integer.class;
			break;
		case 1:
			result = Integer.class;
			break;
		case 2:
			result = Integer.class;
			break;
		case 3:
			result = String.class;
			break;
		case 4:
			result =  Icon.class;
			break;
		case 5:
			result = Icon.class;
			break;
		}
		return result;
	}

	/**
	 * @param testResult
	 */
	public void addResult(BarcodeTestResult testResult) {
		results.add(testResult);		
		this.fireTableDataChanged();
	}
}
