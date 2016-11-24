/**
 * InventoryPanel.java
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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.precapture.data.Inventory;
import edu.harvard.mcz.precapture.data.InventoryLifeCycle;
import edu.harvard.mcz.precapture.exceptions.SaveFailedException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

/**
 * @author mole
 *
 */
public class InventoryPanel extends JPanel {
	private static final long serialVersionUID = 1697467900993700439L;
	private static final Log log = LogFactory.getLog(InventoryPanel.class);

	private JTable tableInventory;
	private JFrame frame;
	private JPopupMenu popupMenu;
	private int clickedOnRow;
	
	public InventoryPanel(JFrame containingFrame) {
		frame = containingFrame;
        init();
	}
	
	private void init() { 
		setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_1 = new JScrollPane();
		add(scrollPane_1, BorderLayout.CENTER);

		tableInventory = new JTable();
		tableInventory.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) { 
					 clickedOnRow = ((JTable)e.getComponent()).getSelectedRow();
				     popupMenu.show(e.getComponent(),e.getX(),e.getY());
				}
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) { 
					 clickedOnRow = ((JTable)e.getComponent()).getSelectedRow();
				     popupMenu.show(e.getComponent(),e.getX(),e.getY());
				}
			}
		});
		InventoryLifeCycle ils = new InventoryLifeCycle();
		InventoryTableModel iTableModel = new InventoryTableModel((ArrayList<Inventory>)ils.findAll());
		tableInventory.setModel(iTableModel);
		scrollPane_1.setViewportView(tableInventory);
		
		popupMenu = new JPopupMenu();
		
		JMenuItem mntmAddRow = new JMenuItem("Add Row");
		mntmAddRow.setMnemonic(KeyEvent.VK_A);
		mntmAddRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				try { 
				    ((InventoryTableModel)tableInventory.getModel()).addRow();
				} catch (Exception ex) { 
					log.error(ex.getMessage());
					JOptionPane.showMessageDialog(frame, "Failed to add an Inventory row. " + ex.getMessage());
				}
			}
		});	
		popupMenu.add(mntmAddRow);		
		
		JMenuItem mntmCloneRow = new JMenuItem("Clone Row");
		mntmCloneRow.setMnemonic(KeyEvent.VK_C);
		mntmCloneRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				try { 
					if (clickedOnRow<0) { 
				       ((InventoryTableModel)tableInventory.getModel()).addRow();
					} else { 
				       ((InventoryTableModel)tableInventory.getModel()).cloneRow(clickedOnRow);
					}
				} catch (Exception ex) { 
					log.error(ex.getMessage());
					JOptionPane.showMessageDialog(frame, "Failed to clone an Inventory row. " + ex.getMessage());
				}
			}
		});	
		popupMenu.add(mntmCloneRow);
		
		JMenuItem mntmDeleteRow = new JMenuItem("Delete Row");
		mntmDeleteRow.setMnemonic(KeyEvent.VK_D);
		mntmDeleteRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				try { 
					if (clickedOnRow>=0) { 
				       ((InventoryTableModel)tableInventory.getModel()).deleteRow(clickedOnRow);
					}
				} catch (Exception ex) { 
					log.error(ex.getMessage());
					JOptionPane.showMessageDialog(frame, "Failed to delete an Inventory row. " + ex.getMessage());
				}
			}
		});	
		popupMenu.addSeparator();		
		popupMenu.add(mntmDeleteRow);		
		
		JPanel panel_8 = new JPanel();
		FlowLayout flowLayout_8 = (FlowLayout) panel_8.getLayout();
		flowLayout_8.setAlignment(FlowLayout.RIGHT);
		add(panel_8, BorderLayout.SOUTH);
		
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
