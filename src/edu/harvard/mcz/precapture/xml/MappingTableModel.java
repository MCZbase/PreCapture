/**
 * MappingTableModel.java
 * edu.harvard.mcz.precapture.xml
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
package edu.harvard.mcz.precapture.xml;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mole
 *
 */
public class MappingTableModel extends AbstractTableModel {
	private static final Log log = LogFactory.getLog(MappingTableModel.class);

	private MappingList mappingList;
	
	/** 
	 * Default no argument constructor, constructs a new MappingTableModel instance.
	 */
	public MappingTableModel() {

	}
	
	/**
	 * Constructor that takes a mapping list for the table model instance, 
	 * displays the field mappings from this MappingList instance.
	 * 
	 * @param mappingList
	 */
	public MappingTableModel(MappingList mappingList) { 
		this.mappingList = mappingList;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		int result = 0;
		if (mappingList!=null) { 
			result = mappingList.getFieldInList().size();
		}
		return result;
	}

	/**
	 * Returns the number of properties present in Field. 
	 * 
	 * @see edu.harvard.mcz.precapture.xml.Field
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		// number of properties in Field
		return 5;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object result = null;
		if (mappingList!=null) {
			if (getRowCount()>0) { 
				try { 
			    switch (columnIndex) {  
			    case 0: 
			    	result = mappingList.getFieldInList().get(rowIndex).getLabel();
			    	break;
			    case 1: 
			    	result = mappingList.getFieldInList().get(rowIndex).getCode();
			    	break;
			    case 2: 
			    	result = mappingList.getFieldInList().get(rowIndex).getType();
			    	break;
			    case 3: 
			    	result = mappingList.getFieldInList().get(rowIndex).isOptional();
			    	break;
			    case 4: 
			    	result = mappingList.getFieldInList().get(rowIndex).getVocabularyTerm();
			    	break;
			    }
				} catch (Exception e) { 
					// asked for something that isn't there
				}
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int columnIndex) {
		String result =  super.getColumnName(columnIndex);
	    switch (columnIndex) {  
	    case 0: 
	    	result = "Label";
	    	break;
	    case 1: 
	    	result = "Code";
	    	break;
	    case 2: 
	    	result = "Type";
	    	break;
	    case 3: 
	    	result = "Display";
	    	break;
	    case 4: 
	    	result = "Vocabulary";
	    	break;
	    }		
		return result;
	}

	
}
