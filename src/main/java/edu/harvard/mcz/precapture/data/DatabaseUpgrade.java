/**
 * SPDX-License-Identifier: GPL-2.0-only
 * DatabaseUpgrade.java
 * edu.harvard.mcz.precapture.data
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
 * 
 * Author: Claude Opus 5.5 (Anthropic)
 */
package edu.harvard.mcz.precapture.data;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h2.api.ErrorCode;
import org.h2.engine.Constants;
import org.h2.tools.Upgrade;

/**
 * Detects an H2 database file written by an older, incompatible, version of
 * H2 and converts it to the current H2 file format.
 *
 * Released versions of PreCapture up to 1.1.2 used H2 1.3.167, which stores
 * the database in PRECAPTURE2.h2.db; current versions of H2 do not read that
 * file, and would silently create a new, empty, PRECAPTURE2.mv.db beside it.
 * Development builds from 2022-2023 used H2 2.0.x or 2.1.x, whose
 * PRECAPTURE2.mv.db files H2 2.2 and later refuse to open.
 *
 * Conversion uses org.h2.tools.Upgrade, which downloads the old H2 version
 * from Maven Central (verifying its SHA-256 checksum), uses it to export the
 * database to an sql script, and loads that script into a new database with the
 * current version of H2.  The original database file is kept with a .bak suffix.
 */
public class DatabaseUpgrade {

	private static final Log log = LogFactory.getLog(DatabaseUpgrade.class);

	/** H2 1.3.167, used by PreCapture releases up to 1.1.2. */
	public static final int H2_1_3_167 = 167;
	/** H2 2.1.214, the last H2 version able to read 2.0.x and 2.1.x database files. */
	public static final int H2_2_1_214 = 214;

	private static final String H2_URL_PREFIX = "jdbc:h2:";
	private static final String MV_SUFFIX = Constants.SUFFIX_MV_FILE;
	private static final String PAGE_STORE_SUFFIX = ".h2.db";
	/** The repository org.h2.tools.Upgrade downloads old H2 versions from. */
	private static final String MAVEN_CENTRAL = "https://repo.maven.apache.org/maven2/";
	private static final int NETWORK_TIMEOUT_MILLISECONDS = 10000;

	private final String databasePath;
	private final String username;
	private final String password;
	private final int fromVersion;

	private DatabaseUpgrade(String databasePath, String username, String password, int fromVersion) {
		this.databasePath = databasePath;
		this.username = username;
		this.password = password;
		this.fromVersion = fromVersion;
	}

	/**
	 * Check whether the database configured in hibernate.cfg.xml needs to be
	 * converted from an older H2 file format.
	 *
	 * @return a DatabaseUpgrade to perform the conversion, or null if no
	 * conversion is needed (including when the configured database is not a
	 * local H2 file, or does not exist yet).
	 */
	public static DatabaseUpgrade checkForUpgrade() {
		String url = HibernateUtil.getConnectionURL();
		String username = HibernateUtil.getConfigurationProperty("hibernate.connection.username");
		String password = HibernateUtil.getConfigurationProperty("hibernate.connection.password");
		return checkForUpgrade(url, username == null ? "" : username, password == null ? "" : password);
	}

