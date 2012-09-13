/**
 * ConfigurePrintingJPanel.java
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import edu.harvard.mcz.precapture.PreCaptureProperties;
import edu.harvard.mcz.precapture.PreCaptureSingleton;
import edu.harvard.mcz.precapture.xml.labels.LabelDefinitionListTypeTableModel;
import edu.harvard.mcz.precapture.xml.labels.LabelDefinitionType;

/**
 * @author mole
 *
 */
public class ConfigurePrintingJPanel extends JPanel {
	private static final long serialVersionUID = 2771323865516346762L;
	private static final Log log = LogFactory.getLog(ConfigurePrintingJPanel.class);

	private JComboBox comboBoxPrintFormat;
	private JTable tablePrintFormatList;
	
	/** 
	 * Default no argument constructor, constructs a new ConfigurePrintingJPanel instance.
	 */
	public ConfigurePrintingJPanel() {
       init();
	}
	
	private void init() { 
		setLayout(new FormLayout(new ColumnSpec[] {
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
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,				
				RowSpec.decode("default:grow"),}));
		{
			JLabel lblPaperSize = new JLabel("Selected Printing Format");
			add(lblPaperSize, "2, 2, right, default");
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
		
		add(comboBoxPrintFormat, "4, 2, fill, default");
		{
			JLabel lblAvailablePrintingFormats = new JLabel("Available Printing Formats");
			add(lblAvailablePrintingFormats, "2, 4, 3, 1, center, default");
			JLabel formatSource = new JLabel("Source: " + PreCaptureSingleton.getInstance().getProperties().getProperties().getProperty(PreCaptureProperties.KEY_PRINTDEFINITIONS));
			add(formatSource, "2, 6, 3, 1, center, default");
		}
		{
			JScrollPane scrollPane_1 = new JScrollPane();
			add(scrollPane_1, "2, 8, 3, 1, fill, fill");
			{
				tablePrintFormatList = new JTable();
				tablePrintFormatList.setModel(new LabelDefinitionListTypeTableModel(PreCaptureSingleton.getInstance().getPrintFormatDefinitionList()));
				// disabling table so that users don't try to select a print format by selecting a row in the table.
				tablePrintFormatList.setEnabled(false);
				scrollPane_1.setViewportView(tablePrintFormatList);
			}
		}
	}
	
}
