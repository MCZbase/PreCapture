/**
 * ConfigureDataSourcesJPanel.java
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

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import edu.harvard.mcz.precapture.PreCaptureApp;
import edu.harvard.mcz.precapture.PreCaptureSingleton;
import edu.harvard.mcz.precapture.data.InventoryLifeCycle;
import edu.harvard.mcz.precapture.data.UnitTrayLabelLifeCycle;

/**
 * @author mole
 *
 */
public class ConfigureDataSourcesJPanel extends JPanel {
	private static final long serialVersionUID = -8003280336712208570L;
	private static final Log log = LogFactory.getLog(ConfigureDataSourcesJPanel.class);

	private JTextField textFieldInventoryCount;
	private JTextField textFieldUnitTrayLabelCount;
	private File taxonfile;
	private Cursor cursor;
	private JPanel frame;
	private JProgressBar taxonProgressBar;
	
	/** 
	 * Default no argument constructor, constructs a new ConfigureDataSourcesJPanel instance.
	 */
	public ConfigureDataSourcesJPanel() {
		frame = this;
        init();
	}
	
    private void init() { 
    	setLayout(new FormLayout(new ColumnSpec[] {
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
				FormFactory.DEFAULT_ROWSPEC,}));
		{
			JLabel lblTaxonomy = new JLabel("Taxonomy");
			add(lblTaxonomy, "2, 2");
		}
		{
			JLabel lblTaxonNameRecord = new JLabel("Taxon Name Record Count");
			add(lblTaxonNameRecord, "4, 4, right, default");
		}
		{
			textFieldUnitTrayLabelCount = new JTextField();
			UnitTrayLabelLifeCycle uls = new UnitTrayLabelLifeCycle();
			textFieldUnitTrayLabelCount.setText(Integer.toString(uls.count()));
			textFieldUnitTrayLabelCount.setEditable(false);
			add(textFieldUnitTrayLabelCount, "6, 4, fill, default");
			textFieldUnitTrayLabelCount.setColumns(10);
		}
		{
			JButton btnLoadTaxonAuthority = new JButton("Load Taxon Authority File");
			btnLoadTaxonAuthority.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
					FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV spreadsheets", "csv");
					fileChooser.setFileFilter(filter);
					int returnVal = fileChooser.showOpenDialog(frame);
					cursor = ((Component)e.getSource()).getParent().getCursor();
					((Component)e.getSource()).getParent().setCursor(new Cursor(Cursor.WAIT_CURSOR));

					if (returnVal == JFileChooser.APPROVE_OPTION) {
						taxonfile = fileChooser.getSelectedFile();
						Thread worker = new Thread() {
							public void run() {
								try {
									int count = UnitTrayLabelLifeCycle.loadFromCSV(taxonfile.getCanonicalPath(),taxonProgressBar);
									JOptionPane.showMessageDialog(frame, "Taxon Authority File load complete (" + count + " specific names). \n Restart program to use.");

									// TODO: After load, the picklists aren't immediately repopulated with 
									// the loaded names.  Most evident on a new installation.

								} catch (Exception ex) { 
									log.error(ex.getMessage());
									JOptionPane.showMessageDialog(frame, "Failed to load taxon list. " + ex.getMessage());
								}
								UnitTrayLabelLifeCycle uls = new UnitTrayLabelLifeCycle();
								textFieldUnitTrayLabelCount.setText(Integer.toString(uls.count()));
								frame.setCursor(cursor);
							}
						};
						worker.start();
					} else { 
						UnitTrayLabelLifeCycle uls = new UnitTrayLabelLifeCycle();
						textFieldUnitTrayLabelCount.setText(Integer.toString(uls.count()));
						((Component)e.getSource()).getParent().setCursor(cursor);
					}
				}
			});
			add(btnLoadTaxonAuthority, "4, 6");
		}
		{
			taxonProgressBar = new JProgressBar();
			add(taxonProgressBar, "6, 6");
		}
		{
			JLabel lblInventory = new JLabel("Inventory");
			add(lblInventory, "2, 8");
		}
		{
			JLabel lblInventoryRecordCount = new JLabel("Inventory Record Count:");
			add(lblInventoryRecordCount, "4, 10, right, default");
		}
		{
			textFieldInventoryCount = new JTextField();
			textFieldInventoryCount.setEditable(false);
			InventoryLifeCycle ils = new InventoryLifeCycle();
			textFieldInventoryCount.setText(Integer.toString(ils.count()));
			add(textFieldInventoryCount, "6, 10, fill, default");
			textFieldInventoryCount.setColumns(10);
		}
		{
			JButton btnReloadInventoryFrom = new JButton("Reload Inventory From Backup");
			btnReloadInventoryFrom.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
					FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV spreadsheets", "csv");
					fileChooser.setFileFilter(filter);
					int returnVal = fileChooser.showOpenDialog(frame);
					Cursor cursor = getCursor();
					setCursor(new Cursor(Cursor.WAIT_CURSOR));
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fileChooser.getSelectedFile();
						try { 
							InventoryLifeCycle ils = new InventoryLifeCycle();
							if (ils.count()>0) { 
								String date = new Date().toString();
								date = date.replace(" ", "");
								date = date.replace(":", "");
								InventoryLifeCycle.exportToCSV("Inventory_backup_"+ date +".csv");
								JOptionPane.showMessageDialog(frame, "Inventory load complete. ");
							} 
							InventoryLifeCycle.loadFromCSV(file.getCanonicalPath(), true);
						} catch (Exception ex) { 
							log.error(ex.getMessage());
							JOptionPane.showMessageDialog(frame, "Failed to load inventory list. " + ex.getMessage());
						}
					}
					InventoryLifeCycle ils = new InventoryLifeCycle();
					textFieldInventoryCount.setText(Integer.toString(ils.count()));	
					setCursor(cursor);
				}
			});
			add(btnReloadInventoryFrom, "4, 12");
		}
    }
	
}
