/**
 * MainFrame.java
 * edu.harvard.mcz.imagecapture.ui
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
 * $Id$
 */
package edu.harvard.mcz.imagecapture.ui;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.UIManager;

import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;

import edu.harvard.mcz.imagecapture.PreCaptureApp;
import edu.harvard.mcz.imagecapture.PreCaptureSingleton;
import edu.harvard.mcz.imagecapture.encoder.LabelEncoder;
import edu.harvard.mcz.imagecapture.exceptions.PrintFailedException;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import javax.swing.JComboBox;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.BevelBorder;
import javax.swing.SwingConstants;
import java.awt.FlowLayout;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.awt.Toolkit;

public class MainFrame {

	private JFrame frame;
	private JTable table;
	private JTable table_1;
	
	private FolderPanel folderPanel;

	/**
	 * Create the application.
	 */
	public MainFrame() {
		
		try {
			// Use the Napkin look and feel
	        UIManager.setLookAndFeel("net.sourceforge.napkinlaf.NapkinLookAndFeel");
	    } catch (Exception e) {
	        // Expected if Napkin look and feel isn't on build path.
	    }		
		
		initialize();
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/edu/harvard/mcz/imagecapture/resources/icon.png")));
		frame.setBounds(100, 100, 462, 640);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		mnFile.setMnemonic(KeyEvent.VK_F);
		menuBar.add(mnFile);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PreCaptureApp.exit();
			}
		});
		
		JMenuItem mntmConfiguration = new JMenuItem("Configuration");
		mntmConfiguration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConfigurationDialog dialog = new ConfigurationDialog();
				dialog.setVisible(true);
			}
		});
		mntmConfiguration.setMnemonic(KeyEvent.VK_C);
		mnFile.add(mntmConfiguration);
		mntmExit.setMnemonic(KeyEvent.VK_X);
		mnFile.add(mntmExit);
		
		JMenu mnHelp = new JMenu("Help");
		mnHelp.setMnemonic(KeyEvent.VK_H);
		menuBar.add(mnHelp);
		
		JMenuItem mntmVersion = new JMenuItem("Version: " + PreCaptureApp.VERSION  + " $Id$" );
		// TODO: set to false on changing to normal look and feel
		mntmVersion.setEnabled(true);
		mnHelp.add(mntmVersion);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Folder", null, panel, null);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_O);
		panel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPaneFolder = new JScrollPane();
		folderPanel = new FolderPanel();
		scrollPaneFolder.setViewportView(folderPanel);
		panel.add(scrollPaneFolder, BorderLayout.CENTER);
		
		JPanel panel_5 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_5.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel.add(panel_5, BorderLayout.SOUTH);
		
		JButton btnPrint = new JButton("Print");
		panel_5.add(btnPrint);
		btnPrint.setMnemonic(KeyEvent.VK_P);
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
		
		
		JPanel panel_4 = new JPanel();
		tabbedPane.addTab("Print List", null, panel_4, null);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_L);
		panel_4.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel_4.add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable();
		table.setModel(PreCaptureSingleton.getInstance().getCurrentLabelList());
		scrollPane.setViewportView(table);
		
		JPanel panel_6 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_6.getLayout();
		flowLayout_1.setAlignment(FlowLayout.RIGHT);
		panel_4.add(panel_6, BorderLayout.SOUTH);
		
		JButton btnPrint_1 = new JButton("Print");
		panel_6.add(btnPrint_1);
		btnPrint_1.setMnemonic(KeyEvent.VK_P);
		btnPrint_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				try {
					LabelEncoder.printList(PreCaptureSingleton.getInstance().getCurrentLabelList().getLabels());
				} catch (PrintFailedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}				
			}
		});
		
		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				PreCaptureSingleton.getInstance().getCurrentLabelList().clearList();
			}
		});
		panel_6.add(btnClear);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Inventory", null, panel_1, null);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane_1 = new JScrollPane();
		panel_1.add(scrollPane_1, BorderLayout.CENTER);
		
		table_1 = new JTable();
		table_1.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Cabinet", "Taxon", "Inches of New England","Sheets per inch"
			}
		));
		scrollPane_1.setViewportView(table_1);
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_I);
	}
}
