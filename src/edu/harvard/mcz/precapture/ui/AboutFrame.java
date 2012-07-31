/**
 * HelpFrame.java
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


import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.precapture.PreCaptureApp;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.event.KeyEvent;
import java.awt.FlowLayout;
import java.io.IOException;
import java.net.URL;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JEditorPane;
import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * UI Display of information about the application.
 * 
 * @author mole
 *
 */
public class AboutFrame extends JFrame {
	private static final Log log = LogFactory.getLog(AboutFrame.class);
	private JPanel contentPane;
	private JFrame frame;
	private JTextField textFieldVersion;
	private JTextField textFieldSVN;
	private JTextField textFieldAuthors;
	private JTextField textFieldLicense;
	private JTextField textFieldCopyright;
	private JTextField textFieldName;

	/** 
	 * Default no argument constructor, constructs a new AboutFrame instance.
	 */
	public AboutFrame() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(AboutFrame.class.getResource("/edu/harvard/mcz/precapture/resources/icon.png")));
		setTitle("About");
		frame = this;
		init();
		this.pack();
	} 	
		
	private void init() { 
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		getContentPane().add(panel, BorderLayout.SOUTH);
		
		JButton btnNewButton = new JButton("Close");
		btnNewButton.setMnemonic(KeyEvent.VK_C);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
			}
		});
		btnNewButton.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(btnNewButton);
		
		JPanel panel_1 = new JPanel();
		panel_1.setMaximumSize(new Dimension(900, 900));
		getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(250dlu;pref)"),},
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
				FormFactory.MIN_ROWSPEC,}));
		
		JLabel lblName = new JLabel("Name");
		panel_1.add(lblName, "2, 2, right, default");
		
		textFieldName = new JTextField(PreCaptureApp.NAME);
		textFieldName.setEditable(false);
		panel_1.add(textFieldName, "4, 2, fill, default");
		textFieldName.setColumns(10);
		
		JLabel lblVersion = new JLabel("Version");
		panel_1.add(lblVersion, "2, 4, right, default");
		
		textFieldVersion = new JTextField(PreCaptureApp.VERSION);
		textFieldVersion.setEditable(false);
		panel_1.add(textFieldVersion, "4, 4, fill, default");
		textFieldVersion.setColumns(10);
		
		JLabel lblSvn = new JLabel("SVN ID");
		panel_1.add(lblSvn, "2, 6, right, default");
		
		textFieldSVN = new JTextField(PreCaptureApp.SVN_ID);
		textFieldSVN.setEditable(false);
		panel_1.add(textFieldSVN, "4, 6, fill, default");
		textFieldSVN.setColumns(10);
		
		JLabel lblAuthors = new JLabel("Authors");
		panel_1.add(lblAuthors, "2, 8, right, default");
		
		textFieldAuthors = new JTextField(PreCaptureApp.AUTHORS);
		textFieldAuthors.setEditable(false);
		panel_1.add(textFieldAuthors, "4, 8, fill, default");
		textFieldAuthors.setColumns(10);
		
		JLabel lblCopyright = new JLabel("Copyright");
		panel_1.add(lblCopyright, "2, 10, right, default");
		
		textFieldCopyright = new JTextField(PreCaptureApp.COPYRIGHT);
		textFieldCopyright.setEditable(false);
		panel_1.add(textFieldCopyright, "4, 10, fill, default");
		textFieldCopyright.setColumns(10);
		
		JLabel lblLicense = new JLabel("License");
		panel_1.add(lblLicense, "2, 12, right, default");
		
		textFieldLicense = new JTextField(PreCaptureApp.LICENSE);
		textFieldLicense.setEditable(false);
		panel_1.add(textFieldLicense, "4, 12, fill, default");
		textFieldLicense.setColumns(10);
		
		URL helpURL = PreCaptureApp.class.getResource("resources/gpl-2.0.txt");
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setMinimumSize(new Dimension(600, 400));
		scrollPane.setMaximumSize(new Dimension(600, 600));
		panel_1.add(scrollPane, "2, 14, 3, 1, fill, top");
		
		JEditorPane editorPane;
		try {
			editorPane = new JEditorPane(helpURL);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			editorPane = new JEditorPane();
		}
		scrollPane.setViewportView(editorPane);
		
	}

}