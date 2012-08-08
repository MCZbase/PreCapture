/**
 * TestLabelEncoder.java
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

import static org.junit.Assert.*;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JTextField;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;

import edu.harvard.mcz.precapture.PreCaptureApp;
import edu.harvard.mcz.precapture.PreCaptureProperties;
import edu.harvard.mcz.precapture.PreCaptureSingleton;
import edu.harvard.mcz.precapture.decoder.LabelDecoder;
import edu.harvard.mcz.precapture.encoder.LabelEncoder;
import edu.harvard.mcz.precapture.ui.ContainerLabel;
import edu.harvard.mcz.precapture.ui.FieldPlusText;
import edu.harvard.mcz.precapture.xml.MappingList;

/**
 * @author mole
 *
 */
public class TestLabelEncoder {
	private static final Log log = LogFactory.getLog(TestLabelEncoder.class);

	/** 
	 * Default no argument constructor, constructs a new TestLabelEncoder instance.
	 */
	public TestLabelEncoder() {

	}

	public static BufferedImage resizeImage(Image originalImage, int newWidth, int newHeight) {
    	BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
    	Graphics2D graphics = scaledImage.createGraphics();
    	graphics.setComposite(AlphaComposite.Src);
    	graphics.drawImage(originalImage, 0, 0, newWidth, newHeight, null); 
    	graphics.dispose();
    	return scaledImage;
    }
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// set default herbarium code
		PreCaptureSingleton.getInstance().setProperties(new PreCaptureProperties());
		PreCaptureSingleton.getInstance().getProperties().getProperties().setProperty(PreCaptureProperties.KEY_MY_COLLECTION_CODE,"GH"); 
		