	/**
	 * Check whether an H2 database needs to be converted from an older file format.
	 *
	 * @param url the jdbc url for the database.
	 * @param username the database user.
	 * @param password the password for the database user.
	 * @return a DatabaseUpgrade to perform the conversion, or null if none is needed.
	 */
	public static DatabaseUpgrade checkForUpgrade(String url, String username, String password) {
		String databasePath = getDatabasePath(url);
		if (databasePath == null) {
			log.debug("Not a local H2 database file, no upgrade check: " + url);
			return null;
		}
		boolean hasMvFile = Files.exists(Paths.get(databasePath + MV_SUFFIX));
		boolean hasPageStoreFile = Files.exists(Paths.get(databasePath + PAGE_STORE_SUFFIX));
		if (!hasMvFile) {
			if (hasPageStoreFile) {
				log.info("Found H2 1.3 database " + databasePath + PAGE_STORE_SUFFIX + " which needs to be upgraded.");
				return new DatabaseUpgrade(databasePath, username, password, H2_1_3_167);
			}
			return null;
		}
		// Try opening the existing database with the current H2 version.
		Properties info = new Properties();
		info.setProperty("user", username);
		info.setProperty("password", password);
		try (Connection connection = new org.h2.Driver().connect(H2_URL_PREFIX + "file:" + databasePath + ";IFEXISTS=TRUE", info)) {
			return null;
		} catch (SQLException e) {
			if (e.getErrorCode() == ErrorCode.FILE_VERSION_ERROR_1) {
				log.info("Found H2 database " + databasePath + MV_SUFFIX + " in an older file format which needs to be upgraded.");
				return new DatabaseUpgrade(databasePath, username, password, H2_2_1_214);
			}
			// Some other problem (such as the database being open in another
			// process), leave that to be reported by the normal startup.
			log.error("Unable to check database version: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Obtain the file path (without a suffix) of a local file H2 database from its
	 * jdbc url.
	 *
	 * @param url a jdbc url, e.g. jdbc:h2:file:./PRECAPTURE2;INIT=...
	 * @return the database path, e.g. ./PRECAPTURE2, or null if the url is not for
	 * an H2 database stored in a local file.
	 */
	public static String getDatabasePath(String url) {
		if (url == null) {
			return null;
		}
		String path = url.trim();
		if (!path.startsWith(H2_URL_PREFIX)) {
			return null;
		}
		path = path.substring(H2_URL_PREFIX.length());
		int semicolon = path.indexOf(';');
		if (semicolon >= 0) {
			path = path.substring(0, semicolon);
		}
		if (path.startsWith("file:")) {
			path = path.substring("file:".length());
		} else if (path.startsWith("mem:") || path.startsWith("tcp:") || path.startsWith("ssl:") || path.startsWith("zip:")) {
			return null;
		}
		if (path.isEmpty()) {
			return null;
		}
		return path;
	}

	/**
	 * @return the database file that will be converted.
	 */
	public String getDatabaseFile() {
		return Paths.get(databasePath + (fromVersion == H2_1_3_167 ? PAGE_STORE_SUFFIX : MV_SUFFIX)).toAbsolutePath().normalize().toString();
	}

	/**
	 * @return the version of H2 that will be used to read the existing database, e.g. 1.3.167.
	 */
	public String getFromVersion() {
		return fromVersion == H2_1_3_167 ? "1.3.167" : "2.1.214";
	}

	/**
	 * @return the file name the original database will be kept under.
	 */
	public String getBackupFile() {
		return getDatabaseFile() + ".bak";
	}

	/**
	 * Convert the database to the current H2 file format.  Downloads the old
	 * version of H2 from Maven Central.  On success the original database file
	 * is kept with a .bak suffix, on failure the original file is left in place.
	 *
	 * @throws Exception if the old version of H2 can't be obtained or the
	 * conversion fails.
	 */
	public void upgrade() throws Exception {
		Properties info = new Properties();
		info.setProperty("user", username);
		info.setProperty("password", password);
		// Use a plain url, the configured url's INIT clause would create objects
		// that the conversion script also creates.
		String url = H2_URL_PREFIX + "file:" + databasePath;
		log.info("Upgrading " + getDatabaseFile() + " from H2 " + getFromVersion() + " to H2 " + Constants.FULL_VERSION);
		if (!Upgrade.upgrade(url, info, fromVersion)) {
			throw new IOException("H2 did not upgrade " + url);
		}
		if (fromVersion == H2_1_3_167) {
			// Upgrade keeps the original .mv.db file as .mv.db.bak, but leaves a 1.3
			// .h2.db file in place, rename it for consistency.
			Path pageStore = Paths.get(databasePath + PAGE_STORE_SUFFIX);
			try { 
				Files.move(pageStore, Paths.get(databasePath + PAGE_STORE_SUFFIX + ".bak"), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) { 
				// The conversion succeeded, and H2 2.x ignores .h2.db files, so this is not fatal.
				log.warn("Unable to rename " + pageStore + " to " + pageStore + ".bak: " + e.getMessage());
			}
		}
		log.info("Database upgrade complete, original kept as " + getBackupFile());
	}

	/**
	 * Check whether the old version of H2 needed for the conversion can be 
	 * obtained, either from the local Maven repository or from Maven Central.
	 * 
	 * @return true if the old H2 jar is in the local Maven repository or 
	 * Maven Central responds to a request for it, false if it can't be reached 
	 * (e.g. no network connection).
	 */
	public boolean isOldH2Available() { 
		String version = getFromVersion();
		String repoFile = "com/h2database/h2/" + version + "/h2-" + version + ".jar";
		Path local = Paths.get(System.getProperty("user.home"), ".m2", "repository").resolve(repoFile);
		if (Files.isRegularFile(local)) { 
			log.debug("Old H2 available locally: " + local);
			return true;
		}
		HttpURLConnection connection = null;
		try { 
			connection = (HttpURLConnection) URI.create(MAVEN_CENTRAL + repoFile).toURL().openConnection();
			connection.setRequestMethod("HEAD");
			connection.setConnectTimeout(NETWORK_TIMEOUT_MILLISECONDS);
			connection.setReadTimeout(NETWORK_TIMEOUT_MILLISECONDS);
			int status = connection.getResponseCode();
			log.debug("Maven Central responded " + status + " for " + repoFile);
			return status == HttpURLConnection.HTTP_OK;
		} catch (IOException e) { 
			log.info("Unable to reach Maven Central to obtain H2 " + version + ": " + e);
			return false;
		} finally { 
			if (connection != null) { 
				connection.disconnect();
			}
		}
	}

	/**
	 * Test whether an exception from upgrade() was caused by a failure to 
	 * download the old version of H2 (as opposed to a failure in the conversion).
	 * 
	 * @param e the exception thrown by upgrade()
	 * @return true if an IOException, such as a network failure, is in the cause chain.
	 */
	public static boolean isDownloadFailure(Throwable e) { 
		for (Throwable t = e; t != null; t = t.getCause()) { 
			if (t instanceof IOException) { 
				return true;
			}
			if (t.getCause() == t) { 
				break;
			}
		}
		return false;
	}

	/**
	 * Move the old database file aside, without converting it, so that a new 
	 * empty database can be created in its place.  The file is renamed with a 
	 * .bak suffix, or, if that file already exists, a timestamped .bak suffix, 
	 * so no earlier backup is overwritten.
	 * 
	 * @return the file the old database was moved to.
	 * @throws IOException if the file can't be renamed.
	 */
	public String setAside() throws IOException { 
		Path original = Paths.get(getDatabaseFile());
		Path backup = Paths.get(getBackupFile());
		if (Files.exists(backup)) { 
			String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
			backup = Paths.get(getDatabaseFile() + "." + timestamp + ".bak");
		}
		Files.move(original, backup);
		log.info("Moved database " + original + " to " + backup + " without converting it.");
		return backup.toString();
	}

}
