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

import javax.swing.JEditorPane;
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
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;

import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;

import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * @author mole
 *
 */
public class HelpFrame extends JFrame {
	private static final long serialVersionUID = 897844978585110911L;
	private static final Log log = LogFactory.getLog(HelpFrame.class);
	private JPanel contentPane;
	private JFrame frame;

	/** 
	 * Default no argument constructor, constructs a new HelpFrame instance.
	 */
	public HelpFrame() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(HelpFrame.class.getResource("resources/icon.png")));
		setTitle("Application Help");
		frame = this;
		init();
		this.pack();
		log.debug("help frame packed");
	} 
	
	private void init(){ 
		getContentPane().setMinimumSize(new Dimension(800, 500));
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		getContentPane().add(panel, BorderLayout.SOUTH);
		
		JButton btnNewButton = new JButton("Close");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
			}
		});
		btnNewButton.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(btnNewButton);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(800, 500));
		scrollPane.setMinimumSize(new Dimension(600, 300));
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		URL helpURL = PreCaptureApp.class.getResource("resources/ApplicationHelp.html");
		JEditorPane textPane;
		try {
			textPane = new JEditorPane();
			textPane.setContentType("text/html");
			textPane.setPage(helpURL);
			log.debug(((HTMLDocument)textPane.getDocument()).getBase());
			if (helpURL.getProtocol().equals("rsrc")) { 
				// invoked from a jar built with eclipse's jarinjarloader
				helpURL = new URL("http://datashot.sourceforge.net/precapture/ApplicationHelp.html");
				textPane.setPage(helpURL);
			} else { 
			    ((HTMLDocument)textPane.getDocument()).setBase(helpURL);
			    log.debug(((HTMLDocument)textPane.getDocument()).getBase());
			} 
		} catch (IOException e1) {
			log.error(e1.getMessage());
			textPane = new JEditorPane();
			Document doc = textPane.getDocument();
			try {
				doc.insertString(0, "Can't open help resource.", null);
			} catch (BadLocationException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}
		
		log.debug(textPane.getDocument().getLength());
		try { 
		scrollPane.setViewportView(textPane);
		} catch (Exception e) { 
			log.error(e.getMessage());
		}

	}

}