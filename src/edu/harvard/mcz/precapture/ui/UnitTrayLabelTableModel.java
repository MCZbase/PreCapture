/**
 * UnitTrayLabelTableModel.java
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
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.precapture.data.UnitTrayLabel;
import edu.harvard.mcz.precapture.data.UnitTrayLabelLifeCycle;

/**
 * @author mole
 *
 */
public class UnitTrayLabelTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -914082823964155138L;
	private static final Log log = LogFactory .getLog(UnitTrayLabelTableModel.class);
	
	private List<UnitTrayLabel> labelList;

	/** 
	 * Default no argument constructor, constructs a new UnitTrayLabelTableModel 
	 * instance, loading the data from UnitTrayLabelLifeCycle.findAll()
	 */
	public UnitTrayLabelTableModel() {
		//labelList = new ArrayList<UnitTrayLabel>();
		UnitTrayLabelLifeCycle uls = new UnitTrayLabelLifeCycle();
		labelList = uls.findAll();
	}
	
	public UnitTrayLabelTableModel(ArrayList<UnitTrayLabel> labelList) { 
		this.labelList = labelList;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return labelList.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 18;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		switch (columnIndex) {  
			case 0:
				return labelList.get(rowIndex).getId();
			case 1:
				return labelList.get(rowIndex).getDrawerNumber();
			case 2:
				return labelList.get(rowIndex).getFamily();
			case 3:
				return labelList.get(rowIndex).getSubfamily();
			case 4:
				return labelList.get(rowIndex).getTribe();
			case 5:
				return labelList.get(rowIndex).getGenus();
			case 6:
				return labelList.get(rowIndex).getSpecificEpithet();
			case 7:
				return labelList.get(rowIndex).getSubspecificEpithet();
			case 8:
				return labelList.get(rowIndex).getInfraspecificEpithet();
			case 9:
				return labelList.get(rowIndex).getInfraspecificRank();
			case 10:
				return labelList.get(rowIndex).getAuthorship();
			case 11:
				return labelList.get(rowIndex).getUnNamedForm();
			case 12:
				return labelList.get(rowIndex).getPrinted();
			case 13:
				return labelList.get(rowIndex).getNumberToPrint();
			case 14:
				return labelList.get(rowIndex).getDateCreated();
			case 15:
				return labelList.get(rowIndex).getDateLastUpdated();
			case 16:
				return labelList.get(rowIndex).getCollection();
			case 17:
				return labelList.get(rowIndex).getOrdinal();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int columnIndex) {
		switch (columnIndex) {  
		case 0:
			return "ID";
		case 1:
			return "DrawerNumber";
		case 2:
			return "Family";
		case 3:
			return "Subfamily";
		case 4:
			return "Tribe";
		case 5:
			return "Genus";
		case 6:
			return "SpecificEpithet";
		case 7:
			return "SubspecificEpithet";
		case 8:
			return "InfraspecificEpithet";
		case 9:
			return "InfraspecificRank";
		case 10:
			return "Authorship";
		case 11:
			return "UnnamedForm";
		case 12:
			return "Printed";
		case 13:
			return "NumberToPrint";
		case 14:
			return "DateCreated";
		case 15:
			return "DateLastUpdated";
		case 16:
			return "Collection";
		case 17:
			return "Ordinal";
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		/**
		private Long id;
		private String drawerNumber;
		private String family;
		private String subfamily;
		private String tribe;
		private String genus;
		private String specificEpithet;
		private String subspecificEpithet;
		private String infraspecificEpithet;
		private String infraspecificRank;
		private String authorship;
		private String unNamedForm;
		private int printed;
		private int numberToPrint;
		private Date dateCreated;
		private Date dateLastUpdated;
		private String collection;  // collection from which the material came
		private Integer ordinal;     // order in which to print
		*/
		switch (columnIndex) {  
		case 0:
			return Integer.class;
		case 1:
			return String.class;
		case 2:
			return  String.class;
		case 3:
			return  String.class;
		case 4:
			return  String.class;
		case 5:
			return  String.class;
		case 6:
			return  String.class;
		case 7:
			return  String.class;
		case 8:
			return  String.class;
		case 9:
			return  String.class;
		case 10:
			return  String.class;
		case 11:
			return  String.class;
		case 12:
			return Integer.class;
		case 13:
			return Integer.class;
		case 14:
			return Date.class;
		case 15:
			return Date.class;
		case 16:
			return String.class;
		case 17:
			return Integer.class;
		}
		return Object.class;
	}
	
	
	
}
