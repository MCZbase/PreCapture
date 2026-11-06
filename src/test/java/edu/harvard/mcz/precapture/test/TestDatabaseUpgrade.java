/**
 * TestDatabaseUpgrade.java
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.harvard.mcz.precapture.data.DatabaseUpgrade;

/**
 * Tests for detecting databases from older H2 versions.  The conversion itself 
 * needs the old H2 version from Maven Central, so it isn't tested here.
 */
public class TestDatabaseUpgrade {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testGetDatabasePath() { 
		assertEquals("./PRECAPTURE2", DatabaseUpgrade.getDatabasePath(" jdbc:h2:file:./PRECAPTURE2;INIT=create schema IF NOT EXISTS precapture"));
		assertEquals("PRECAPTURE2", DatabaseUpgrade.getDatabasePath("jdbc:h2:PRECAPTURE2;INIT=create schema IF NOT EXISTS precapture"));
		assertEquals("/data/db", DatabaseUpgrade.getDatabasePath("jdbc:h2:file:/data/db"));
		assertNull(DatabaseUpgrade.getDatabasePath("jdbc:h2:mem:test"));
		assertNull(DatabaseUpgrade.getDatabasePath("jdbc:h2:tcp://localhost/~/test"));
		assertNull(DatabaseUpgrade.getDatabasePath("jdbc:mysql://localhost:3306/precapture"));
		assertNull(DatabaseUpgrade.getDatabasePath(null));
	}

	@Test
	public void testNoDatabase() { 
		String url = "jdbc:h2:file:" + new File(folder.getRoot(), "PRECAPTURE2").getPath();
		assertNull(DatabaseUpgrade.checkForUpgrade(url, "sa", ""));
	}

	@Test
	public void testCurrentDatabase() throws Exception { 
		String url = "jdbc:h2:file:" + new File(folder.getRoot(), "PRECAPTURE2").getPath();
		try (Connection connection = DriverManager.getConnection(url, "sa", "")) { 
			connection.createStatement().execute("create table t (x int)");
		}
		assertNull(DatabaseUpgrade.checkForUpgrade(url, "sa", ""));
	}

	@Test
	public void testPageStoreDatabaseAndSetAside() throws IOException { 
		File pageStore = new File(folder.getRoot(), "PRECAPTURE2.h2.db");
		Files.write(pageStore.toPath(), "H2 1.3 database".getBytes());
		String url = "jdbc:h2:file:" + new File(folder.getRoot(), "PRECAPTURE2").getPath() + ";INIT=create schema IF NOT EXISTS precapture";
		DatabaseUpgrade upgrade = DatabaseUpgrade.checkForUpgrade(url, "sa", "");
		assertNotNull(upgrade);
		assertEquals("1.3.167", upgrade.getFromVersion());
		assertEquals(pageStore.getAbsolutePath(), upgrade.getDatabaseFile());

		// set aside, without converting
		String backup = upgrade.setAside();
		assertEquals(pageStore.getAbsolutePath() + ".bak", backup);
		assertFalse(pageStore.exists());
		assertEquals("H2 1.3 database", new String(Files.readAllBytes(Paths.get(backup))));
		assertNull(DatabaseUpgrade.checkForUpgrade(url, "sa", ""));

		// setting aside a second time doesn't overwrite the first backup
		Files.write(pageStore.toPath(), "second".getBytes());
		String secondBackup = DatabaseUpgrade.checkForUpgrade(url, "sa", "").setAside();
		assertNotEquals(backup, secondBackup);
		assertTrue(secondBackup.endsWith(".bak"));
		assertEquals("H2 1.3 database", new String(Files.readAllBytes(Paths.get(backup))));
		assertEquals("second", new String(Files.readAllBytes(Paths.get(secondBackup))));
	}

	@Test
	public void testIsDownloadFailure() { 
		assertTrue(DatabaseUpgrade.isDownloadFailure(new RuntimeException("Error downloading", new ConnectException("Connection refused"))));
		assertFalse(DatabaseUpgrade.isDownloadFailure(new RuntimeException("SHA-256 checksum mismatch")));
	}

}
