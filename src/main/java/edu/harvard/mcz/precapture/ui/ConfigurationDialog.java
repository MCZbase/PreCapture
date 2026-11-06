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
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.precapture.interfaces.JPanelWithEditableTable;

public class ConfigurationDialog extends JDialog {

	private static final long serialVersionUID = -8059759034783154405L;
	private static final Log log = LogFactory.getLog(ConfigurationDialog.class);

	private final JPanel contentPanel = new JPanel();
	private JPanel panel_1;  

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
					JPanel panel = new ConfigureFieldsJPanel();
					tabbedPane.addTab("Fields", null, panel, null);
					tabbedPane.setMnemonicAt(0, KeyEvent.VK_I);
				}
				{
					JPanel panel = new ConfigurePrintingJPanel();
					tabbedPane.addTab("Printing", null, panel, null);
					tabbedPane.setMnemonicAt(1, KeyEvent.VK_R);
				}
				{
					panel_1 = new ConfigurePropertiesJPanel();
					tabbedPane.addTab("Properties", null, panel_1, null);
					tabbedPane.setMnemonicAt(2, KeyEvent.VK_P);
				}
				{
					JPanel panel = new ConfigureDataSourcesJPanel();
					tabbedPane.addTab("Data Sources", null, panel, null);
					
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
						// Force save of any currently editing table cells
						((JPanelWithEditableTable)panel_1).saveInProgressTableChanges();	
						// Grab focus to make sure that changes
						// in currently modal fields get saved
						((JButton)e.getSource()).grabFocus();
						
						// Save Preferences
                        // TODO: Save to file here.
						
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
				// TODO: This doesn't actually do anything, remove?
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
