package edu.harvard.mcz.precapture.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.nio.file.Paths;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

// import edu.harvard.mcz.precapture.LoginDialog;
// import edu.harvard.mcz.precapture.MainFrame;
// import edu.harvard.mcz.precapture.Singleton;

/**
 * Singleton class to obtain access to Hibernate sessions, used in the *LifeCycle classes.
 *  
 * Modified from the hibernate tutorial
 * http://www.hibernate.org/hib_docs/v3/reference/en-US/html/tutorial-firstapp.html
 * Changed singleton implementation to allow loading of credentials from config and dialog at runtime
 */
public class HibernateUtil {

	private static SessionFactory sessionFactory = null;
	
	/** Location of the H2 database file, overriding the one in hibernate.cfg.xml, if not null. */
	private static String databasePath = null;
	
	private static final Log log = LogFactory.getLog(HibernateUtil.class);
	
	public static void terminateSessionFactory() {
		try { 
			sessionFactory.getCurrentSession().cancelQuery();
			sessionFactory.getCurrentSession().clear();
			sessionFactory.getCurrentSession().close();
		} catch (Exception e) { 
		} finally { 
			try { 
				sessionFactory.close();
			} catch  (Exception e1) { 
			} finally { 
				sessionFactory = null;
			} 
		}
	}
	

	/**
	 * Set the location of the H2 database file, replacing the file path in the 
	 * hibernate.connection.url in hibernate.cfg.xml (other settings in that url 
	 * are retained).  Closes the current session factory, if any, so that the 
	 * next call to getSessionFactory() uses the new location.
	 * 
	 * @param path the path of the database file, without the .mv.db suffix, 
	 * relative to the working directory unless absolute, e.g. ./PRECAPTURE2 
	 * or /data/precapture/PRECAPTURE2.  Null or blank to use hibernate.cfg.xml.
	 */
	public static void setDatabasePath(String path) { 
		if (sessionFactory != null) { 
			terminateSessionFactory();
		}
		databasePath = normalizeDatabasePath(path);
		log.debug("Database path set to " + databasePath);
	}

	/**
	 * Put a database path in the form H2 requires: without a .mv.db suffix, and with 
	 * a path relative to the working directory starting with ./ (H2 does not accept 
	 * implicitly relative paths such as data/PRECAPTURE2).
	 * 
	 * @param path a database path, e.g. data/PRECAPTURE2.mv.db
	 * @return the normalized path, e.g. ./data/PRECAPTURE2, or null if path is null or blank.
	 */
	public static String normalizeDatabasePath(String path) { 
		if (path == null) { 
			return null;
		}
		path = path.trim();
		if (path.endsWith(".mv.db")) { 
			path = path.substring(0, path.length() - ".mv.db".length());
		}
		if (path.isEmpty()) { 
			return null;
		}
		if (!Paths.get(path).isAbsolute() && !path.startsWith(".") && !path.startsWith("~")) { 
			path = "./" + path;
		}
		return path;
	}

	/**
	 * The hibernate configuration from hibernate.cfg.xml, with the database 
	 * path set by setDatabasePath() applied.
	 * 
	 * @return the configuration.
	 */
	private static Configuration configuration() { 
		Configuration config = new Configuration().configure();
		if (databasePath != null) { 
			String configuredUrl = config.getProperties().getProperty("hibernate.connection.url");
			String url = replaceDatabasePath(configuredUrl, databasePath);
			if (url == null) { 
				log.error("Not an H2 file database, unable to set its path: " + configuredUrl);
			} else { 
				config.setProperty("hibernate.connection.url", url);
			}
		}
		return config;
	}

	/**
	 * Replace the database file path in an H2 jdbc url.
	 * 
	 * @param url an H2 jdbc url for a file database, e.g. jdbc:h2:file:./PRECAPTURE2;INIT=...
	 * @param path the new database path, e.g. /data/PRECAPTURE2
	 * @return the url with the path replaced, e.g. jdbc:h2:file:/data/PRECAPTURE2;INIT=..., 
	 * or null if url is not for an H2 file database.
	 */
	public static String replaceDatabasePath(String url, String path) { 
		if (DatabaseUpgrade.getDatabasePath(url) == null) { 
			return null;
		}
		String trimmed = url.trim();
		int semicolon = trimmed.indexOf(';');
		String settings = semicolon >= 0 ? trimmed.substring(semicolon) : "";
		return "jdbc:h2:file:" + path + settings;
	}

