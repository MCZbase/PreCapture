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
import java.util.Iterator;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.precapture.PreCaptureProperties;
import edu.harvard.mcz.precapture.PreCaptureSingleton;
import edu.harvard.mcz.precapture.data.UnitTrayLabel;
import edu.harvard.mcz.precapture.data.UnitTrayLabelLifeCycle;
import edu.harvard.mcz.precapture.exceptions.SaveFailedException;

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
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_EDITABLE_AUTHORITY_FILE).equals("true")) {
			if (columnIndex==12 || columnIndex==14 || columnIndex ==15) { 
				return false;
			}
			return true;
		} else { 
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		try { 
			switch (columnIndex) {  
			case 0:
				labelList.get(rowIndex).setId(Long.valueOf(aValue.toString()));
				break;
			case 1:
				labelList.get(rowIndex).setDrawerNumber(aValue.toString());
				break;
			case 2:
				labelList.get(rowIndex).setFamily(aValue.toString());
				break;
			case 3:
				labelList.get(rowIndex).setSubfamily(aValue.toString());
				break;
			case 4:
				labelList.get(rowIndex).setTribe(aValue.toString());
				break;
			case 5:
				labelList.get(rowIndex).setGenus(aValue.toString());
				break;
			case 6:
				labelList.get(rowIndex).setSpecificEpithet(aValue.toString());
				break;
			case 7:
				labelList.get(rowIndex).setSubspecificEpithet(aValue.toString());
				break;
			case 8:
				labelList.get(rowIndex).setInfraspecificEpithet(aValue.toString());
				break;
			case 9:
				labelList.get(rowIndex).setInfraspecificRank(aValue.toString());
				break;
			case 10:
				labelList.get(rowIndex).setAuthorship(aValue.toString());
				break;
			case 11:
				labelList.get(rowIndex).setUnNamedForm(aValue.toString());
				break;
			case 12:
				labelList.get(rowIndex).setPrinted(Integer.parseInt(aValue.toString()));
				break;
			case 13:
				labelList.get(rowIndex).setNumberToPrint(Integer.parseInt(aValue.toString()));
				break;
			case 14:
				//labelList.get(rowIndex).setDateCreated(aValue.toString());
				break;
			case 15:
				//labelList.get(rowIndex).setDateLastUpdated(aValue.toString());
				break;
			case 16:
				labelList.get(rowIndex).setCollection(aValue.toString());
				break;
			case 17:
				labelList.get(rowIndex).setOrdinal(Integer.parseInt(aValue.toString()));
				break;
			}
			UnitTrayLabelLifeCycle uls = new UnitTrayLabelLifeCycle();
			uls.merge(labelList.get(rowIndex));
		} catch (java.lang.NumberFormatException e) { 
			log.error(e.getMessage());
		} catch (SaveFailedException e) {
			log.error(e.getMessage(),e);
		}
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
	
	/**
	 * Set the value of the print flag/to be printed counter for all rows 
	 * in the taxon authority table (unit tray label list) to a specified value.
	 * 
	 * @param value value to set the print flag to, 0 means none.
	 */
	public void setAllPrintFlagsTo(int value) {
		UnitTrayLabelLifeCycle uls = new UnitTrayLabelLifeCycle();
		Iterator<UnitTrayLabel> i = labelList.iterator();
		while (i.hasNext()) { 
			UnitTrayLabel label = i.next();
			if (label.getNumberToPrint() != value) { 
			   label.setNumberToPrint(value);
			   try {
				   uls.merge(label);
			   } catch (SaveFailedException e) {
				   log.error(e.getMessage());
			   }
			}
		}
		this.fireTableDataChanged();
	}
	
	/**
	 * Add a row to the taxon authority table (unit tray label list).
	 */
	public void addRow() { 
		UnitTrayLabelLifeCycle uls = new UnitTrayLabelLifeCycle();
		UnitTrayLabel label = new UnitTrayLabel();
		label.setDateCreated(new Date());
		try {
			uls.persist(label);
		    labelList.add(label);
		    this.fireTableDataChanged();
		} catch (SaveFailedException e) {
			log.error(e.getMessage());
		}
	}
	
	/**
	 * Add all taxon authority table (unit tray label) records that have a print flag set to 
	 * 1 or higher to the list of containers for printing.  This copies records from the taxon
	 * authority table to the current print list.
	 * 
	 * @param rowSorter 
	 */
	public int addPrintFlaggedToContainerList(RowSorter<? extends TableModel> rowSorter) { 
		int added = 0;
		UnitTrayLabelLifeCycle uls = new UnitTrayLabelLifeCycle();
		Iterator<UnitTrayLabel> i = labelList.iterator();
		if (rowSorter!=null) {
			ArrayList<UnitTrayLabel> toAdd = new ArrayList<UnitTrayLabel>(rowSorter.getViewRowCount());
			while (toAdd.size()<rowSorter.getViewRowCount()) { toAdd.add(null); }
			int index = 0;  // row number in model
			while (i.hasNext()) { 
				if (rowSorter.convertRowIndexToView(index)>=0) { 
				   toAdd.remove(rowSorter.convertRowIndexToView(index));
				   toAdd.add(rowSorter.convertRowIndexToView(index),i.next());
				   log.debug(toAdd.get(rowSorter.convertRowIndexToView(index)).getGenus());
				}
				index++;
			}
			i = null;
			for (int j=0; j< toAdd.size(); j++) { 
				if (toAdd.get(j)==null) { toAdd.remove(j); }
			}			
			i = toAdd.iterator();
		}
		while (i.hasNext()) { 
			UnitTrayLabel utl = i.next();
			if (utl!=null && utl.getNumberToPrint()>0) { 
				ArrayList<FieldPlusText> textFields = ContainerEntryPanel.extractDataToList(utl);
				ContainerLabel newLabel = new ContainerLabel(textFields, false);
				log.debug(newLabel.toJSON());
				newLabel.setNumberToPrint(utl.getNumberToPrint());
				PreCaptureSingleton.getInstance().getCurrentLabelList().addLabelToList(newLabel);
				utl.setPrinted(utl.getPrinted() + utl.getNumberToPrint());;
				utl.setNumberToPrint(0);
		        try {
					uls.merge(utl);
					added++;
				} catch (SaveFailedException e) {
					log.error(e.getMessage());
				}
			}
		}
		this.fireTableDataChanged();
		return added;
	}
	
}
