/**
 * TestContainerLabel.java
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

import static org.junit.Assert.*;

import javax.swing.JTextField;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

import edu.harvard.mcz.precapture.PreCaptureSingleton;
import edu.harvard.mcz.precapture.ui.ContainerLabel;
import edu.harvard.mcz.precapture.ui.FieldPlusText;
import edu.harvard.mcz.precapture.xml.Field;
import edu.harvard.mcz.precapture.xml.MappingList;

/**
 * @author mole
 *
 */
public class TestContainerLabel {
	private static final Log log = LogFactory.getLog(TestContainerLabel.class);

	@Before
	public void setUp() { 
		MappingList testList = new MappingList();
		PreCaptureSingleton.getInstance().setMappingList(testList);
	}
	
	/** 
	 * Default no argument constructor, constructs a new TestContainerLabel instance.
	 */
	public TestContainerLabel() {
	}

	/**
	 * Test method for {@link edu.harvard.mcz.precapture.ui.ContainerLabel#metadataToJSON()}.
	 */
	@Test
	public void testMetadataToJSON() {
		// Does empty metadata serialize to the expected JSON fragment.
		ContainerLabel testLabel = new ContainerLabel();
		log.debug(testLabel.metadataToJSON());
		// MappingList supportedProjects is an array list, so expected empty list is []
		//
		// Not clear if null or empty string is desired for null version.
		//
		// Might fail on added spaces if JSON generation changes to a JSON library rather
		// than build by hand.  RFC4627 allows insignificant trailing spaces after
		// the comma value separator, for space in QRcode probably better to leave them out.
		assertTrue(testLabel.metadataToJSON().equals("\"m1p\":\"[]\",\"m2v\":null"));
		try {
		    JSONObject deserialized = JSONObject.fromObject(testLabel.metadataToJSON());
			fail("Incorrectly parsed metadata testLabel.toJSON() as JSON");
		} catch (JSONException e) {
			// expected as this isn't valid JSON
		}
	}

	/**
	 * Test method for {@link edu.harvard.mcz.precapture.ui.ContainerLabel#toJSON()}.
	 */
	@Test
	public void testToJSON() {
		// Does a label with no fields produce just the metadata in valid JSON
		ContainerLabel testLabel = new ContainerLabel();
		log.debug(testLabel.toJSON());
		assertTrue(testLabel.toJSON().equals("{\"m1p\":\"[]\",\"m2v\":null}"));
		try {
		    JSONObject deserialized = JSONObject.fromObject(testLabel.toJSON());
		} catch (JSONException e) {
			fail("Unable to parse metadata testLabel.toJSON() as JSON");
		}
		
		// does a field serialize to the field kvp plus metadata in valid JSON
		Field f = new Field();
		JTextField t = new JTextField();
		FieldPlusText testField = new FieldPlusText(f,t);
		testField.getField().setCode("a");
		testField.getField().setLabel("Authorship");
		testField.getTextField().setText("Agassiz");
		testLabel.getFields().add(testField);
		log.debug(testLabel.toJSON());
		assertTrue(testLabel.toJSON().equals("{\"m1p\":\"[]\",\"m2v\":null,\"a\":\"Agassiz\"}"));
		
		// are embedded quotation marks correctly escaped
		testLabel.getFields().get(0).getTextField().setText("Test\"Quoted\"Bit");
		log.debug(testLabel.toJSON());
		assertTrue(testLabel.toJSON().equals("{\"m1p\":\"[]\",\"m2v\":null,\"a\":\"Test\\\"Quoted\\\"Bit\"}"));
		
		try { 
		    JSONObject deserialized = JSONObject.fromObject(testLabel.toJSON());
		    log.debug(deserialized.toString());
		    log.debug(deserialized.getString("m2v"));
		    assertTrue(deserialized.getString("m2v").equals("null"));
		    log.debug(deserialized.getString("a"));
		    assertTrue(deserialized.getString("a").equals("Test\"Quoted\"Bit")); 
		} catch (JSONException e) {
			fail("Unable to parse testLabel.toJSON() as JSON");
		}
		
		// Test a pair of data fields
		testLabel = new ContainerLabel();
		f = new Field();
		t = new JTextField();
		testField = new FieldPlusText(f,t);
		testField.getField().setCode("a");
		testField.getField().setLabel("Authorship");
		testField.getTextField().setText("Agassiz");
		testLabel.getFields().add(testField);
		Field f1 = new Field();
		JTextField t1 = new JTextField();
		FieldPlusText testField1 = new FieldPlusText(f1,t1);
		testField1.getField().setCode("g");
		testField1.getField().setLabel("Genus");
		testField1.getTextField().setText("Silurus");
		testLabel.getFields().add(testField1);		
		log.debug(testLabel.toJSON());
		assertTrue(testLabel.toJSON().equals("{\"m1p\":\"[]\",\"m2v\":null,\"a\":\"Agassiz\",\"g\":\"Silurus\"}"));
	}
}
