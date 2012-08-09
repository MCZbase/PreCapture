/**
 * ConfigurationDialog.java
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
 */
package edu.harvard.mcz.precapture.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Date;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import edu.harvard.mcz.precapture.data.InventoryLifeCycle;
import edu.harvard.mcz.precapture.data.UnitTrayLabelLifeCycle;

import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigurationDialog extends JDialog {

	private static final long serialVersionUID = -8059759034783154405L;
	private static final Log log = LogFactory.getLog(ConfigurationDialog.class);
	
	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldInventoryCount;
	private JTextField textFieldUnitTrayLabelCount;

	/**
	 * Create the dialog.
	 */
	public ConfigurationDialog(final JFrame frame) {
		setTitle("PreCaptureApplication Configuration");
		setBounds(100, 100, 601, 531);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, BorderLayout.CENTER);
			{
				JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
				scrollPane.setViewportView(tabbedPane);
				{
					JPanel panel = new FieldsConfigJPanel();
					tabbedPane.addTab("Fields", null, panel, null);
					tabbedPane.setMnemonicAt(0, KeyEvent.VK_I);
				}
				{
					JPanel panel = new PrintingConfigJPanel();
					tabbedPane.addTab("Printing", null, panel, null);
					tabbedPane.setMnemonicAt(1, KeyEvent.VK_R);
				}
				{
					JPanel panel_1 = new PropertiesConfigJPanel();
					tabbedPane.addTab("Properties", null, panel_1, null);
					tabbedPane.setMnemonicAt(2, KeyEvent.VK_P);
				}
				{
					JPanel panel = new JPanel();
					tabbedPane.addTab("Data Sources", null, panel, null);
					panel.setLayout(new FormLayout(new ColumnSpec[] {
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
						panel.add(lblTaxonomy, "2, 2");
					}
					{
						JLabel lblTaxonNameRecord = new JLabel("Taxon Name Record Count");
						panel.add(lblTaxonNameRecord, "4, 4, right, default");
					}
					{
						textFieldUnitTrayLabelCount = new JTextField();
						UnitTrayLabelLifeCycle uls = new UnitTrayLabelLifeCycle();
						textFieldUnitTrayLabelCount.setText(Integer.toString(uls.count()));
						textFieldUnitTrayLabelCount.setEditable(false);
						panel.add(textFieldUnitTrayLabelCount, "6, 4, fill, default");
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
								Cursor cursor = getCursor();
								setCursor(new Cursor(Cursor.WAIT_CURSOR));
								if (returnVal == JFileChooser.APPROVE_OPTION) {
									File file = fileChooser.getSelectedFile();
									try { 
										UnitTrayLabelLifeCycle.loadFromCSV(file.getCanonicalPath());
										JOptionPane.showMessageDialog(frame, "Taxon Authority File load complete. ");
									} catch (Exception ex) { 
										log.error(ex.getMessage());
										JOptionPane.showMessageDialog(frame, "Failed to load taxon list. " + ex.getMessage());
									}
								}
								UnitTrayLabelLifeCycle uls = new UnitTrayLabelLifeCycle();
								textFieldUnitTrayLabelCount.setText(Integer.toString(uls.count()));
								setCursor(cursor);
							}
						});
						panel.add(btnLoadTaxonAuthority, "4, 6");
					}
					{
						JLabel lblInventory = new JLabel("Inventory");
						panel.add(lblInventory, "2, 8");
					}
					{
						JLabel lblInventoryRecordCount = new JLabel("Inventory Record Count:");
						panel.add(lblInventoryRecordCount, "4, 10, right, default");
					}
					{
						textFieldInventoryCount = new JTextField();
						textFieldInventoryCount.setEditable(false);
						InventoryLifeCycle ils = new InventoryLifeCycle();
						textFieldInventoryCount.setText(Integer.toString(ils.count()));
						panel.add(textFieldInventoryCount, "6, 10, fill, default");
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
						panel.add(btnReloadInventoryFrom, "4, 12");
					}
					tabbedPane.setMnemonicAt(3, KeyEvent.VK_D);
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// Save Preferences
						
						// Close preferences dialog window
						setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				okButton.setMnemonic(KeyEvent.VK_ENTER);
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// Close preferences dialog window
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				cancelButton.setMnemonic(KeyEvent.VK_C);
				buttonPane.add(cancelButton);
			}
		}
	}

}
