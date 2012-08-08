/**
 * SplashScreen.java
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
 * Author: mole
 */
package edu.harvard.mcz.precapture;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.ImageIcon;
import javax.swing.JProgressBar;
import java.awt.Toolkit;
import java.awt.Dimension;

/**
 * @author mole
 *
 */
public class SplashScreen extends JFrame {
	private static final Log log = LogFactory.getLog(SplashScreen.class);
	private JProgressBar progressBar;
	
	public static final int START_PROGRESS = 0;
	public static final int END_PROGRESS = 100;

	/** 
	 * Default no argument constructor, constructs a new SplashScreen instance.
	 */
	public SplashScreen() {
		// no borders
		setUndecorated(true);
		// center on screen
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension upperLeft = new Dimension((int)Math.round((screen.getWidth()-500)/2), (int)Math.round((screen.getHeight()-500)/2));
		this.setLocation(upperLeft.width, upperLeft.height);  // to center on screen
		setIconImage(Toolkit.getDefaultToolkit().getImage(SplashScreen.class.getResource("/edu/harvard/mcz/precapture/resources/icon.png")));
		
		JLabel lblNewLabel = new JLabel(PreCaptureApp.NAME + " " + PreCaptureApp.VERSION);
		getContentPane().add(lblNewLabel, BorderLayout.NORTH);
		
		progressBar = new JProgressBar();
		getContentPane().add(progressBar, BorderLayout.SOUTH);
		progressBar.setValue(START_PROGRESS);
		
		JLabel label = new JLabel("");
		label.setIcon(new ImageIcon(SplashScreen.class.getResource("/edu/harvard/mcz/precapture/resources/splashscreen.png")));
		getContentPane().add(label, BorderLayout.CENTER);
	}
	
	public void setProgress(int progress) {
		if (progress<START_PROGRESS) { progress = START_PROGRESS; } 
		if (progress>END_PROGRESS) { progress = END_PROGRESS; } 
		progressBar.setValue(progress);
	}

}
