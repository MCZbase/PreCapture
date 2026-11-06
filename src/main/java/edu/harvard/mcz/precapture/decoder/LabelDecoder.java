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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JTextField;

import org.json.JSONObject;

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

import edu.harvard.mcz.precapture.PreCaptureSingleton;
import edu.harvard.mcz.precapture.exceptions.BarcodeReadException;
import edu.harvard.mcz.precapture.ui.ContainerLabel;
import edu.harvard.mcz.precapture.ui.FieldPlusText;
import edu.harvard.mcz.precapture.xml.Field;
import edu.harvard.mcz.precapture.xml.MappingList;

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
	
	/**
	 * LabelDecoder for a particular container label.
	 * 
	 * No supporting functionality implemented yet.  
	 * Use the static decodeImage and decodeImageToResult methods.
	 * 
	 * May be removed.
	 * 
	 * @param containerLabel
	 */
	public LabelDecoder(ContainerLabel containerLabel) { 
		// TODO: Implement or remove.
		this.label = containerLabel;
	}

	/**
	 * Obtain a value from a JSON object as a string.
	 * 
	 * @param json the JSON object
	 * @param key the key for the value
	 * @return the value as a string, or null if the key is absent or its value is JSON null.
	 */
	private static String getString(JSONObject json, String key) { 
		Object value = json.opt(key);
		if (value == null || JSONObject.NULL.equals(value)) { 
			return null;
		}
		return value.toString();
	}

	public static Map<String,String> getVocabularyValueMap(String jsonString) {
		HashMap<String,String> result = new HashMap<String,String>();
	     JSONObject json = new JSONObject(jsonString);
	     String project = getString(json, "m1p");
	     String version = getString(json, "m2v");
	     
	     boolean foundMapping = false;
	     // Check if current mapping list is the one used in this json
	     MappingList mapping =  PreCaptureSingleton.getInstance().getMappingList();
	     List<String> projects = mapping.getSupportedProject();
	     Iterator<String> i = projects.iterator();
	     while (!foundMapping && i.hasNext()) { 
	         if (i.next().equals(project) && mapping.getVersion().equals(version)) { 
	        	 foundMapping = true;
	         }
	     }
	     //TODO Find other mappings
	     
	     if (foundMapping) { 
	    	 Iterator<String> keys = json.keys();
	    	 while (keys.hasNext()) { 
	    		 String key = keys.next();
	    		 String term = getTermForKey(mapping,key);
	    		 String value = getString(json, key);
	    		 log.debug(key + ":" + term + ":" + value );
	    		 result.put(term, value);
	    	 }
	     }
	     return result;
	}
	
	/**
	 * Given a string containing JSON, construct a ContanierLabel object
	 * 
	 * @param jsonString
	 * @return
	 */
	public static ContainerLabel createLabelFromJson(String jsonString) { 
	     ContainerLabel result = null;
	     JSONObject json = new JSONObject(jsonString);
	     String project = getString(json, "m1p");
	     String version = getString(json, "m2v");
	     log.debug("Project: " + project);
	     log.debug("Version: " + version);
	     
	     boolean foundMapping = false;
	     // Check if current mapping list is the one used in this json
	     MappingList mapping =  PreCaptureSingleton.getInstance().getMappingList();
	     List<String> projects = mapping.getSupportedProject();
	     Iterator<String> i = projects.iterator();
	     while (!foundMapping && i.hasNext()) {
	    	 String projectInMapping = i.next();
	         if (
	        		 (projectInMapping.equals(project) || project.equals("["+projectInMapping+"]") )
	        		 && mapping.getVersion().equals(version)
	            ) { 
	        	 foundMapping = true;
	         }
	     }
	     //TODO Find other mappings
	     
	     if (foundMapping) { 
	 		 ArrayList<FieldPlusText> textFields = new ArrayList<FieldPlusText>(); 
	         int fieldCount = PreCaptureSingleton.getInstance().getMappingList().getFieldInList().size();
		     for (int j=0; j<fieldCount; j++) { 
		     	textFields.add(new FieldPlusText(PreCaptureSingleton.getInstance().getMappingList().getFieldInList().get(j), new JTextField()));
		     } 	    	 
	    	 result = new ContainerLabel(textFields);
	    	 Iterator<String> keys = json.keys();
	    	 while (keys.hasNext()) { 
	    		 String key = keys.next();
	    		 String term = getTermForKey(mapping,key);
	    		 String value = getString(json, key);

	    		 ArrayList<FieldPlusText> fields = result.getFields();
	    		 Iterator<FieldPlusText> f = fields.iterator();
	    		 while (f.hasNext()) { 
	    			 FieldPlusText field = f.next();
	    			 if (field.getField().getVocabularyTerm().equals(term)) { 
	    				 field.getTextField().setText(value);
	    			 }
	    		 }
	    		 log.debug(key + ":" + value );
	    	 }
	     } else { 
	    	 log.error("Failed to find a configured mapping in JSON, check project ("+ project +") and version ("+ version +").");
	    	 log.debug("Supported Version: " + mapping.getVersion());
	         log.debug("Supported Projects:");
	         i = projects.iterator();
	         while (i.hasNext()) { 
	        	 log.debug(i.next());
	         }
	     }
	     
	     return result;
	}
	
	/**
	 * Given a mapping list and a key, return the vocabulary term for that key
	 * 
	 * @param mappings the mapping definition to check for the code and vocabulary term.
	 * @param key the short code used in the JSON as a key for a field concept.
	 * @return string value of the vocabulary term that the code translates to.
	 */
	public static String getTermForKey(MappingList mappings, String key) {
		String result = null;
		List<Field> fields = mappings.getFieldInList();
		Iterator<Field> i = fields.iterator();
		while (i.hasNext()) { 
			Field field = i.next();
			if (field.getCode().equals(key)) { 
				result = field.getVocabularyTerm();
			}
		}
		return result;
	}

	/**
	 * Given an image, returns the decoded text of a QRCode within that image.
	 * 
	 * @param image a bufferedimage containing a QRCode barcode 
	 * @return the text encoded in the QRCode.
	 * @throws BarcodeReadException if unable to read the barcode, recasts 4 different 
	 * of ZXing exceptions as common BarcodeReadException.
	 */
	public static String decodeImage(BufferedImage image) throws BarcodeReadException { 
		String result = null;
		try { 
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		Map<DecodeHintType,Boolean> hints = new HashMap<DecodeHintType,Boolean>();
		hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
		Result readValue = decodeWithFallback(bitmap, hints);
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
	
	/**
	 * Given an image containing a QRCode, returns a ZXing result object that 
	 * wraps the text of the QRCode in metadata about the read.
	 * 
	 * @param image a buffered image in which to look for a barcode.
	 * @return a Result object, from which Result.getText() will return the text
	 * of the barcode. @see com.google.zxing.Result
	 * @throws Exception if there is a problem with the image or barcode.
	 */
	public static Result decodeImageToResult(BufferedImage image) throws Exception { 
		Result result = null;
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		result = decodeWithFallback(bitmap, new HashMap<DecodeHintType,Boolean>());
		return result;		
	}
	
	/**
	 * Decode a QR code, if the finder patterns can't be located, retry treating 
	 * the image as a pure barcode (just the code and its quiet zone, as produced by 
	 * LabelEncoder.getBufferedImage()).  The zxing 3.x detector can't locate finder 
	 * patterns in such images when they are at 1 pixel per module.
	 * 
	 * @param bitmap the image to decode
	 * @param hints decoding hints, not modified.
	 * @return the decoded result
	 */
	private static Result decodeWithFallback(BinaryBitmap bitmap, Map<DecodeHintType,Boolean> hints) throws NotFoundException, ChecksumException, FormatException { 
		QRCodeReader reader = new QRCodeReader();
		try { 
			return reader.decode(bitmap, hints);
		} catch (NotFoundException e) { 
			Map<DecodeHintType,Boolean> pureHints = new HashMap<DecodeHintType,Boolean>(hints);
			pureHints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
			return reader.decode(bitmap, pureHints);
		}
	}
	
	
}
