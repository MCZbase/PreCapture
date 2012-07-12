package edu.harvard.mcz.imagecapture.ui;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.UIManager;

import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;

import edu.harvard.mcz.imagecapture.PreCaptureApp;
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
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.BevelBorder;

public class MainFrame {

	private JFrame frame;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;
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
		frame.setBounds(100, 100, 462, 432);
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
		
		JPanel panel_3 = new JPanel();
		panel.add(panel_3, BorderLayout.CENTER);
		panel_3.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JLabel lblTaxon = new JLabel("Taxon");
		panel_3.add(lblTaxon, "2, 2, right, default");
		
		JComboBox comboBox_2 = new JComboBox();
		panel_3.add(comboBox_2, "4, 2, 3, 1, fill, default");
		
		JLabel lblGenus = new JLabel("Genus");
		panel_3.add(lblGenus, "2, 4, right, default");
		
		textField = new JTextField();
		panel_3.add(textField, "4, 4, 3, 1, fill, default");
		textField.setColumns(10);
		
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
		
		JButton btnPrint = new JButton("Print");
		btnPrint.setMnemonic(KeyEvent.VK_P);
		btnPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		panel_3.add(btnPrint, "4, 16, fill, default");
		
		JButton btnAddToPrint = new JButton("Add to Print List");
		btnAddToPrint.setMnemonic(KeyEvent.VK_A);
		btnAddToPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		panel_3.add(btnAddToPrint, "6, 16, left, default");
		
		
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
		
		JButton btnPrint_1 = new JButton("Print");
		btnPrint_1.setMnemonic(KeyEvent.VK_P);
		panel_4.add(btnPrint_1, BorderLayout.SOUTH);
		
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
