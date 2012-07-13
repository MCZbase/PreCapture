/**
 * PreCaptureProperties.java
 * edu.harvard.mcz.imagecapture
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
package edu.harvard.mcz.imagecapture;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

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
	
	public static final String KEY_PAPERSIZE = "print.papersize";
	public static final String KEY_FIELDMAPPING = "config.fieldmap";

	private Properties properties = null;
	private String propertiesFilename = null;
	private StringBuffer propertiesFilePath = null;

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
	}

	/*
	 * Get persistence file location.
	 * 
	 * @returns a text string representing the storage location of properties
	 * file.
	 */
	public String getPropertiesSource() {
		return propertiesFilePath.toString();
	}

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return properties.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
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
