/**
 * ContainerListPanel.java
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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.precapture.PreCaptureSingleton;
import edu.harvard.mcz.precapture.encoder.LabelEncoder;
import edu.harvard.mcz.precapture.exceptions.PrintFailedException;

/**
 * @author mole
 *
 */
public class ContainerListPanel extends JPanel {
	private static final Log log = LogFactory.getLog(ContainerListPanel.class);

	private JTable tablePrintList;
	private JFrame parent;
	
	public ContainerListPanel(JFrame parent) {
		this.parent = parent;
        init();
	}
	
	private void init() { 
        setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		
		tablePrintList = new JTable();
		tablePrintList.setModel(PreCaptureSingleton.getInstance().getCurrentLabelList());
		scrollPane.setViewportView(tablePrintList);
		
		JPanel panel_6 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_6.getLayout();
		flowLayout_1.setAlignment(FlowLayout.RIGHT);
		add(panel_6, BorderLayout.SOUTH);
		
		JButton btnPrint_1 = new JButton("Print");
		panel_6.add(btnPrint_1);
		btnPrint_1.setMnemonic(KeyEvent.VK_P);
		btnPrint_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				try {
					LabelEncoder.printList(PreCaptureSingleton.getInstance().getCurrentLabelList().getLabels());
				} catch (PrintFailedException e1) {
					log.error(e1.getMessage());
					JOptionPane.showMessageDialog(parent, "Failed to print. " + e1.getMessage());
				}				
			}
		});
		
		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				PreCaptureSingleton.getInstance().getCurrentLabelList().clearList();
			}
		});
		panel_6.add(btnClear);
		
		//TODO: Add popup menu to clone/delete rows.
		
	}
}
