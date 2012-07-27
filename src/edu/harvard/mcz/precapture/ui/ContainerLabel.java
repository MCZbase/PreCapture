/**
 * ContainerLabel.java
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

import java.awt.Color;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;

import edu.harvard.mcz.precapture.PreCaptureSingleton;
import edu.harvard.mcz.precapture.encoder.LabelEncoder;
import edu.harvard.mcz.precapture.xml.labels.LabelDefinitionType;
import edu.harvard.mcz.precapture.xml.labels.TextOrentationType;

/**
 * The list of fields that go with a single label for a container (a folder,
 * or unit tray or other container).
 * 
 * @author mole
 *
 */
public class ContainerLabel {
	private static final Log log = LogFactory.getLog(ContainerLabel.class);

	private int numberToPrint = 0;
	
	private ArrayList<FieldPlusText> fields;
	
	/** 
	 * Default no argument constructor, constructs a new ContainerLabel instance.
	 */
	public ContainerLabel() {
		numberToPrint = 1;
        this.fields = new ArrayList<FieldPlusText>();
	}
	
	
	public ContainerLabel(ArrayList<FieldPlusText> fields) {
		numberToPrint = 1;
        this.fields = fields;
	}


	/**
	 * @return the fields
	 */
	public ArrayList<FieldPlusText> getFields() {
		return fields;
	}	
	
	
	/**
	 * @return the numberToPrint
	 */
	public int getNumberToPrint() {
		return numberToPrint;
	}


	/**
	 * Set how many copies of this label to print.
	 * 
	 * @param numberToPrint the numberToPrint to set
	 */
	public void setNumberToPrint(int numberToPrint) {
		this.numberToPrint = numberToPrint;
	}
	
	/**
	 * Resets number to print to 1 and content of each text field to an empty string.
	 */
	public void resetToBlank() { 
		numberToPrint = 1;
		for (int i = 0; i<fields.size(); i++) { 
			if (!fields.get(i).getField().getVocabularyTerm().equals("dwc:genus")) { 
			    fields.get(i).getTextField().setText("");
			}
		}
	}

    /**
     * Get a fragmentary JSON encoding of the project and version of the precapture field
     * configuration used to encode the field names in JSON.
     * 
     * @return a JSON represenation of the metadata.
     */
	public String metadataToJSON() { 
		StringBuffer data = new StringBuffer();
		data.append("\"m1p\":\"").append(PreCaptureSingleton.getInstance().getMappingList().getSupportedProject()).append("\", ");
		data.append("\"m2v\":\"").append(PreCaptureSingleton.getInstance().getMappingList().getVersion()).append("\", ");
		return data.toString();
	}
	
	/**
	 * 
	 * @return a JSON representation of the keys and values of the fields in this set.
	 */
	public String toJSON() { 

		StringBuffer data = new StringBuffer(); 
		data.append("{ ");
		data.append(metadataToJSON());
		String comma = "";
		for (int i=0;i<fields.size();i++) { 
			if (!fields.get(i).getTextField().getText().isEmpty()) { 
				data.append(comma);
				data.append("\"").append(fields.get(i).getField().getCode()).append("\":\"").append(fields.get(i).getTextField().getText()).append("\"");
				comma = ", ";
			}
		}
		data.append(" }");	

		return data.toString();

	}
	
	public String toString() { 
		StringBuffer data = new StringBuffer(); 
		for (int i=0;i<fields.size();i++) { 
			if (!fields.get(i).getTextField().getText().isEmpty()) { 
				data.append(fields.get(i).getTextField().getText()).append(" ");
			}
		}
		return data.toString();
	}
	
	/**
	 * 
	 * @return a PDF paragraph cell containing a text encoding of the fields in this set.
	 */
	public PdfPCell toPDFCell(LabelDefinitionType printDefinition) { 
		PdfPCell cell = new PdfPCell();
		;
		if (printDefinition.getTextOrentation().toString().toLowerCase().equals(TextOrentationType.VERTICAL.toString().toLowerCase())) { 
			log.debug("Print orientation of text is Vertical");
			cell.setRotation(90);
			cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
		}
		cell.setBorderColor(BaseColor.LIGHT_GRAY);
		cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
		cell.disableBorderSide(PdfPCell.RIGHT);
		cell.setPaddingLeft(3);

		Paragraph higher = new Paragraph();
		boolean added = false;
		boolean hasContent = false;
		for (int i=0;i<fields.size();i++) { 
		   if (fields.get(i).getField().isNewLine()) {
			   if (hasContent) { 
				   cell.addElement(higher);
			   }
			   higher = new Paragraph();
			   added = false;
			   hasContent = false;
		   }
		   Chunk chunk = new Chunk(fields.get(i).getTextField().getText().trim()); 
		   if (fields.get(i).getField().isUseItalic()) {
		       chunk.setFont(new Font(Font.FontFamily.TIMES_ROMAN, 
		    		   fields.get(i).getField().getFontSize(), 
		    		   Font.ITALIC));
		   } else { 
		       chunk.setFont(new Font(Font.FontFamily.TIMES_ROMAN, 
		    		   fields.get(i).getField().getFontSize(), 
		    		   Font.NORMAL));
		   }
		   hasContent = true;
		   higher.add(chunk);
		   if (fields.get(i).getTextField().getText().trim().length()>0) { 
			   // add a trailing space as a separator if there was something to separate.
			   higher.add(new Chunk(" "));
		   }
		} 
		if (!added) { 
			cell.addElement(higher);
		}

		return cell;
	}

	protected ContainerLabel clone() {
		ContainerLabel result = new ContainerLabel(); 
	    result.setNumberToPrint(numberToPrint);
	    for (int i = 0; i< this.fields.size(); i++) {
	    	result.getFields().add(getFields().get(i).clone());
	    }
	    return result;
	}
	
	
}
