/**
 * PreCaptureSingleton.java
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
 * Author: mole
 */
package edu.harvard.mcz.imagecapture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.ui.LabelList;
import edu.harvard.mcz.imagecapture.xml.MappingList;

/**
 * @author mole
 *
 */
public class PreCaptureSingleton {
	private static final Log log = LogFactory.getLog(PreCaptureSingleton.class);

	private PreCaptureProperties properties;
	private MappingList mappingList;
	private LabelList currentLabelList;
	
	// Eagerly create an instance to let the JVM create the unique instance 
	// during class load, this prevents the creation of multiple instances 
	//in different threads.
    private static PreCaptureSingleton uniqueInstance = new PreCaptureSingleton();
	
	/** 
	 * Default no argument constructor is private to implement Singleton pattern.
	 */
	private PreCaptureSingleton() {

	}
	/**
	 * Point of access for the singleton instance.
	 * 
	 * Example:
     * PreCaptureSingleton.getInstance().setProperties(new PreCaptureProperties());
	 * 
	 * @return the unique instance of the singleton.
	 */
	public static PreCaptureSingleton getInstance() { 
		return uniqueInstance;
	}

	/**
	 * @return the properties
	 */
	public PreCaptureProperties getProperties() {
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(PreCaptureProperties properties) {
		this.properties = properties;
	}
	/**
	 * @return the mappingList
	 */
	public MappingList getMappingList() {
		return mappingList;
	}
	/**
	 * @param mappingList the mappingList to set
	 */
	public void setMappingList(MappingList mappingList) {
		this.mappingList = mappingList;
	}
	/**
	 * @return the currentLabelList
	 */
	public LabelList getCurrentLabelList() {
		return currentLabelList;
	}
	/**
	 * @param currentLabelList the currentLabelList to set
	 */
	public void setCurrentLabelList(LabelList currentLabelList) {
		this.currentLabelList = currentLabelList;
	}
	
	
}
