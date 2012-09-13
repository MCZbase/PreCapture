/**
 * ConfigurePropertiesJPanel.java
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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import edu.harvard.mcz.precapture.PreCaptureSingleton;
import edu.harvard.mcz.precapture.data.HibernateUtil;
import edu.harvard.mcz.precapture.interfaces.JPanelWithEditableTable;

import javax.swing.JTable;
import java.awt.BorderLayout;

/**
 * @author mole
 *
 */
public class ConfigurePropertiesJPanel extends JPanel implements JPanelWithEditableTable {
	private static final long serialVersionUID = 5193156023117363717L;

	private static final Log log = LogFactory.getLog(ConfigurePropertiesJPanel.class);

	private JTextField txtDerby;
	private JTable table;
	
	/** 
	 * Default no argument constructor, constructs a new ConfigurePropertiesJPanel instance.
	 */
	public ConfigurePropertiesJPanel() {
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
				RowSpec.decode("default:grow"),}));
		{
			JLabel lblDatabase = new JLabel("Database Driver");
			add(lblDatabase, "2, 2, right, default");
		}
		{
			txtDerby = new JTextField();
			txtDerby.setEditable(false);
			txtDerby.setText(HibernateUtil.getDriverClassString());
			add(txtDerby, "4, 2, fill, default");
			txtDerby.setColumns(20);
		}
		{
			JLabel lblDatabaseConn = new JLabel("Database Connection");
			add(lblDatabaseConn, "2, 4, right, default");
		}
		{
			JTextField txtDerbyConn = new JTextField();
			txtDerbyConn.setEditable(false);
			txtDerbyConn.setText(HibernateUtil.getConnectionURL());
			add(txtDerbyConn, "4, 4, fill, default");
			txtDerbyConn.setColumns(20);
		}
		{
			JPanel panel = new JPanel();
			add(panel, "2, 6, 3, 1, fill, fill");
			panel.setLayout(new BorderLayout(0, 0));
			{
				table = new JTable(PreCaptureSingleton.getInstance().getProperties());
				panel.add(table,BorderLayout.CENTER);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.harvard.mcz.precapture.ui.JPanelWithEditableTable#saveInProgressTableChanges()
	 */
	@Override
	public void saveInProgressTableChanges() { 
		if (table.getCellEditor() != null) { 
			table.getCellEditor().stopCellEditing(); 
		}
	}
}
