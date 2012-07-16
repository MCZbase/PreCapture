/**
 * FieldPlusText.java
 * edu.harvard.mcz.imagecapture.ui
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
package edu.harvard.mcz.imagecapture.ui;

import java.awt.TextField;

import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.xml.Field;

/**
 * Structure to hold a field definition along with a text input field that
 * goes with that definition.
 * 
 * @author mole
 *
 */
public class FieldPlusText {
	private static final Log log = LogFactory.getLog(FieldPlusText.class);

	private Field field;
	private JTextField textField;
	
	/** 
	 * Default no argument constructor, constructs a new FieldPlusText instance.
	 */
	public FieldPlusText() {

	}

	/**
	 * Construct a Field/TextField pair.
	 * 
	 * @param field
	 * @param textField
	 */
	public FieldPlusText(Field field, JTextField textField) {
		super();
		this.field = field;
		this.textField = textField;
	}

	/**
	 * @return the field
	 */
	public Field getField() {
		return field;
	}

	/**
	 * @return the textField
	 */
	public JTextField getTextField() {
		return textField;
	}

	protected FieldPlusText clone() {
		FieldPlusText result = new FieldPlusText(field,new JTextField());
		result.getTextField().setText(this.getTextField().getText());
		return result;
	}
	
	
	
	
}
