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

public class MainFrame {

	private JFrame frame;
	private ArrayList<FieldPlusText> textFields;
	/*
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;
	*/
	private JTable table;
	private JTable table_1;

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
				System.exit(0);
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
		
		JMenuItem mntmVersion = new JMenuItem("Version: " + PreCaptureApp.VERSION);
		mntmVersion.setEnabled(false);
		mnHelp.add(mntmVersion);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Folder", null, panel, null);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_O);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_2.setBackground(Color.WHITE);
		panel.add(panel_2, BorderLayout.NORTH);
		panel_2.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				ColumnSpec.decode("186px"),
				ColumnSpec.decode("61px:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("15px"),}));
		
		JLabel lblFilterTaxaBy = new JLabel("Filter Taxa by:");
		panel_2.add(lblFilterTaxaBy, "2, 2");
		
		JLabel lblFamily = new JLabel("Family");
		panel_2.add(lblFamily, "3, 2, right, default");
		
		JComboBox comboBox = new JComboBox();
		panel_2.add(comboBox, "4, 2, fill, default");
		
		JLabel lblNewLabel = new JLabel("Genus");
		panel_2.add(lblNewLabel, "3, 4, right, default");
		
		JComboBox comboBox_1 = new JComboBox();
		panel_2.add(comboBox_1, "4, 4, fill, default");
		
		
		int fieldCount = PreCaptureSingleton.getInstance().getMappingList().getFieldInList().size();
		
		ArrayList<RowSpec> rowSpec = new ArrayList<RowSpec>();
	    rowSpec.add(FormFactory.RELATED_GAP_ROWSPEC);
	    rowSpec.add(FormFactory.DEFAULT_ROWSPEC);
	    for (int i=0; i<=fieldCount; i++) { 
	        rowSpec.add(FormFactory.RELATED_GAP_ROWSPEC);
	        rowSpec.add(FormFactory.DEFAULT_ROWSPEC);
	    }
		
		JPanel panel_3 = new JPanel();
		panel.add(panel_3, BorderLayout.CENTER);
		panel_3.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			    (RowSpec[]) rowSpec.toArray((RowSpec[])Array.newInstance(RowSpec.class, rowSpec.size()))
			));
		
		JLabel lblTaxon = new JLabel("Taxon");
		panel_3.add(lblTaxon, "2, 2, right, default");
		
		JComboBox comboBox_2 = new JComboBox();
		panel_3.add(comboBox_2, "4, 2, 3, 1, fill, default");
	
		textFields = new ArrayList<FieldPlusText>(); 
		
	    for (int i=0; i<fieldCount; i++) { 
	    	
	    	textFields.add(new FieldPlusText(PreCaptureSingleton.getInstance().getMappingList().getFieldInList().get(i), new JTextField()));
		
	    	int col = 2 + (i+1) * 2;
	    	
		    JLabel label = new JLabel(textFields.get(i).getField().getLabel());
		    panel_3.add(label, "2, " + Integer.toString(col) + ", right, default");
		
		    textFields.get(i).getTextField().setColumns(10);
		    panel_3.add(textFields.get(i).getTextField(), "4, " + Integer.toString(col) +  " , 3, 1, fill, default");
		
	    } 

	    /* 
	    
		JLabel lblSpecies = new JLabel("Species");
		panel_3.add(lblSpecies, "2, 6, right, default");
		
		textField_1 = new JTextField();
		panel_3.add(textField_1, "4, 6, 3, 1, fill, default");
		textField_1.setColumns(10);
		
		JLabel lblSubspecies = new JLabel("Subspecies");
		panel_3.add(lblSubspecies, "2, 8, right, default");
		
		textField_2 = new JTextField();
		panel_3.add(textField_2, "4, 8, 3, 1, fill, default");
		textField_2.setColumns(10);
		
		JLabel lblInfraspecificName = new JLabel("Infraspecific Name");
		panel_3.add(lblInfraspecificName, "2, 10, right, default");
		
		textField_3 = new JTextField();
		panel_3.add(textField_3, "4, 10, 3, 1, fill, default");
		textField_3.setColumns(10);
		
		JLabel lblInfraspecificRank = new JLabel("Infraspecific Rank");
		panel_3.add(lblInfraspecificRank, "2, 12, right, default");
		
		textField_4 = new JTextField();
		panel_3.add(textField_4, "4, 12, 3, 1, fill, default");
		textField_4.setColumns(10);
		
		JLabel lblVerbatimName = new JLabel("Other Verbatim Name");
		panel_3.add(lblVerbatimName, "2, 14, right, default");
		
		textField_5 = new JTextField();
		panel_3.add(textField_5, "4, 14, 3, 1, fill, default");
		textField_5.setColumns(10);
		
		*/
		
		JPanel panel_5 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_5.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel.add(panel_5, BorderLayout.SOUTH);
		
		JButton btnPrint = new JButton("Print");
		panel_5.add(btnPrint);
		btnPrint.setMnemonic(KeyEvent.VK_P);
		
		JButton btnAddToPrint = new JButton("Add to Print List");
		btnAddToPrint.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_5.add(btnAddToPrint);
		btnAddToPrint.setMnemonic(KeyEvent.VK_A);
		btnAddToPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		
		
		JPanel panel_4 = new JPanel();
		tabbedPane.addTab("Print List", null, panel_4, null);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_L);
		panel_4.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel_4.add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"Taxon","VON", "Number to Print"
				}
			));
		scrollPane.setViewportView(table);
		
		JPanel panel_6 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_6.getLayout();
		flowLayout_1.setAlignment(FlowLayout.RIGHT);
		panel_4.add(panel_6, BorderLayout.SOUTH);
		
		JButton btnPrint_1 = new JButton("Print");
		panel_6.add(btnPrint_1);
		btnPrint_1.setMnemonic(KeyEvent.VK_P);
		
		JButton btnClear = new JButton("Clear");
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
