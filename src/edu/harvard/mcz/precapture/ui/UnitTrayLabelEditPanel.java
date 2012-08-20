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
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * @author mole
 *
 */
public class UnitTrayLabelEditPanel extends JPanel {
	private static final Log log = LogFactory
			.getLog(UnitTrayLabelEditPanel.class);
	private JTable table;

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
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);
		
		JButton btnAddRow = new JButton("Add Row");
		btnAddRow.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(btnAddRow);
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable();
		table.setModel(new UnitTrayLabelTableModel());
		scrollPane.setViewportView(table);

	}

}
