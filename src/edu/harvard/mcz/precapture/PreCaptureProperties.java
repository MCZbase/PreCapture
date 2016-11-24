/**
 * PreCaptureProperties.java
 * edu.harvard.mcz.precapture
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
 * Author: Paul J. Morris
 */
package edu.harvard.mcz.precapture;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mole
 *
 */
public class PreCaptureProperties extends AbstractTableModel {
	
	private static final long serialVersionUID = -3965995641119155635L;
	private static final Log log = LogFactory.getLog(PreCaptureProperties.class);

	public static final String DEFAULT_FILENAME = "PreCapture.properties";
	
	/** Key for properties file for paper size. */
	public static final String KEY_PAPERSIZE = "print.papersize";
	/** Key for properties file for location of field mapping xml file. */
	public static final String KEY_FIELDMAPPING = "config.fieldmap";
	/** Key for properties file for whether authority file is shown. */
	public static final String KEY_DISPLAY_AUTHORITY_FILE =  "config.displayauthorityfile";	
	/** Key for properties file for whether authority file is editable. */
	public static final String KEY_EDITABLE_AUTHORITY_FILE =  "config.editableauthorityfile";
	/** Key for properties file for whether the inventory tab should be shown in the UI. */
	public static final String KEY_SHOW_INVENTORY = "config.showinventorytab";
	/** Key for properties file for location of print layout definition xml file. */
	public static final String KEY_PRINTDEFINITIONS = "config.printdefinitions";
	/** Key for properties file for location of print layout definition xml file. */
	public static final String KEY_SELECTED_PRINT_DEFINITION = "config.selectedprintdefinition";
	/** Key for properties file for which main frame variant to use. */
	public static final String KEY_MAINFRAME = "config.mainuiframe";
	/** Key for properties file for the dwc:collection code to use by default.  */
	public static final String KEY_MY_COLLECTION_CODE = "config.mycollectioncode";
	/** Key for properties file for the minimum number of characters needed before filtering a
	 * filtering combo box (Taxon name combo box).
	 */
	public static final String KEY_FILTER_LENGTH_THRESHOLD = "config.filterlengththreshold";
	/** Value to use for QRCode error correction level in hint to Zxing in encoding. */
	public static final String KEY_QRCODEECLEVEL = "config.qr_errorcorrection";
	/** Key for properties file for wheter or not the edit panel should be shown for 
	 * unit tray label (authority file records).  */
	public static final String KEY_EDITVIEWUTL = "config.ediableunittraylabeldb";

	/** Key for properties file for extra human readable text to put on all slips/labels by default */
	public static final String KEY_EXTRAHUMANTEXT = "config.extrahumantext";
	/** Key for properties file for file to print labels out to. */
	public static final String KEY_LABELPRINTFILE = "print.outputfile";
	/** Key for properties file for turning on debugging of label layout by adding colors */
	public static final String KEY_DEBUGLABEL = "debug.labellayout";
	
	/** Key for property to handle infraspecific names. */
	public static final String KEY_USEQUADRANOMIALS = "config.usequadranomials";
	/** Key for properties file for rank of subspecies in trinomials, 'subspecies' by default.  */
	public static final String KEY_TRINOMIAL_SUBSP_RANK = "config.trinomialsubsprank";
	/** When not using quadranomials, put subspecies and subspecies rank in infraspecific name and rank. */
	public static final String KEY_PUT_SUBSPECIES_IN_INFRA = "config.putsubspeciesininfra";	
	

	private Properties properties = null;
	private String propertiesFilename = null;
	private StringBuffer propertiesFilePath = null;

	/** 
	 * Support for persistent properties for the PreCapture application.
	 */
	public PreCaptureProperties() {
		super();
		propertiesFilename = DEFAULT_FILENAME;
		propertiesFilePath = new StringBuffer(System.getProperty("user.dir"));
		propertiesFilePath.append(System.getProperty("file.separator"));
		propertiesFilePath.append(propertiesFilename);
		log.debug("Opening properties file: " + propertiesFilePath.toString());
		try {
			loadProperties();
		} catch (Exception e) {
			properties = new Properties();
			log.debug(e.getMessage());
		}
		checkDefaults();
	}