		// Load field mappings from XML 
		String resource = "test/resources/ResourceTest_PrecaptureFields.xml";
		InputStream stream = PreCaptureApp.class.getResourceAsStream(resource);
		if (stream!=null) {
			JAXBContext jc;
			try {

				jc = JAXBContext.newInstance( edu.harvard.mcz.precapture.xml.MappingList.class );
				Unmarshaller u = jc.createUnmarshaller();
				MappingList mappingList = null;
				mappingList = (MappingList)u.unmarshal(stream);
				PreCaptureSingleton.getInstance().setMappingList(mappingList);

				StringBuffer projects = new StringBuffer();
				List<String> lp = mappingList.getSupportedProject();
				Iterator<String> i = lp.iterator();
				while (i.hasNext()) { 
					projects.append(i.next()).append(" ");
				}
				log.debug("Loaded field mappings: " + projects.toString() + mappingList.getVersion());

			} catch (JAXBException e) {
				String message = "Unable to load field mappings.  JAXBException. \nYou may be missing @XmlRootElement(name=FieldMapping) from MappingList.java ";
				// You will need to add the annotation: @XmlRootElement(name="FieldMapping") to MappingList.java
				// if you have regenerated the imagecapture.xml classes from the schema.
				log.error(message);
				log.error(e.getMessage());
				fail(message);
			}
		} else { 
			// getResourceAsStream returns null if loader has an IO exception.
			log.error("Couldn't find resource file: " + resource);
			fail("Couldn't find resource file: " + resource);
		}		
	}

	/**
	 * Test method for {@link edu.harvard.mcz.precapture.encoder.LabelEncoder#getBufferedImage()}.
	 */
	@Test
	public void testGetBufferedImage() {
		// test round trip JSON read write from image (really testing zxing)
		ArrayList<FieldPlusText> textFields = new ArrayList<FieldPlusText>(); 
        int fieldCount = PreCaptureSingleton.getInstance().getMappingList().getFieldInList().size();
	    for (int i=0; i<fieldCount; i++) { 
	    	textFields.add(new FieldPlusText(PreCaptureSingleton.getInstance().getMappingList().getFieldInList().get(i), new JTextField()));
	    } 
		ContainerLabel containerLabel = new ContainerLabel(textFields);
		containerLabel.setNumberToPrint(1);
		
		String json = containerLabel.toJSON();
		String decodedJson = null;
		Map<ResultMetadataType, Object> resultMetadata = null;
		
		LabelEncoder encoder = new LabelEncoder(containerLabel);
		try {
			Result result = LabelDecoder.decodeImageToResult(encoder.getBufferedImage());
			decodedJson = result.getText();
			resultMetadata = result.getResultMetadata();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertTrue(json.equals(decodedJson));
		Set<ResultMetadataType> keys = resultMetadata.keySet();
		Iterator<ResultMetadataType> k = keys.iterator();
		while (k.hasNext()) {
			ResultMetadataType key = k.next();
			log.debug(key.toString() + " " + resultMetadata.get(key).toString());
		}
		
		// test with each field populated with one character
		Iterator<FieldPlusText> i = containerLabel.getFields().iterator();
		int counter = 0;
		String firstKey = "";
		while (i.hasNext()) {
			counter ++;
			FieldPlusText field = i.next();
			field.getTextField().setText(Integer.toString(counter));
			if (counter==1) { 
			    firstKey = field.getField().getCode();
			}
		}
		assert (counter>0);   // test that there is at least one field with data.
		json = containerLabel.toJSON();
		encoder = new LabelEncoder(containerLabel);
		try {
			decodedJson = LabelDecoder.decodeImage(encoder.getBufferedImage());
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertTrue(json.equals(decodedJson));		
		
		// test roundtrip of UTF-8.
		i = containerLabel.getFields().iterator();
		String data = "Üäè⾙";
		while (i.hasNext()) {
			try {
				byte[] dataBytes = data.getBytes("UTF-8");
				data = new String(dataBytes,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i.next().getTextField().setText(data);
		}
		json = containerLabel.toJSON();
		log.debug(json);
		encoder = new LabelEncoder(containerLabel);
		try {
			decodedJson = LabelDecoder.decodeImage(encoder.getBufferedImage());
			log.debug(decodedJson);
			JSONObject jsonObject = JSONObject.fromObject(decodedJson);
			String oneDataElement = jsonObject.get(firstKey).toString();
			assertTrue(data.equals(oneDataElement));
			log.debug(data + " = " + oneDataElement);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertTrue(json.equals(decodedJson));		
		
		

		// test durability against image scaling.
		// Note that a reduction in size by half causes the decoding to fail if 
		// the error correction level is lower than H.
		encoder = new LabelEncoder(containerLabel);
		try {
			BufferedImage image = encoder.getBufferedImage();
			decodedJson = LabelDecoder.decodeImage(this.resizeImage(image, image.getWidth()/2, image.getHeight()/2));
		} catch (Exception e) {
			log.debug(e.getMessage());
			fail(e.getMessage());
		}
	
		assertTrue(json.equals(decodedJson));				
		
		// Runs up to about 150 characters at 50% size reduction before failing.
		// hits limit first if utf-8 is used.
		for (int x=0; x<13; x++) { 
			
			i = containerLabel.getFields().iterator();
			counter = 0;
			while (i.hasNext()) {
			    StringBuffer text = new StringBuffer(); 
			    text.append("è");
			    text.append(Integer.toString(counter));
			    for (int j=0; j<x; j++) { 
				   text.append(Integer.toString(j));
			    }
				counter ++;
				i.next().getTextField().setText(text.toString());
			}
			json = containerLabel.toJSON();
			log.debug(json);
			try {
				log.debug("initial bytes="+json.getBytes("utf-8").length);
			} catch (UnsupportedEncodingException e1) {
				fail(e1.getMessage());
			}
			
			encoder = new LabelEncoder(containerLabel);
			try {
				BufferedImage image = encoder.getBufferedImage();
				decodedJson = LabelDecoder.decodeImage(this.resizeImage(image, image.getWidth()/2, image.getHeight()/2));
				log.debug("decoded bytes=" + decodedJson.length());
			} catch (Exception e) {
				log.debug(e.getMessage());
				fail(e.getMessage());
			}
		
			assertTrue(json.equals(decodedJson));	
		}
		
	}
}
