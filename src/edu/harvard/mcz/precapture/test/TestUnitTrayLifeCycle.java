/**
 * TestUnitTrayLifeCycle.java
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import edu.harvard.mcz.precapture.data.UnitTrayLabel;
import edu.harvard.mcz.precapture.data.UnitTrayLabelLifeCycle;

/**
 * @author mole
 *
 */
public class TestUnitTrayLifeCycle {
	private static final Log log = LogFactory
			.getLog(TestUnitTrayLifeCycle.class);

	/** 
	 * Default no argument constructor, constructs a new TestUnitTrayLifeCycle instance.
	 */
	public TestUnitTrayLifeCycle() {

	}

	/**
	 * Test method for {@link edu.harvard.mcz.precapture.data.UnitTrayLabelLifeCycle#getScientificName(edu.harvard.mcz.precapture.data.UnitTrayLabel)}.
	 */
	@Test
	public void testGetScientificName() {
		UnitTrayLabel testLabel = new UnitTrayLabel();
		// Set a few core fields and test.
		testLabel.setAuthorship("L.");
		testLabel.setGenus("Quercus");
		testLabel.setSpecificEpithet("alba");
		assertTrue(UnitTrayLabelLifeCycle.getScientificName(testLabel).equals("Quercus alba L."));
		// add some irrelevant fields and test again
		testLabel.setFamily("Family");
		testLabel.setCollection("collection");
		assertTrue(UnitTrayLabelLifeCycle.getScientificName(testLabel).equals("Quercus alba L."));
		testLabel.setInfraspecificEpithet("infraspecific");
		testLabel.setInfraspecificRank("infraspecificRank");
		testLabel.setSubspecificEpithet("subspecific");
		// This is the expected behavior for animals (ICZN) (constructing a quadranomial)
		// it may not be the desired behavior for botanical (ICNAFB) names. 
		assertTrue(UnitTrayLabelLifeCycle.getScientificName(testLabel).equals("Quercus alba subspecific infraspecificRank infraspecific L."));
	
		// test when given null
		testLabel = null;
		assertTrue(UnitTrayLabelLifeCycle.getScientificName(testLabel).equals(""));
		// test when given empty unit tray label
		testLabel = new UnitTrayLabel();
		assertTrue(UnitTrayLabelLifeCycle.getScientificName(testLabel).equals(""));
		
	}
}
