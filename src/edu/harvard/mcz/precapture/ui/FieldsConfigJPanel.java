/**
 * FieldsConfigJPanel.java
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

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.precapture.PreCaptureProperties;
import edu.harvard.mcz.precapture.PreCaptureSingleton;
import edu.harvard.mcz.precapture.xml.MappingTableModel;
import javax.swing.JLabel;
import java.awt.FlowLayout;

/**
 * @author mole
 *
 */
public class FieldsConfigJPanel extends JPanel {
	private static final long serialVersionUID = -7371846617414565995L;

	private static final Log log = LogFactory.getLog(FieldsConfigJPanel.class);

	private JTable table;
	private JLabel textField;
	
	/** 
	 * Default no argument constructor, constructs a new FieldsConfigJPanel instance.
	 */
	public FieldsConfigJPanel() {
       init();
	}
	
	private void init() { 
		setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane_1 = new JScrollPane();
			add(scrollPane_1, BorderLayout.CENTER);
			{
				table = new JTable();
				TableModel model = new MappingTableModel(PreCaptureSingleton.getInstance().getMappingList());
				table.setModel(model);
				scrollPane_1.setViewportView(table);
			}
		}
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(panel, BorderLayout.NORTH);
		
		JLabel lblSource = new JLabel("Source:");
		panel.add(lblSource);
		
		textField = new JLabel();
		textField.setText(PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_FIELDMAPPING));
		panel.add(textField);
	}
}
