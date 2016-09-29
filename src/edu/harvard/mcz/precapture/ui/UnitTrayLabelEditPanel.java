/**
 * UnitTrayLabelEditPanel.java
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

import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.precapture.PreCaptureProperties;
import edu.harvard.mcz.precapture.PreCaptureSingleton;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.RowSorter.SortKey;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * @author mole
 *
 */
public class UnitTrayLabelEditPanel extends JPanel {
	private static final long serialVersionUID = -5306720974718111113L;

	private static final Log log = LogFactory.getLog(UnitTrayLabelEditPanel.class);
	
	private JTable table;
	private JPanel panel;

	/** 
	 * Default no argument constructor, constructs a new UnitTrayLabelEditPanel instance.
	 */
	public UnitTrayLabelEditPanel() {
		new UnitTrayLabelEditPanel(null);
	}
	/**
	 * Construct an instance of a taxon authority file editing panel, with a 
	 * specified parent frame.
	 * 
	 * @param frame
	 */
	public UnitTrayLabelEditPanel(JFrame frame) {
		init();
	}
	
	private void init() {
		setLayout(new BorderLayout(0, 0));
		
		panel = new JPanel();
		add(panel, BorderLayout.SOUTH);

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable();
		table.setModel(new UnitTrayLabelTableModel());
		table.setAutoCreateRowSorter(true);
		scrollPane.setViewportView(table);		
		
		if (PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_EDITABLE_AUTHORITY_FILE).equals("true")) { 
			JButton btnAddRow = new JButton("Add Row");
			btnAddRow.setHorizontalAlignment(SwingConstants.RIGHT);
			btnAddRow.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					((UnitTrayLabelTableModel)table.getModel()).addRow();;
				}
			});
			panel.add(btnAddRow);

			JButton btnZeroPrint = new JButton("To Print to Zero");
			btnZeroPrint.setHorizontalAlignment(SwingConstants.RIGHT);
			btnZeroPrint.setToolTipText("Set number 'To Print' to zero for all rows.");
			btnZeroPrint.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Cursor currentCursor = panel.getCursor();
					panel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					((UnitTrayLabelTableModel)table.getModel()).setAllPrintFlagsTo(0);
					panel.setCursor(currentCursor);
				}
			});
			panel.add(btnZeroPrint);
			
			JButton btnOnePrint = new JButton("To Print to One");
			btnOnePrint.setHorizontalAlignment(SwingConstants.RIGHT);
			btnOnePrint.setToolTipText("Set number 'To Print' to one for all rows.");
			btnOnePrint.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Cursor currentCursor = panel.getCursor();
					panel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					((UnitTrayLabelTableModel)table.getModel()).setAllPrintFlagsTo(1);
					panel.setCursor(currentCursor);
				}
			});
			panel.add(btnOnePrint);			

			JButton btnAddPrint = new JButton("Add to Print List");
			btnAddPrint.setHorizontalAlignment(SwingConstants.RIGHT);
			btnAddPrint.setToolTipText("Copy all rows with 'To Print' greater than zero to the Print List.");
			btnAddPrint.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Cursor currentCursor = panel.getCursor();
					panel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					int rowsAdded = ((UnitTrayLabelTableModel)table.getModel()).addPrintFlaggedToContainerList(table.getRowSorter());
					if (rowsAdded==0) { 
					    JOptionPane.showMessageDialog(table.getParent(), "No rows added, enter the number to add in the 'NumberToPrint' column to add rows to the print list.");
					}
					panel.setCursor(currentCursor);
				}
			});
			panel.add(btnAddPrint);		
		}		

	}

}
