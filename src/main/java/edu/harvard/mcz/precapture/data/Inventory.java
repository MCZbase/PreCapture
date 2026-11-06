/**
 * Inventory.java
 * edu.harvard.mcz.precapture.data
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
package edu.harvard.mcz.precapture.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mole
 *
 */
@Entity
@Table(name = "INVENTORY")
public class Inventory  {
	private static final Log log = LogFactory.getLog(Inventory.class);

	private Long id;
	private String cabinet;
	private String taxon;
	private Float thickness;
	private Float sheetsPerUnitThickness;
	
	
	/** 
	 * Default no argument constructor, constructs a new Inventory instance.
	 */
	public Inventory() {
         cabinet = "";
         taxon = "";
         thickness = 0f;
         sheetsPerUnitThickness = 0f;
	}

	/**
	 * @return the cabinet
	 */
	@Column(name = "Cabinet", length = 40)
	public String getCabinet() {
		return cabinet;
	}

	/**
	 * @param cabinet the cabinet to set
	 */
	public void setCabinet(String cabinet) {
		this.cabinet = cabinet;
	}

	/**
	 * @return the taxon
	 */
	@Column(name = "Taxon", length = 40)
	public String getTaxon() {
		return taxon;
	}

	/**
	 * @param taxon the taxon to set
	 */
	public void setTaxon(String taxon) {
		this.taxon = taxon;
	}

	/**
	 * @return the thickness
	 */
	@Column(name = "Thickness")
	public float getThickness() {
		return thickness;
	}

	/**
	 * @param thickness the thickness to set
	 */
	public void setThickness(float thickness) {
		this.thickness = thickness;
	}

	/**
	 * @return the sheetsPerUnitThickness
	 */
	@Column(name = "SheetsPerThickness")
	public float getSheetsPerUnitThickness() {
		return sheetsPerUnitThickness;
	}

	/**
	 * @param sheetsPerUnitThickness the sheetsPerUnitThickness to set
	 */
	public void setSheetsPerUnitThickness(float sheetsPerUnitThickness) {
		this.sheetsPerUnitThickness = sheetsPerUnitThickness;
	}

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	
}
