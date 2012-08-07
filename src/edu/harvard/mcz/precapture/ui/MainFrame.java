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
 * $Id$
 */
package edu.harvard.mcz.precapture.ui;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.UIManager;

import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;

import edu.harvard.mcz.precapture.PreCaptureApp;
import edu.harvard.mcz.precapture.PreCaptureProperties;
import edu.harvard.mcz.precapture.PreCaptureSingleton;
import edu.harvard.mcz.precapture.SplashScreen;
import edu.harvard.mcz.precapture.data.Inventory;
import edu.harvard.mcz.precapture.data.InventoryLifeCycle;
import edu.harvard.mcz.precapture.data.UnitTrayLabelLifeCycle;
import edu.harvard.mcz.precapture.encoder.LabelEncoder;
import edu.harvard.mcz.precapture.exceptions.PrintFailedException;
import edu.harvard.mcz.precapture.exceptions.SaveFailedException;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.BorderLayout;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;

/**
 * Main UI Frame for the PreCapture Application.
 * 
 * @author mole
 *
 */
public class MainFrame  implements WindowListener {

	private static final Log log = LogFactory.getLog(MainFrame.class);
	
	private JFrame frame;
	private JTable tablePrintList;
	private JTable tableInventory;
	/** Set to true to use Napkin Look and Feel.
	*/
	private boolean useNapkin;
	
	private FolderEntryPanel folderPanel;

	/**
	 * Create the user interface.
	 */
	public MainFrame() {
		
		useNapkin = false;
		
		if (useNapkin) { 

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
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/edu/harvard/mcz/precapture/resources/icon.png")));
		frame.setBounds(100, 100, 640, 640);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		mnFile.setMnemonic(KeyEvent.VK_F);
		menuBar.add(mnFile);
		
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
		// TODO: Move to configuration.
		JMenuItem mntmLoadTaxa = new JMenuItem("Load Taxon List");
		mntmLoadTaxa.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
				FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV spreadsheets", "csv");
				fileChooser.setFileFilter(filter);
				int returnVal = fileChooser.showOpenDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					try {
						Cursor c = frame.getCursor();
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						UnitTrayLabelLifeCycle.loadFromCSV(file.getCanonicalPath());
						frame.setCursor(c);
						
					} catch (Exception ex) { 
						log.error(ex.getMessage());
						JOptionPane.showMessageDialog(frame, "Failed to load taxon list. " + ex.getMessage());
					}
				}
			}
		});
		mntmLoadTaxa.setMnemonic(KeyEvent.VK_T);		
		mnFile.add(mntmConfiguration);
		
		JMenuItem mntmRunTests = new JMenuItem("Run Tests");
		mntmRunTests.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BarcodeTestingFrame testFrame = new BarcodeTestingFrame();
				testFrame.setVisible(true);
			}
		});
		mntmRunTests.setMnemonic(KeyEvent.VK_T);
		mnFile.add(mntmRunTests);
		
		//mnFile.add(mntmLoadTaxa);
		mntmExit.setMnemonic(KeyEvent.VK_X);
		mnFile.add(mntmExit);
		
		JMenu mnHelp = new JMenu("Help");
		mnHelp.setMnemonic(KeyEvent.VK_H);
		menuBar.add(mnHelp);
		
		JMenuItem mntmApplicationHelp = new JMenuItem("Using " + PreCaptureApp.NAME);
		mntmApplicationHelp.setMnemonic(KeyEvent.VK_U);
		mntmApplicationHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log.debug("Help selected");
				HelpFrame help = new HelpFrame();
				help.setVisible(true);
			}
		});
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
		mntmVersion.setEnabled(useNapkin);
		mnHelp.add(mntmVersion);
		
		JMenuItem mntmID = new JMenuItem("$Id$" );
		// needs to be enabled to be readable in napkin look and feel
		mntmID.setEnabled(useNapkin);
		mnHelp.add(mntmID);		
		
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Folder", null, panel, null);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_O);
		panel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPaneFolder = new JScrollPane();
		folderPanel = new FolderEntryPanel();
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
		
		tablePrintList = new JTable();
		tablePrintList.setModel(PreCaptureSingleton.getInstance().getCurrentLabelList());
		scrollPane.setViewportView(tablePrintList);
		
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
					log.error(e1.getMessage());
					JOptionPane.showMessageDialog(frame, "Failed to print. " + e1.getMessage());
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
		
		if (PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_SHOW_INVENTORY).equals("true"))  {
			JPanel panel_1 = new JPanel();
			tabbedPane.addTab("Inventory", null, panel_1, null);
			panel_1.setLayout(new BorderLayout(0, 0));

			JScrollPane scrollPane_1 = new JScrollPane();
			panel_1.add(scrollPane_1, BorderLayout.CENTER);

			tableInventory = new JTable();
			InventoryLifeCycle ils = new InventoryLifeCycle();
			InventoryTableModel iTableModel = new InventoryTableModel((ArrayList<Inventory>)ils.findAll());
			tableInventory.setModel(iTableModel);
			scrollPane_1.setViewportView(tableInventory);
			tabbedPane.setMnemonicAt(2, KeyEvent.VK_I);
			
			JPanel panel_8 = new JPanel();
			FlowLayout flowLayout_8 = (FlowLayout) panel_8.getLayout();
			flowLayout_8.setAlignment(FlowLayout.RIGHT);
			panel_1.add(panel_8, BorderLayout.SOUTH);
			
			JButton btnAddInventoryRow = new JButton("Add Row");
			panel_8.add(btnAddInventoryRow);
			btnAddInventoryRow.setMnemonic(KeyEvent.VK_A);
			btnAddInventoryRow.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { 
					try { 
					    ((InventoryTableModel)tableInventory.getModel()).addRow();
					} catch (Exception ex) { 
						log.error(ex.getMessage());
						JOptionPane.showMessageDialog(frame, "Failed to add an Inventory row. " + ex.getMessage());
					}
				}
			});			
			
			JButton btnExport = new JButton("Export as spreadsheet");
			panel_8.add(btnExport);
			btnExport.setMnemonic(KeyEvent.VK_E);
			btnExport.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { 
					JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
					FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV spreadsheets", "csv");
					fileChooser.setFileFilter(filter);
				    int returnVal = fileChooser.showSaveDialog(frame);
				        if (returnVal == JFileChooser.APPROVE_OPTION) {
				            File filename = fileChooser.getSelectedFile();
				            try {
								((InventoryTableModel)tableInventory.getModel()).writeToFile(filename);
							} catch (SaveFailedException e1) {
						         log.error(e1.getMessage());
						         JOptionPane.showMessageDialog(frame, "Failed to export. " + e1.getMessage());
							}
				        }
				}
			});
		} 
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		PreCaptureApp.exit(frame);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}