	/**
	 *  Using the Hibernate configuration in Configuration from hibernate.cfg.xml
	 *  create a Hibernate sessionFactory.  Method is private as the the session factory 
	 *  should be a singleton, invoke getSessionFactory() to create or access a session.
	 *  
	 *  @see edu.harvard.mcz.precapture.data.HibernateUtil#getSessionFactory
	 */
	private static void createSessionFactory() {
			Configuration config = configuration();
			sessionFactory = config.buildSessionFactory();
			Session session = sessionFactory.getCurrentSession();
			session.beginTransaction();
			session.close();									
/*			
		try {
			// Create the Configuration from hibernate.cfg.xml
			// Add authentication properties obtained from the user
			boolean success = false;
			LoginDialog loginDialog = new LoginDialog();
			while (!success && loginDialog.getResult()!=LoginDialog.RESULT_CANCEL) {
				// retrieve the connection parameters from hibernate.cfg.xml and load into the LoginDialog
				loginDialog.setConnection(config.getProperty("hibernate.connection.url"));
				loginDialog.setDialect(config.getProperty("hibernate.dialect"));
				loginDialog.setDriver(config.getProperty("hibernate.connection.driver_class"));
				// If the database username(schema) and password are present load them as well.
				String un = config.getProperty("hibernate.connection.username");
				if (un!=null) { 
					loginDialog.setSchemaName(config.getProperty("hibernate.connection.username"));
				}
				String pw = config.getProperty("hibernate.connection.password"); 
				if (pw!=null) { 
					loginDialog.setDBPassword(config.getProperty("hibernate.connection.password"));
				}					
	            // Display the LoginDialog as a modal dialog 
				loginDialog.setModalityType(ModalityType.APPLICATION_MODAL);
				loginDialog.setVisible(true);
				// Check authentication (starting with the database user(schema)/password.
				String username = "NotAuthenticated";
				if (loginDialog.getResult()==LoginDialog.RESULT_LOGIN) { 
					if (Singleton.getSingletonInstance().getMainFrame() != null) { 
			           Singleton.getSingletonInstance().getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					   Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Connecting to database");
					} 
					config.setProperty("hibernate.connection.password", loginDialog.getDBPassword());
					username = loginDialog.getSchemaName();
					config.setProperty("hibernate.connection.username", username);
					config.setProperty("hibernate.connection.url", loginDialog.getConnection());
					// Now create the SessionFactory from this configuration
					try { 
						sessionFactory = config.buildSessionFactory();
					} catch (Throwable ex) {
						// Make sure you log the exception, as it might be swallowed
						System.out.println("Initial SessionFactory creation failed." + ex);
						throw new ExceptionInInitializerError(ex);
					}
					try { 
						// Check database authentication by beginning a transaction.
						Session session = sessionFactory.getCurrentSession();
						session.beginTransaction();
						session.close();
						// If an exception hasn't been thrown, dbuser/dbpassword has 
						// successfully authenticated against the database.
						// Now try authenticating the individual user by the email addresss/password that they provided.
						UsersLifeCycle uls = new UsersLifeCycle();
						Users userToLogin = new Users();
						userToLogin.setUsername(loginDialog.getUsername());
						userToLogin.setHash(loginDialog.getUserPasswordHash());
						List<Users> foundUser = uls.findByExample(userToLogin);
						if (foundUser.size()==1) {
 						    // There should be one and only one user returned.
							log.debug(foundUser.get(0).getHash());
							log.debug(loginDialog.getUserPasswordHash());
						    if (foundUser.get(0).getUsername().equals(loginDialog.getUsername()) && foundUser.get(0).getHash().equals(loginDialog.getUserPasswordHash())) {
							   // and that user must have exactly the username/password hash provided in the dialog. 
						       Singleton.getSingletonInstance().setCurrentUsername(userToLogin.getUsername());
						       success = true;
						       try { 
						           Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Connected as "+ foundUser.get(0).getFullname());
						           Singleton.getSingletonInstance().getMainFrame().setState(MainFrame.STATE_RUNNING);
						       } catch (NullPointerException ex) { 
								   // expected if we haven't instantiated a main frame.
						       }
						   } 
						} 
						if (!success) { 
							loginDialog = new LoginDialog();
							loginDialog.setStatus("Login failed: Incorrect Email and/or Password.");
							success = false;
							if (loginDialog.getUsername()!=null) { 
						        log.debug("Login failed for " + loginDialog.getUsername());
							}
							sessionFactory.close();
							sessionFactory = null;
							try { 
							    Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Login failed.");
							} catch (NullPointerException ex) { 
								// expected if we haven't instantiated a main frame.
							}
						}
					} catch (Throwable e) {
						e.printStackTrace();
						log.error(e);
						log.trace(e);
						System.out.println(e.getStackTrace());
						System.out.println("Initial SessionFactory creation failed." + e.getMessage());
						// TODO: don't reference UI if not running one...
						loginDialog = new LoginDialog();
						loginDialog.setStatus("Login failed: " + e.getCause());
						success = false;
						sessionFactory.close();
						sessionFactory = null;
						if (Singleton.getSingletonInstance().getMainFrame() != null) {
						    Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Login failed.");
						} 
					}
					if (Singleton.getSingletonInstance().getMainFrame() != null) { 
					   Singleton.getSingletonInstance().getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				}
			}
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			ex.printStackTrace();
			System.out.println("Initial SessionFactory creation failed." + ex);
			System.out.println("Cause" + ex.getCause().getMessage());
			throw new ExceptionInInitializerError(ex);
		}
*/		
	}
	
	
    /**
     * Call this method to obtain a Hibernate Session.
     * <BR>  
     * Usage:
     * <pre>
         Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	     session.beginTransaction();
	   </pre>
     * 
     * @return the Hibernate SessionFactory.
     */
	public static SessionFactory getSessionFactory() {
		if (sessionFactory==null) { 
			createSessionFactory();
		}
		return sessionFactory;
	}

	/**
	 * Provide a display of the configured driver_class.  Intended to show, in 
	 * the UI, the user the value of driver_class.
	 * 
	 * @return a string representing the connection.driver_class in the 
	 * hibernate configuration file.
	 */
	public static String getDriverClassString() { 
		Configuration config = configuration();
		return config.getProperties().getProperty("hibernate.connection.driver_class");
	}
	
	/**
	 * Provide a display of the configured database connection url.  Intended to
	 * show the user the value of connection.url in the UI.
	 * 
	 * @return a string representing the connection.url in the hibernate
	 * configuration file.
	 */
    public static String getConnectionURL() { 
		Configuration config = configuration();
		return config.getProperties().getProperty("hibernate.connection.url");
	}

	/**
	 * Obtain the value of a property from the hibernate configuration file.
	 * 
	 * @param name the name of the property, e.g. hibernate.connection.username
	 * @return the value of the property, or null if it is not set.
	 */
	public static String getConfigurationProperty(String name) { 
		Configuration config = configuration();
		return config.getProperties().getProperty(name);
	}
	
}
	

