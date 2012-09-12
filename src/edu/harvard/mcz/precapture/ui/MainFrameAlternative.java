/**
 * MainFrame.java
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
 * Author: Paul J. Morris
 * 
 * $Id: MainFrame.java 34 2012-07-31 23:30:57Z chicoreus $
 */
package edu.harvard.mcz.precapture.ui;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.UIManager;

import java.awt.event.KeyEvent;

import edu.harvard.mcz.precapture.PreCaptureApp;
import edu.harvard.mcz.precapture.PreCaptureProperties;
import edu.harvard.mcz.precapture.PreCaptureSingleton;
import edu.harvard.mcz.precapture.encoder.LabelEncoder;
import edu.harvard.mcz.precapture.exceptions.PrintFailedException;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.BorderLayout;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import javax.swing.JSplitPane;

/**
 * Main UI Frame for the PreCapture Application.
 * 
 * @author mole
 *
 */
public class MainFrameAlternative implements WindowListener {

	private static final Log log = LogFactory.getLog(MainFrameAlternative.class);
	
	private JFrame frame;
	private JTable tablePrintList;
	
	private ContainerEntryPanel folderPanel;

	/**
	 * Create the user interface.
	 */
	public MainFrameAlternative() {
		
		if (PreCaptureSingleton.useNapkin()) { 

			try {
				// Use the Napkin look and feel
				UIManager.setLookAndFeel("net.sourceforge.napkinlaf.NapkinLookAndFeel");
			} catch (Exception e) {
				// Expected if Napkin look and feel isn't on build path.
			}		

		}
		
		initialize();
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle(PreCaptureApp.NAME);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // handled by window listener
		frame.addWindowListener(this);  
		// Maximize 
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(MainFrameAlternative.class.getResource("/edu/harvard/mcz/precapture/resources/icon.png")));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setBounds(0, 0, (int)screenSize.getWidth()-25, (int)screenSize.getHeight()-25);
		GraphicsEnvironment grEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        frame.setMaximizedBounds(grEnv.getMaximumWindowBounds());
	    frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		
		JMenuBar menuBar = new MainMenu(frame, this.getClass());
		frame.setJMenuBar(menuBar);
		
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Folders", null, panel, null);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_O);
		panel.setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		panel.add(splitPane, BorderLayout.CENTER);
		
		JPanel panel_2 = new JPanel();
		panel_2.setMinimumSize(new Dimension(400, 10));
		splitPane.setLeftComponent(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPaneFolder = new JScrollPane();
		scrollPaneFolder.setMinimumSize(new Dimension(400, 22));
		panel_2.add(scrollPaneFolder, BorderLayout.CENTER);
		folderPanel = new ContainerEntryPanel();
		scrollPaneFolder.setViewportView(folderPanel);
		
		JPanel panel_5 = new JPanel();
		panel_2.add(panel_5, BorderLayout.SOUTH);
		FlowLayout flowLayout = (FlowLayout) panel_5.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		
		JButton btnPrint = new JButton("Print One");
		panel_5.add(btnPrint);
		btnPrint.setMnemonic(KeyEvent.VK_O);
		btnPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				folderPanel.invokePrintOne();
			}
		});
		
		JButton btnAddToPrint = new JButton("Add to Print List");
		btnAddToPrint.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_5.add(btnAddToPrint);
		btnAddToPrint.setMnemonic(KeyEvent.VK_A);
		btnAddToPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				folderPanel.invokeAddToList();
			}
		});
		
		JPanel panel_3 = new ContainerListPanel(frame);
		splitPane.setRightComponent(panel_3);
		
		if (PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_SHOW_INVENTORY).equals("true"))  {
			tabbedPane.addTab("Inventory", null, new InventoryPanel(frame), null);
			tabbedPane.setMnemonicAt(1, KeyEvent.VK_I);
		} 
		
		if (PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_EDITVIEWUTL).equals("true"))  {
			tabbedPane.addTab("Taxon Authority File", null, new UnitTrayLabelEditPanel(frame), null);
			tabbedPane.setMnemonicAt(1, KeyEvent.VK_T);
		} 
	}

	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowClosing(WindowEvent e) {
		PreCaptureApp.exit(frame);
	}

	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}
