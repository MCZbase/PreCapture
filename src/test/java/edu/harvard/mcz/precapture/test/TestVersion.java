/**
 * TestVersion.java
 * edu.harvard.mcz.precapture.test
 * Copyright © 2026 President and Fellows of Harvard College
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
 */
package edu.harvard.mcz.precapture.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.harvard.mcz.precapture.PreCaptureApp;

/**
 * Test that the application version is filled in from pom.xml by the build.
 */
public class TestVersion {

	@Test
	public void testVersion() { 
		assertTrue("Version not filled in from pom.xml: " + PreCaptureApp.VERSION, 
				PreCaptureApp.VERSION.matches("\\d+\\.\\d+\\.\\d+.*"));
	}

}
