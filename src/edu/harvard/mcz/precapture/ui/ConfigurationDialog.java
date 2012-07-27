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
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.List;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import edu.harvard.mcz.precapture.PreCaptureProperties;
import edu.harvard.mcz.precapture.PreCaptureSingleton;
import edu.harvard.mcz.precapture.xml.MappingTableModel;
import edu.harvard.mcz.precapture.xml.labels.LabelDefinitionListTypeTableModel;
import edu.harvard.mcz.precapture.xml.labels.LabelDefinitionType;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigurationDialog extends JDialog {

	private static final long serialVersionUID = -8059759034783154405L;
	private static final Log log = LogFactory.getLog(ConfigurationDialog.class);
	
	private final JPanel contentPanel = new JPanel();
	private JTable table;
	private JTable tablePrintFormatList;
	private JTextField txtDerby;
	private JComboBox comboBoxPrintFormat;

	/**
	 * Create the dialog.
	 */
	public ConfigurationDialog() {
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
				}
				{
					JPanel panel = new JPanel();
					tabbedPane.addTab("Fields", null, panel, null);
					panel.setLayout(new BorderLayout(0, 0));
					{
						JScrollPane scrollPane_1 = new JScrollPane();
						panel.add(scrollPane_1, BorderLayout.CENTER);
						{
							table = new JTable();
							TableModel model = new MappingTableModel(PreCaptureSingleton.getInstance().getMappingList());
							table.setModel(model);
							scrollPane_1.setViewportView(table);
						}
					}
					tabbedPane.setMnemonicAt(0, KeyEvent.VK_F);
				}
				{
					tabbedPane.setMnemonicAt(0, KeyEvent.VK_I);
				}
				JPanel panel = new JPanel();
				tabbedPane.addTab("Printing", null, panel, null);
				tabbedPane.setMnemonicAt(1, KeyEvent.VK_R);
				panel.setLayout(new FormLayout(new ColumnSpec[] {
						FormFactory.RELATED_GAP_COLSPEC,
						ColumnSpec.decode("default:grow"),
						FormFactory.RELATED_GAP_COLSPEC,
						ColumnSpec.decode("default:grow"),},
					new RowSpec[] {
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC,
						RowSpec.decode("default:grow"),}));
				{
					JLabel lblPaperSize = new JLabel("Selected Printing Format");
					panel.add(lblPaperSize, "2, 2, right, default");
				}
				comboBoxPrintFormat = new JComboBox();
				List<LabelDefinitionType> defs = PreCaptureSingleton.getInstance().getPrintFormatDefinitionList().getLabelDefinition();
				Iterator<LabelDefinitionType> i = defs.iterator();
				while(i.hasNext()) { 
					comboBoxPrintFormat.addItem(i.next().getTitle());
				}				
				comboBoxPrintFormat.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						log.debug(comboBoxPrintFormat.getSelectedItem().toString());
						PreCaptureSingleton.getInstance().getProperties().getProperties().setProperty(PreCaptureProperties.KEY_SELECTED_PRINT_DEFINITION, 
								comboBoxPrintFormat.getSelectedItem().toString());
					}
				});
				comboBoxPrintFormat.setSelectedItem(PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_SELECTED_PRINT_DEFINITION));
				
				panel.add(comboBoxPrintFormat, "4, 2, fill, default");
				{
					JLabel lblAvailablePrintingFormats = new JLabel("Available Printing Formats");
					panel.add(lblAvailablePrintingFormats, "2, 4, 3, 1, center, default");
				}
				{
					JScrollPane scrollPane_1 = new JScrollPane();
					panel.add(scrollPane_1, "2, 6, 3, 1, fill, fill");
					{
						tablePrintFormatList = new JTable();
						tablePrintFormatList.setModel(new LabelDefinitionListTypeTableModel(PreCaptureSingleton.getInstance().getPrintFormatDefinitionList()));
						scrollPane_1.setViewportView(tablePrintFormatList);
					}
				}
				JPanel panel_1 = new JPanel();
				tabbedPane.addTab("Persistence", null, panel_1, null);
				panel_1.setLayout(new FormLayout(new ColumnSpec[] {
						FormFactory.RELATED_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.RELATED_GAP_COLSPEC,
						ColumnSpec.decode("default:grow"),},
					new RowSpec[] {
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,}));
				{
					JLabel lblDatabase = new JLabel("Database");
					panel_1.add(lblDatabase, "2, 2, right, default");
				}
				{
					txtDerby = new JTextField();
					txtDerby.setEditable(false);
					txtDerby.setText("localhost:derby");
					panel_1.add(txtDerby, "4, 2, fill, default");
					txtDerby.setColumns(10);
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