	/**
	 * properties getter
	 * 
	 * @return the properties list
	 */
	public Properties getProperties() {
		return properties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		String returnValue = "";
		if (column == 0) {
			returnValue = "Key";
		}
		if (column == 1) {
			returnValue = "Property value";
		}
		return returnValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		boolean returnValue = false;
		if (columnIndex == 1) {
			returnValue = true;
		}
		return returnValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object,
	 * int, int)
	 */
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (columnIndex == 1) {
			Enumeration<Object> p = properties.keys();
			int element = 0;
			while (p.hasMoreElements()) {
				String e = (String) p.nextElement();
				if (element == rowIndex) {
					properties.setProperty(e, (String) value);
				}
				element++;
			}
		}
	}

	/**
	 * Set default property values for each key.  Doesen't overwrite any values 
	 * that have already been set.
	 */
	private void checkDefaults() {
		if (!properties.containsKey(KEY_PAPERSIZE)) {
			// paper size for printing (e.g. A4, or Letter)
			properties.setProperty(KEY_PAPERSIZE, "Letter");
		}
		if (!properties.containsKey(KEY_FIELDMAPPING)) {
			// xml file containing field mappings
			properties.setProperty(KEY_FIELDMAPPING, "resources/NEVP_TCN_PrecaptureFields.xml");
		}
		if (!properties.containsKey(KEY_DISPLAY_AUTHORITY_FILE)) {
			// is the taxon name authority file shown? 
			properties.setProperty(KEY_DISPLAY_AUTHORITY_FILE, "false");
		}
		if (!properties.containsKey(KEY_EDITABLE_AUTHORITY_FILE)) {
			// is the taxon name authority file editable? 
			properties.setProperty(KEY_EDITABLE_AUTHORITY_FILE, "false");
		}		
		if (!properties.containsKey(KEY_SHOW_INVENTORY)) {
			// is the taxon name authority file editable? 
			properties.setProperty(KEY_SHOW_INVENTORY, "true");
		}
		if (!properties.containsKey(KEY_PRINTDEFINITIONS)) {
			// xml file containing print layout definitions
			properties.setProperty(KEY_PRINTDEFINITIONS, "resources/LabelDefinitions.xml");
		}		
		if (!properties.containsKey(KEY_SELECTED_PRINT_DEFINITION)) {
			// name of selected print layout definition
			properties.setProperty(KEY_SELECTED_PRINT_DEFINITION, "Two Half Sheets on 8.5x11");
		}
		if (!properties.containsKey(KEY_MAINFRAME)) {
			// name of selected UI main frame 
			properties.setProperty(KEY_MAINFRAME, "MainFrame");
		}
		if (!properties.containsKey(KEY_MY_COLLECTION_CODE)) {
			// default collection code (empty string is application default) 
			properties.setProperty(KEY_MY_COLLECTION_CODE, "");
		}		
		if (!properties.containsKey(PreCaptureProperties.KEY_FILTER_LENGTH_THRESHOLD)) {
			// default filtering threshold for filtering combobox
			properties.setProperty(KEY_FILTER_LENGTH_THRESHOLD, "4");
		}			
		if (!properties.containsKey(KEY_QRCODEECLEVEL)) {
			// name of selected UI main frame 
			properties.setProperty(KEY_QRCODEECLEVEL, "H");
		}
		if (!properties.containsKey(KEY_EDITVIEWUTL)) {
			// by default don't show the unit tray label db editor panel
			properties.setProperty(KEY_EDITVIEWUTL, "false");
		}		
		if (!properties.containsKey(KEY_USEQUADRANOMIALS)) {
			// by default don't use subspecific epithet as different field from 
			// infraspecific epithet
			properties.setProperty(KEY_USEQUADRANOMIALS, "false");
		}		
		if (!properties.containsKey(KEY_EXTRAHUMANTEXT)) {
			// by default print "don't remove slip" in human readable form
			properties.setProperty(KEY_EXTRAHUMANTEXT, "Do not remove this slip from this folder.");
		}		
		if (!properties.containsKey(KEY_LABELPRINTFILE)) {
			// by default print to labels.pdf in the current directory.
			properties.setProperty(KEY_LABELPRINTFILE, "labels.pdf");
		}		
		if (!properties.containsKey(KEY_DEBUGLABEL)) {
			// by default print to labels.pdf in the current directory.
			properties.setProperty(KEY_DEBUGLABEL, "false");
		}	
		if (!properties.containsKey(KEY_TRINOMIAL_SUBSP_RANK)) {
			// by default print to labels.pdf in the current directory.
			properties.setProperty(KEY_TRINOMIAL_SUBSP_RANK, "subspecies");
		}		
		if (!properties.containsKey(KEY_PUT_SUBSPECIES_IN_INFRA)) {
			// by default print to labels.pdf in the current directory.
			properties.setProperty(KEY_PUT_SUBSPECIES_IN_INFRA, "true");
		}			
	}

	/**
	 * Get persistence file location.
	 * 
	 * @returns a text string representing the storage location of properties
	 * file.
	 */
	public String getPropertiesSource() {
		return propertiesFilePath.toString();
	}

	/**
	 * Load the properties from the properties file
	 * 
	 * @throws Exception if there is a problem with the file or with opening it.
	 */
	protected void loadProperties() throws Exception {
		properties = new Properties();
		FileInputStream propertiesStream = null;
		try {
			propertiesStream = new FileInputStream(
					propertiesFilePath.toString());
			properties.load(propertiesStream);
		} catch (FileNotFoundException e) {
			log.error("Error: Properties file not found.");
			log.error(e.getMessage());
			throw e;
		} catch (Exception ex) {
			log.error("Error loading properties.");
			log.error(ex.getMessage());
			throw ex;
		} finally {
			if (propertiesStream != null) {
				propertiesStream.close();
			}
		}
	}

	/**
	 * Persist the current property values to the property file.
	 * 
	 * @throws Exception on a problem writing the file.
	 */
	public void saveProperties() throws Exception {
		FileOutputStream propertiesStream = null;
		try {
			propertiesStream = new FileOutputStream( propertiesFilePath.toString());
			properties.store(propertiesStream, PreCaptureApp.NAME + " " + PreCaptureApp.VERSION
					+ " User Properties");
			propertiesStream.close();
		} catch (Exception e) {
			log.error("Error saving properties.");
			log.error(e.getMessage());
			e.printStackTrace();
			throw e;
		} finally {
			if (propertiesStream != null) {
				propertiesStream.close();
			}
		}
	}

	public int getRowCount() {
		return properties.size();
	}

	public int getColumnCount() {
		return 2;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		String value = "";
		Enumeration<Object> p = properties.keys();
		int element = 0;
		while (p.hasMoreElements()) {
			String e = (String) p.nextElement();
			if (element == rowIndex) {
				if (columnIndex == 0) {
					value = e;
				} else {
					value = properties.getProperty(e);
				}
			}
			element++;
		}
		return value;
	}
	
	
	
}
