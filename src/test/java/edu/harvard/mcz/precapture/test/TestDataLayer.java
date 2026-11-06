/**
 * TestDataLayer.java
 * edu.harvard.mcz.precapture.test
 * Copyright © 2026 President and Fellows of Harvard College
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
 */
package edu.harvard.mcz.precapture.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.harvard.mcz.precapture.data.HibernateUtil;
import edu.harvard.mcz.precapture.data.Inventory;
import edu.harvard.mcz.precapture.data.InventoryLifeCycle;
import edu.harvard.mcz.precapture.data.UnitTrayLabel;
import edu.harvard.mcz.precapture.data.UnitTrayLabelLifeCycle;

/**
 * Tests of the persistence layer against an H2 database in a temporary directory.
 */
public class TestDataLayer {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private File databaseBase;

	@Before
	public void setUp() throws Exception {
		databaseBase = new File(folder.getRoot(), "PRECAPTURE2");
		HibernateUtil.setDatabasePath(databaseBase.getPath());
	}

	@After
	public void tearDown() throws Exception {
		HibernateUtil.terminateSessionFactory();
		HibernateUtil.setDatabasePath(null);
	}

	@Test
	public void testDatabaseLocation() {
		assertTrue(HibernateUtil.getConnectionURL().startsWith("jdbc:h2:file:" + databaseBase.getPath() + ";"));
		HibernateUtil.getSessionFactory();
		assertTrue(new File(databaseBase.getPath() + ".mv.db").exists());
		assertEquals("jdbc:h2:file:/data/db;INIT=create schema IF NOT EXISTS precapture",
				HibernateUtil.replaceDatabasePath(" jdbc:h2:file:./PRECAPTURE2;INIT=create schema IF NOT EXISTS precapture", "/data/db"));
		assertEquals("jdbc:h2:file:/data/db", HibernateUtil.replaceDatabasePath("jdbc:h2:PRECAPTURE2", "/data/db"));
		assertNull(HibernateUtil.replaceDatabasePath("jdbc:mysql://localhost:3306/precapture", "/data/db"));
		assertEquals("./data/PRECAPTURE2", HibernateUtil.normalizeDatabasePath(" data/PRECAPTURE2.mv.db "));
		assertEquals("./PRECAPTURE2", HibernateUtil.normalizeDatabasePath("./PRECAPTURE2"));
		assertEquals("../PRECAPTURE2", HibernateUtil.normalizeDatabasePath("../PRECAPTURE2"));
		assertEquals("~/PRECAPTURE2", HibernateUtil.normalizeDatabasePath("~/PRECAPTURE2"));
		assertEquals("/data/PRECAPTURE2", HibernateUtil.normalizeDatabasePath("/data/PRECAPTURE2"));
		assertNull(HibernateUtil.normalizeDatabasePath(" "));
	}

	/** Run sql directly against the test database, with the session factory closed. */
	private void executeSql(String sql) throws Exception {
		HibernateUtil.terminateSessionFactory();
		try (Connection connection = DriverManager.getConnection("jdbc:h2:file:" + databaseBase.getPath(), "sa", "")) {
			connection.createStatement().execute(sql);
		}
	}

	private void persistOneOfEach() throws Exception {
		UnitTrayLabel label = new UnitTrayLabel();
		label.setGenus("Malaxis");
		label.setOrdinal(4);
		new UnitTrayLabelLifeCycle().persist(label);
		Inventory inventory = new Inventory();
		inventory.setCabinet("C1");
		new InventoryLifeCycle().persist(inventory);
	}

	@Test
	public void testExistingDataKeptAtStartup() throws Exception {
		persistOneOfEach();
		HibernateUtil.terminateSessionFactory();
		// a new session factory, as at application startup
		assertEquals(1, new UnitTrayLabelLifeCycle().countAtStartup());
		assertEquals(1, new InventoryLifeCycle().countAtStartup());
	}

