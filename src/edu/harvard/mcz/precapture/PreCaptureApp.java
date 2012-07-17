/**
 * PreCaptureApp.java
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

import java.awt.EventQueue;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.precapture.ui.LabelList;
import edu.harvard.mcz.precapture.ui.MainFrame;
import edu.harvard.mcz.precapture.xml.MappingList;

/**
 * Launch the Pre-Capture Label generation Application.
 * 
 * @author mole
 *
 * $Id$ 
 */
public class PreCaptureApp {

	public static final String NAME = "PreCaptureApp";
	public static final String VERSION = "0.2";

	private static final Log log = LogFactory.getLog(PreCaptureApp.class);
	
	/**
	 * Entry point for the PreCapture application.  
	 * 
	 * @param args none of which are interpreted.
	 */
	public static void main(String[] args) {

		// Load configuration properties
		PreCaptureSingleton.getInstance().setProperties(new PreCaptureProperties());
		
		// Load field mappings from XML 
		String resource = PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_FIELDMAPPING);
		InputStream stream = PreCaptureApp.class.getResourceAsStream(resource);
		if (stream!=null) {
	   	    JAXBContext jc;
			try {
				
				jc = JAXBContext.newInstance( edu.harvard.mcz.precapture.xml.MappingList.class );
				Unmarshaller u = jc.createUnmarshaller();
				MappingList mappingList = null;
				mappingList = (MappingList)u.unmarshal(stream);
				PreCaptureSingleton.getInstance().setMappingList(mappingList);
			} catch (JAXBException e) {
				// You will need to add the annotation: @XmlRootElement(name="FieldMapping") to MappingList.java
				// if you have regenerated the imagecapture.xml classes from the schema.
				log.error("JAXBException.  You may be missing @XmlRootElement(name=FieldMapping) from MappingList.java ");
				log.error(e.getMessage());
			}
		} else { 
			// getResourceAsStream returns null if loader has an IO exception.
			log.error("Couldn't find resource file: " + resource);
		}		
		
		LabelList labelList = new LabelList();
		PreCaptureSingleton.getInstance().setCurrentLabelList(labelList);
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame window = new MainFrame();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});		
		
	}

	/**
	 * Save properties, write log message, and exit application normally. 
	 */
	public static void exit() {
		try {
			PreCaptureSingleton.getInstance().getProperties().saveProperties();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.debug("Exiting.");
		System.exit(0);
	}
}
