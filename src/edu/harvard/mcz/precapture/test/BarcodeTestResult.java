/**
 * BarcodeTestResult.java
 * edu.harvard.mcz.precapture.test
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
package edu.harvard.mcz.precapture.test;

import javax.swing.Icon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mole
 *
 */
public class BarcodeTestResult {
	private static final Log log = LogFactory.getLog(BarcodeTestResult.class);

	private int bytes;
	private int originalWidth;
	private int scaledToWidth;
	private String message;
	private Icon originalImage;
	private Icon scaledImage;


	/** 
	 * Default no argument constructor, constructs a new BarcodeTestResult instance.
	 */
	public BarcodeTestResult() {

	}


	/**
	 * @param bytes
	 * @param originalWidth
	 * @param scaledToWidth
	 * @param message
	 * @param originalImage
	 * @param scaledImage
	 */
	public BarcodeTestResult(int bytes, int originalWidth, int scaledToWidth,
			String message, Icon originalImage, Icon scaledImage) {
		super();
		this.bytes = bytes;
		this.originalWidth = originalWidth;
		this.scaledToWidth = scaledToWidth;
		this.message = message;
		this.originalImage = originalImage;
		this.scaledImage = scaledImage;
	}


	/**
	 * @return the bytes
	 */
	public int getBytes() {
		return bytes;
	}


	/**
	 * @param bytes the bytes to set
	 */
	public void setBytes(int bytes) {
		this.bytes = bytes;
	}


	/**
	 * @return the originalWidth
	 */
	public int getOriginalWidth() {
		return originalWidth;
	}


	/**
	 * @param originalWidth the originalWidth to set
	 */
	public void setOriginalWidth(int originalWidth) {
		this.originalWidth = originalWidth;
	}


	/**
	 * @return the scaledToWidth
	 */
	public int getScaledToWidth() {
		return scaledToWidth;
	}


	/**
	 * @param scaledToWidth the scaledToWidth to set
	 */
	public void setScaledToWidth(int scaledToWidth) {
		this.scaledToWidth = scaledToWidth;
	}


	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}


	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}


	/**
	 * @return the originalImage
	 */
	public Icon getOriginalImage() {
		return originalImage;
	}


	/**
	 * @param originalImage the originalImage to set
	 */
	public void setOriginalImage(Icon originalImage) {
		this.originalImage = originalImage;
	}


	/**
	 * @return the scaledImage
	 */
	public Icon getScaledImage() {
		return scaledImage;
	}


	/**
	 * @param scaledImage the scaledImage to set
	 */
	public void setScaledImage(Icon scaledImage) {
		this.scaledImage = scaledImage;
	}
	
	
}
