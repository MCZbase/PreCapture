/**
 * PreCaptureApp.java
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

import java.awt.EventQueue;

import edu.harvard.mcz.imagecapture.ui.MainFrame;

/**
 * Launch the Pre-Capture Label generation Application.
 * 
 * @author mole
 *
 */
public class PreCaptureApp {

	public static final String VERSION = "0.0";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

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

}
