/**
 * MainMenu.java
 * edu.harvard.mcz.precapture.ui
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
package edu.harvard.mcz.precapture.ui;

import javax.help.CSH;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.JMenuBar;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.SwingUtilities;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JMenuItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jdt.internal.jarinjarloader.JarRsrcLoader;

import edu.harvard.mcz.precapture.PreCaptureApp;
import edu.harvard.mcz.precapture.PreCaptureSingleton;
import edu.harvard.mcz.precapture.resources.help.HelpLoaderClass;

/**
 * @author mole
 *
 */
public class MainMenu extends JMenuBar {
	private static final long serialVersionUID = 217747443237015250L;
	private static final Log log = LogFactory.getLog(MainMenu.class);

	private JFrame frame;
	private Class<?> currentViewClass;
	
	public MainMenu(JFrame containingFrame, Class<?> currentViewClass) {
		frame = containingFrame;
		this.currentViewClass = currentViewClass;
        init();
	}
	
	protected void init() { 
		JMenu mnFile = new JMenu("File");
		mnFile.setMnemonic(KeyEvent.VK_F);
		add(mnFile);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PreCaptureApp.exit(frame);
			}
		});
		
		JMenuItem mntmConfiguration = new JMenuItem("Configuration");
		mntmConfiguration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConfigurationDialog dialog = new ConfigurationDialog(frame);
				dialog.setVisible(true);
			}
		});
		mntmConfiguration.setMnemonic(KeyEvent.VK_C);
		mnFile.add(mntmConfiguration);
		
		JMenuItem mntmRunTests = new JMenuItem("Run Tests");
		mntmRunTests.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable(){ public void run(){
				    BarcodeTestingFrame testFrame = new BarcodeTestingFrame();
				} });
			}
		});
		mntmRunTests.setMnemonic(KeyEvent.VK_T);
		mnFile.add(mntmRunTests);
		
		//mnFile.add(mntmLoadTaxa);
		mntmExit.setMnemonic(KeyEvent.VK_X);
		mnFile.add(mntmExit);
		
		JMenu mnView = new JMenu("Window");
		mnView.setMnemonic(KeyEvent.VK_W);
		add(mnView);
		
		JMenuItem mntmChangeToTabs = new JMenuItem("Change to Tabs");
		if (currentViewClass==MainFrame.class) { 
			mntmChangeToTabs.setText("Change to SplitPane");
		}
		mntmChangeToTabs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        if (currentViewClass==MainFrame.class) {
				      MainFrameAlternative mainFrame = new MainFrameAlternative();
		        } else { 
				      MainFrame mainFrame = new MainFrame();
		        }
			    frame.setVisible(false);
			}
		});
		mntmChangeToTabs.setMnemonic(KeyEvent.VK_T);
		mnView.add(mntmChangeToTabs);
		
		JMenu mnHelp = new JMenu("Help");
		mnHelp.setMnemonic(KeyEvent.VK_H);
		add(mnHelp);
		
		JMenuItem mntmApplicationHelp = new JMenuItem("Using " + PreCaptureApp.NAME);
		mntmApplicationHelp.setMnemonic(KeyEvent.VK_U);
		try {
			//URL hsURL = PreCaptureApp.class.getResource("resources/help/helpset.hs");
			URL hsURL = ClassLoader.getSystemResource("helpset.hs");
			//hsURL = new URL("jar:rsrc:jar:precaptureHelp.jar!/helpset.hs");
			if (hsURL==null) { 
				hsURL =  JarRsrcLoader.class.getClassLoader().getResource("helpset.hs");
			}
			if (hsURL==null) { 
				hsURL =  JarRsrcLoader.class.getClassLoader().getSystemResource("helpset.hs");
			}			
			if (hsURL==null) { 
				//hsURL =  JarRsrcLoader.class.getClassLoader().getResourceAsStream("helpset.hs");
			}			
			log.debug(hsURL);
			
			//HelpSet hs = new HelpSet(HelpLoaderClass.class.getClassLoader(),hsURL);
			//HelpSet hs = new HelpSet(ClassLoader.getSystemClassLoader(),hsURL);
			HelpSet hs = new HelpSet(null,hsURL);
			mntmApplicationHelp.addActionListener(new CSH.DisplayHelpFromSource(hs.createHelpBroker()));
		} catch (HelpSetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		/*
		mntmApplicationHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log.debug("Help selected");
				HelpFrame help = new HelpFrame();
				help.setVisible(true);
			}
		});
		*/
		mnHelp.add(mntmApplicationHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log.debug("About selected");
				AboutFrame about = new AboutFrame();
				about.setVisible(true);
			}
		});
		mntmAbout.setMnemonic(KeyEvent.VK_A);
		mnHelp.add(mntmAbout);
		
		JMenuItem mntmVersion = new JMenuItem("Version: " + PreCaptureApp.VERSION);
		// needs to be enabled to be readable in napkin look and feel
		mntmVersion.setEnabled(PreCaptureSingleton.useNapkin());
		mnHelp.add(mntmVersion);
		
		JMenuItem mntmID = new JMenuItem("$Id: MainFrame.java 34 2012-07-31 23:30:57Z chicoreus $" );
		// needs to be enabled to be readable in napkin look and feel
		mntmID.setEnabled(PreCaptureSingleton.useNapkin());
		mnHelp.add(mntmID);		
	}
}