	@Test
	public void testMissingTableCreatedAtStartup() throws Exception {
		persistOneOfEach();
		executeSql("drop table PUBLIC.INVENTORY");
		assertEquals(0, new InventoryLifeCycle().countAtStartup());
		assertEquals(1, new UnitTrayLabelLifeCycle().countAtStartup());
	}

	@Test
	public void testMissingColumnAddedAtStartup() throws Exception {
		persistOneOfEach();
		executeSql("alter table PUBLIC.UNIT_TRAY_LABEL drop column Ordinal");
		UnitTrayLabelLifeCycle uls = new UnitTrayLabelLifeCycle();
		assertEquals(1, uls.countAtStartup());
		assertEquals("Malaxis", uls.findAll().get(0).getGenus());
		assertEquals(Integer.valueOf(0), uls.findMaxOrdinal());
	}

	@Test
	public void testUnitTrayLabelLifeCycle() throws Exception {
		UnitTrayLabelLifeCycle uls = new UnitTrayLabelLifeCycle();
		assertEquals(0, uls.count());
		assertEquals(Integer.valueOf(0), uls.findMaxOrdinal());

		UnitTrayLabel label = new UnitTrayLabel();
		label.setFamily(" Orchidaceae ");
		label.setGenus("Malaxis");
		label.setSpecificEpithet("monophyllos");
		label.setAuthorship("(L.) Sw. Łuczaj");
		label.setOrdinal(7);
		label.setDateCreated(new Date());
		uls.persist(label);
		assertNotNull(label.getId());
		assertEquals("Orchidaceae", label.getFamily());

		UnitTrayLabel other = new UnitTrayLabel();
		other.setFamily("Rosaceae");
		other.setGenus("Rosa");
		other.setSpecificEpithet("carolina");
		uls.persist(other);

		assertEquals(2, uls.count());
		assertEquals(Integer.valueOf(7), uls.findMaxOrdinal());
		assertEquals("Malaxis monophyllos (L.) Sw. Łuczaj", uls.findById(label.getId().intValue()).toString());

		UnitTrayLabel example = new UnitTrayLabel();
		example.setFamily("Orchidaceae");
		List<UnitTrayLabel> found = uls.findByExample(example);
		assertEquals(1, found.size());
		assertEquals(label.getId(), found.get(0).getId());
		example.setGenus("Rosa");
		assertEquals(0, uls.findByExample(example).size());
		assertEquals(2, uls.findByExample(new UnitTrayLabel()).size());

		assertEquals("[, Orchidaceae, Rosaceae]", uls.findDistinctFamilies(true).toString());
		assertEquals("[Malaxis, Rosa]", uls.findDistinctGenera(false).toString());
		assertEquals("[Malaxis monophyllos (L.) Sw. Łuczaj, Rosa carolina]", uls.findAll().toString());

		label.setPrinted(3);
		assertEquals(3, uls.merge(label).getPrinted());
		assertEquals(3, uls.findById(label.getId().intValue()).getPrinted());

		uls.delete(label);
		assertEquals(1, uls.count());
		assertNull(uls.findById(label.getId().intValue()));

		assertTrue(uls.deleteAll());
		assertEquals(0, uls.count());
	}

	@Test
	public void testInventoryLifeCycle() throws Exception {
		InventoryLifeCycle ils = new InventoryLifeCycle();
		Inventory inventory = new Inventory();
		inventory.setCabinet("C2");
		inventory.setTaxon("Malaxis");
		inventory.setThickness(2.5f);
		inventory.setSheetsPerUnitThickness(10f);
		// attachDirty persists a new record
		ils.attachDirty(inventory);
		assertNotNull(inventory.getId());
		// and updates an existing one
		inventory.setThickness(3.0f);
		ils.attachDirty(inventory);
		Inventory first = new Inventory();
		first.setCabinet("C1");
		first.setTaxon("Rosa");
		ils.persist(first);

		assertEquals(2, ils.count());
		List<Inventory> all = ils.findAll();
		assertEquals("C1", all.get(0).getCabinet());
		assertEquals(3.0f, all.get(1).getThickness(), 0f);

		Inventory example = new Inventory();
		example.setCabinet("C2");
		example.setTaxon("Malaxis");
		example.setThickness(3.0f);
		example.setSheetsPerUnitThickness(10f);
		assertEquals(1, ils.findByExample(example).size());
		example.setThickness(2.5f);
		assertEquals(0, ils.findByExample(example).size());

		ils.delete(inventory);
		assertEquals(1, ils.count());
		assertTrue(ils.deleteAll());
		assertEquals(0, ils.count());
	}

