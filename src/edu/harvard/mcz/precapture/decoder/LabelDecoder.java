/**
 * LabelDecoder.java
 * edu.harvard.mcz.precapture.decoder
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
package edu.harvard.mcz.precapture.decoder;

import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import edu.harvard.mcz.precapture.exceptions.BarcodeReadException;
import edu.harvard.mcz.precapture.ui.ContainerLabel;

/**
 * @author mole
 *
 */
public class LabelDecoder {
	
	private static final Log log = LogFactory.getLog(LabelDecoder.class);
	
	private ContainerLabel label;
	
	/**
	 * Default no argument constructor.
	 */
	public LabelDecoder() { 
		
	}
	
	public LabelDecoder(ContainerLabel containerLabel) { 
		this.label = containerLabel;
	}
	
	public static String decodeImage(BufferedImage image) throws BarcodeReadException { 
		String result = null;
		try { 
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		Map<DecodeHintType,Boolean> hints = new HashMap<DecodeHintType,Boolean>();
		hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
		QRCodeReader reader = new QRCodeReader();
		Result readValue = reader.decode(bitmap, hints);
		result = readValue.getText();
		byte[] dataBytes = result.getBytes("UTF-8");
		result = new String(dataBytes,"UTF-8");
		} catch (NotFoundException e) { 
			throw new BarcodeReadException("Error reading barcode. NotFoundException. " + e.getMessage());
	    } catch (ChecksumException e) { 
			throw new BarcodeReadException("Error reading barcode. ChecksumException. " + e.getMessage());
	    } catch (FormatException e) { 
			throw new BarcodeReadException("Error reading barcode. FormatException. " + e.getMessage());
	    } catch (UnsupportedEncodingException e) { 
			throw new BarcodeReadException("Error reading barcode. UnsupportedEncodingExeption " + e.getMessage());
	    } 
		return result;
	}
	
	public static Result decodeImageToResult(BufferedImage image) throws Exception { 
		Result result = null;
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		QRCodeReader reader = new QRCodeReader();
		result = reader.decode(bitmap);
		return result;		
	}
	
	
}
