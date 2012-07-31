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
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import edu.harvard.mcz.precapture.data.HibernateUtil;
import edu.harvard.mcz.precapture.data.InventoryLifeCycle;
import edu.harvard.mcz.precapture.data.UnitTrayLabelLifeCycle;
import edu.harvard.mcz.precapture.exceptions.StartupFailedException;
import edu.harvard.mcz.precapture.ui.LabelList;
import edu.harvard.mcz.precapture.ui.MainFrame;
import edu.harvard.mcz.precapture.xml.MappingList;
import edu.harvard.mcz.precapture.xml.labels.LabelDefinitionListType;
import edu.harvard.mcz.precapture.xml.labels.LabelDefinitionType;

/**
 * Launch the Pre-Capture Label generation Application.
 * 
 * @author mole
 *
 * $Id$ 
 */
public class PreCaptureApp {

	public static final String NAME = "PreCaptureApp";
	public static final String VERSION = "0.5";
	public static final String SVN_ID = "$Id:$";
	public static final String AUTHORS = "Paul J. Morris";
	public static final String COPYRIGHT = "Copyright © 2012 President and Fellows of Harvard College";
	public static final String LICENSE = "Version 2 of the GNU General Public License";

	private static final Log log = LogFactory.getLog(PreCaptureApp.class);

	/**
	 * Entry point for the PreCapture application.  
	 * 
	 * @param args none of which are interpreted.
	 */
	public static void main(String[] args) {

		try { 

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

					StringBuffer projects = new StringBuffer();
					List<String> lp = mappingList.getSupportedProject();
					Iterator<String> i = lp.iterator();
					while (i.hasNext()) { 
						projects.append(i.next()).append(" ");
					}
					log.debug("Loaded field mappings: " + projects.toString() + mappingList.getVersion());

				} catch (JAXBException e) {
					String message = "Unable to load field mappings.  JAXBException. \nYou may be missing @XmlRootElement(name=FieldMapping) from MappingList.java ";
					// You will need to add the annotation: @XmlRootElement(name="FieldMapping") to MappingList.java
					// if you have regenerated the imagecapture.xml classes from the schema.
					log.error(message);
					log.error(e.getMessage());
					throw new StartupFailedException(message);
				}
			} else { 
				// getResourceAsStream returns null if loader has an IO exception.
				log.error("Couldn't find resource file: " + resource);
			}	

			// Load printing definitions from XML
			String printresource = PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_PRINTDEFINITIONS);
			InputStream printstream = PreCaptureApp.class.getResourceAsStream(printresource);
			if (printstream!=null) {
				JAXBContext jc;
				try {

					jc = JAXBContext.newInstance( edu.harvard.mcz.precapture.xml.labels.LabelDefinitionListType.class );
					Unmarshaller u = jc.createUnmarshaller();
					LabelDefinitionListType printFormatDefintionList = null;
					printFormatDefintionList = (LabelDefinitionListType)u.unmarshal(printstream);
					PreCaptureSingleton.getInstance().setPrintFormatDefinitionList(printFormatDefintionList);

					List<LabelDefinitionType> printDefs = printFormatDefintionList.getLabelDefinition();
					Iterator<LabelDefinitionType> i = printDefs.iterator();
					log.debug("Available print format definitions:");
					boolean selectedMatched = false;
					while (i.hasNext()) {
						String title = i.next().getTitle();
						if (title.equals(PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_SELECTED_PRINT_DEFINITION))) { 
							selectedMatched = true;
							log.debug(title + " *");
						} else { 
							log.debug(title);
						}
					}
					if (!selectedMatched) { 
						log.error("Selected print format definition isn't on list of available definitions.");
					}

				} catch (JAXBException e) {
					String message = "Unable to load print format definition. JAXBException.  \nYou may be missing @XmlRootElement(name=LabelDefinition) from LabelDefinitionListType.java";
					e.printStackTrace();
					// You will need to add the annotation: @XmlRootElement(name="LabelDefinition") to LabelDefinitionListType.java
					// if you have regenerated the imagecapture.xml classes from the schema.
					log.error(message);
					log.error(e.getMessage());
					throw new StartupFailedException(message);
				}
			} else { 
				// getResourceAsStream returns null if loader has an IO exception.
				log.error("Couldn't find resource file: " + resource);
			}		

			LabelList labelList = new LabelList();
			PreCaptureSingleton.getInstance().setCurrentLabelList(labelList);

			// Test database 
			try {
				log.debug(org.apache.derby.tools.sysinfo.getProductName() + " " + org.apache.derby.tools.sysinfo.getVersionString());
				// Database lives in current directory.
				System.setProperty("derby.system.home", System.getProperty("user.dir"));

				// test database connectivity
				SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
				Session session = sessionFactory.openSession();
				try {
					// Test to see if inventory table exists
					InventoryLifeCycle ils = new InventoryLifeCycle();
					log.debug("Inventory Records: " + ils.countAtStartup());
					// Test to see if unit tray label table exists
					UnitTrayLabelLifeCycle uls = new UnitTrayLabelLifeCycle();
					log.debug("Unit Tray Label Records: " + uls.countAtStartup());
				} catch (HibernateException ex) { 
					log.debug("Recreating schema.");
					// if not, create schema specified in hibernate.cfg.xml
					Configuration conf = new Configuration().configure();
					new SchemaExport(conf).create(true, true);
				} catch (NullPointerException ex) { 
					log.debug("Recreating schema.");
					// if not, create schema specified in hibernate.cfg.xml
					Configuration conf = new Configuration().configure();
					new SchemaExport(conf).create(true, true);
				}


				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							// Launch user interface
							MainFrame window = new MainFrame();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});		

			} catch (Exception e) {
				// report database connection error
				StringBuffer messages = new StringBuffer();
				messages.append(e.getMessage()).append("\n");
				log.error(e.getMessage());
				if (e.getCause()!=null) { 
					messages.append(e.getCause().getMessage()).append("\n");
					log.error(e.getCause().getMessage());
					if (e.getCause().getCause()!=null) { 
						log.error(e.getCause().getCause().getMessage());
						messages.append(e.getCause().getCause().getMessage()).append("\n");
						if (e.getCause().getCause().getCause()!=null) { 
							log.error(e.getCause().getCause().getCause().getMessage());
							messages.append(e.getCause().getCause().getCause().getMessage()).append("\n");
						}
					}
				}
				throw new StartupFailedException(messages.toString());
			}

		} catch (StartupFailedException sfe) {
			// Fatal startup error, alert user and exit.
			JOptionPane.showMessageDialog(null, "Unable to start " + PreCaptureApp.NAME +". \n" + sfe.getMessage());
			exit();
		}

	}

	/**
	 * Save properties, write log message, and exit application normally. 
	 */
	public static void exit() {
		try {
			// Write current properties to properties file (creating file 
			// with default properties if it does not yet exist).
			PreCaptureSingleton.getInstance().getProperties().saveProperties();
			// Test to see if inventory table is non-empty, if so, backup.
			InventoryLifeCycle ils = new InventoryLifeCycle();
			if (ils.count()>0) { 
			    InventoryLifeCycle.exportToCSV("Inventory_backup.csv");
			} 
			// Test to see if UnitTrayLabel table is non-empty, if so, backup.
			UnitTrayLabelLifeCycle uls = new UnitTrayLabelLifeCycle();
			if (uls.count()>0) { 
			    UnitTrayLabelLifeCycle.exportToCSV("TaxonAuthorityFile_backup.csv");
			} 
			HibernateUtil.terminateSessionFactory();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.debug("Exiting.");
		System.exit(0);
	}
}