	@Test
	public void testTaxonCsvRoundTrip() throws Exception {
		File in = folder.newFile("taxa.csv");
		Files.write(in.toPath(), ("Family,Subfamily,Tribe,Genus,SpecificEpithet,SubspecificEpithet,InfraspecificEpithet,InfraspecificRank,Authorship\n"
				+ "Orchidaceae,Epidendroideae,Malaxideae,Malaxis,monophyllos,,,,(L.) Sw.\n"
				+ "Rosaceae,Rosoideae,Roseae,Rosa,carolina,,,,\"Łuczaj, Müll.\"\n").getBytes(StandardCharsets.UTF_8));
		assertEquals(2, UnitTrayLabelLifeCycle.loadFromCSV(in.getPath()));
		File out = new File(folder.getRoot(), "taxa_out.csv");
		assertEquals(2, UnitTrayLabelLifeCycle.exportToCSV(out.getPath()));
		assertEquals(new String(Files.readAllBytes(in.toPath()), StandardCharsets.UTF_8),
				new String(Files.readAllBytes(out.toPath()), StandardCharsets.UTF_8));
		// loading replaces the existing records
		assertEquals(2, UnitTrayLabelLifeCycle.loadFromCSV(out.getPath()));
		List<UnitTrayLabel> loaded = new UnitTrayLabelLifeCycle().findAll();
		assertEquals("[Malaxis monophyllos (L.) Sw., Rosa carolina Łuczaj, Müll.]", loaded.toString());
		assertEquals("Epidendroideae", loaded.get(0).getSubfamily());
		assertEquals("Malaxideae", loaded.get(0).getTribe());

		// files without the optional Subfamily and Tribe columns still load
		File noHigherTaxa = folder.newFile("taxa_without_subfamily.csv");
		Files.write(noHigherTaxa.toPath(), ("Family,Genus,SpecificEpithet,Authorship\n"
				+ "Orchidaceae,Malaxis,monophyllos,(L.) Sw.\n").getBytes(StandardCharsets.UTF_8));
		assertEquals(1, UnitTrayLabelLifeCycle.loadFromCSV(noHigherTaxa.getPath()));
		loaded = new UnitTrayLabelLifeCycle().findAll();
		assertEquals("Malaxis monophyllos (L.) Sw.", loaded.get(0).toString());
		assertEquals("", loaded.get(0).getTribe());
	}

	@Test
	public void testInventoryCsvRoundTrip() throws Exception {
		File in = folder.newFile("inventory.csv");
		Files.write(in.toPath(), ("Cabinet,Taxon,Thickness,SheetsPerUnitThickness\n"
				+ "\"C, 2\",Rosa,1.0,4.0\n"
				+ "C1,Malaxis,2.5,10.0\n").getBytes(StandardCharsets.ISO_8859_1));
		assertEquals(2, InventoryLifeCycle.loadFromCSV(in.getPath(), true));
		File out = new File(folder.getRoot(), "inventory_out.csv");
		assertEquals(2, InventoryLifeCycle.exportToCSV(out.getPath()));
		assertEquals(new String(Files.readAllBytes(in.toPath()), StandardCharsets.ISO_8859_1),
				new String(Files.readAllBytes(out.toPath()), StandardCharsets.ISO_8859_1));
	}

}
