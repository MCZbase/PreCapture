/**
 * SPDX-License-Identifier: GPL-2.0-only
 * UnitTrayLabelLifeCycle.java
 * edu.harvard.mcz.precapture.data
 * Copyright © 2009-2026 President and Fellows of Harvard College
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
 * Author: Paul J. Morris
 * Author: Claude Opus 5.5 (Anthropic)
 */
package edu.harvard.mcz.precapture.data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jakarta.persistence.PersistenceException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.LockMode;
import org.hibernate.SessionException;
import org.hibernate.Session;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import edu.harvard.mcz.precapture.exceptions.SaveFailedException;

/** UnitTrayLabelLifeCycle  Utility class to manage the lifecycle of UnitTrayLabel PDOs,
 * and support various other functions related to UnitTrayLabel objects.  The UnitTrayLabel
 * data set comprises a taxon authority file.  
 * 
 * @author Paul J. Morris
 *
 */
public class UnitTrayLabelLifeCycle  {
	
private static final Log log = LogFactory.getLog(UnitTrayLabelLifeCycle.class);

    /**
     * Given a unit tray label object, return a string representation of the taxon name.
     * 
     * Placed here as UnitTrayLabel class is generated from xml and could be 
     * overwritten if regenerated.
     * 
     * @see UnitTrayLabel
     * @param unitTrayLabel the unit tray label from which to extract a taxon name
     * @return a string representation of the taxon name, or an empty string if unitTrayLabel is null.
     */
    public static String getScientificName(UnitTrayLabel unitTrayLabel) {
    	StringBuffer result = new StringBuffer();
    	if (unitTrayLabel!=null) { 
    		result.append(unitTrayLabel.getGenus()).append(" ");
    		result.append(unitTrayLabel.getSpecificEpithet()).append(" ");
    		if (unitTrayLabel.getSubspecificEpithet()!=null && unitTrayLabel.getSubspecificEpithet().length()>0) {
    			result.append(unitTrayLabel.getSubspecificEpithet()).append(" ");
    		}
    		if (unitTrayLabel.getInfraspecificRank()!=null && unitTrayLabel.getInfraspecificRank().length()>0) {    	
    			result.append(unitTrayLabel.getInfraspecificRank()).append(" ");
    		}    	
    		if (unitTrayLabel.getInfraspecificEpithet()!=null && unitTrayLabel.getInfraspecificEpithet().length()>0) {    	
    			result.append(unitTrayLabel.getInfraspecificEpithet()).append(" ");
    		}
    		result.append(unitTrayLabel.getAuthorship());
    	}
    	return result.toString().trim();
	}
	
    public static int loadFromCSV(String filename) throws IOException {
    	return loadFromCSV(filename,null);
    }	    	
    	
    /**
    * @param canonicalPath
    * @param taxonProgressBar
    * 
    * @returns count of the number of specific epithets found in load.
    */
    public static int loadFromCSV(String filename, JProgressBar taxonProgressBar) throws IOException {
    	int numberLoaded = 0;
    	int totalLines = 0;
    	final JProgressBar jpb = taxonProgressBar;
    	if (taxonProgressBar!=null) { 
    		totalLines = CsvUtility.countRecords(filename, StandardCharsets.UTF_8);
    		final int totalLinesF = totalLines;
    		SwingUtilities.invokeLater(new Runnable() {
    			public void run() { 
    		         jpb.setMaximum(totalLinesF);
    			}
    		});
    	}
    	CSVParser reader = CsvUtility.openForReading(filename, StandardCharsets.UTF_8);
    	log.debug("Reading: " + filename);
    	boolean hasTribe = false;
    	boolean hasSubfamily = false;
    	List<String> headers = reader.getHeaderNames();
    	log.debug("ExpectedHeaders: Family, Genus, SpecificEpithet, SubspecificEpithet, InfraspecificEpithet, InfraspecificRank, Authorship");
    	log.debug("OptionalHeaders: Subfamily, Tribe");
    	for (String header : headers) { 
    		log.debug("Contains Header: "+ header);
    		if (header.equals("Tribe")) { hasTribe = true; } 
    		if (header.equals("Subfamily")) { hasSubfamily = true; } 
    	}
    	UnitTrayLabelLifeCycle uls = new UnitTrayLabelLifeCycle();
    	uls.deleteAll();
    	for (CSVRecord record : reader) { 
    		UnitTrayLabel unitTrayLabel = new UnitTrayLabel();
    		// TODO: If scientific name is added to data structure 
    		// it will need to be added here as well.
    		unitTrayLabel.setFamily(CsvUtility.get(record, "Family"));
    	    if (hasSubfamily) {
    		    unitTrayLabel.setSubfamily(CsvUtility.get(record, "Subfamily"));
    		}
    	    if (hasTribe) {
    		    unitTrayLabel.setTribe(CsvUtility.get(record, "Tribe"));
    		}
    		unitTrayLabel.setGenus(CsvUtility.get(record, "Genus"));
    		unitTrayLabel.setSpecificEpithet(CsvUtility.get(record, "SpecificEpithet"));
    		unitTrayLabel.setSubspecificEpithet(CsvUtility.get(record, "SubspecificEpithet"));
    		unitTrayLabel.setInfraspecificEpithet(CsvUtility.get(record, "InfraspecificEpithet"));
    		unitTrayLabel.setInfraspecificRank(CsvUtility.get(record, "InfraspecificRank"));
    		unitTrayLabel.setAuthorship(CsvUtility.get(record, "Authorship"));
    		try {
				uls.persist(unitTrayLabel);
				if (unitTrayLabel.getSpecificEpithet()!=null) { 
				    numberLoaded++;
				}
			} catch (SaveFailedException e) {
				log.error(e.getMessage());
			}
    	    if (taxonProgressBar!=null) {
    	    	final int numberLoadedF = numberLoaded;
    	    	SwingUtilities.invokeLater(new Runnable() { 
    	    		public void run() {
    	    	        jpb.setValue(numberLoadedF);
    	    	   } 
    	    	});
    	    }
    	}
    	reader.close();
    	if (taxonProgressBar!=null) {
    	    taxonProgressBar.setValue(totalLines);
    	}
    	return numberLoaded;
    }

    public static int exportToCSV(String filename) throws IOException {
    	int numberWritten = 0;
    	// Write UTF-8, the encoding loadFromCSV() reads (previously written as ISO-8859-1, 
    	// so characters outside ISO-8859-1 were lost and non-ASCII characters were 
    	// garbled when an export was reloaded).
    	CSVPrinter writer = CsvUtility.openForWriting(filename, StandardCharsets.UTF_8);
    	String[] header = {"Family","Subfamily","Tribe","Genus","SpecificEpithet","SubspecificEpithet","InfraspecificEpithet","InfraspecificRank","Authorship"};
    	writer.printRecord((Object[])header);
    	UnitTrayLabelLifeCycle uls = new UnitTrayLabelLifeCycle();
    	List<UnitTrayLabel> list = uls.findAll();
    	Iterator<UnitTrayLabel> i = list.iterator();
    	while (i.hasNext()) { 
    		//  TODO: If scientificName is added, it will need to be added here as well.
    		UnitTrayLabel unitTrayLabel = i.next();
    		ArrayList<String> row = new ArrayList<String>();
    		row.add(unitTrayLabel.getFamily());
    		row.add(unitTrayLabel.getSubfamily());
    		row.add(unitTrayLabel.getTribe());
    		row.add(unitTrayLabel.getGenus());
    		row.add(unitTrayLabel.getSpecificEpithet());
    		row.add(unitTrayLabel.getSubspecificEpithet());
    		row.add(unitTrayLabel.getInfraspecificEpithet());
    		row.add(unitTrayLabel.getInfraspecificRank());
    		row.add(unitTrayLabel.getAuthorship());
    		writer.printRecord(row);
    		numberWritten++;
    	}
    	writer.close();
    	return numberWritten;
    }    
    
	public void persist(UnitTrayLabel transientInstance) throws SaveFailedException {
		log.debug("persisting UnitTrayLabel instance");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				session.persist(transientInstance);
				session.getTransaction().commit();
				log.debug("persist successful");
			} catch (PersistenceException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
				throw new SaveFailedException("Save to UnitTrayLabel table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(UnitTrayLabel instance) throws SaveFailedException {
		log.debug("attaching dirty UnitTrayLabel instance");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try  {
				if (instance.getId()==null) {
					// new record, assigns an id to instance
					session.persist(instance);
				} else {
					session.merge(instance);
				}
				session.getTransaction().commit();
				log.debug("attach successful");
			} catch (PersistenceException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
				throw new SaveFailedException("Save to UnitTrayLabel table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
	
	public void attachClean(UnitTrayLabel instance) {
		log.debug("attaching clean UnitTrayLabel instance");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				session.lock(instance, LockMode.NONE);
				session.getTransaction().commit();
				log.debug("attach successful");
			} catch (PersistenceException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(UnitTrayLabel persistentInstance) throws SaveFailedException {
		log.debug("deleting UnitTrayLabel instance");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				UnitTrayLabel managed = session.find(UnitTrayLabel.class, persistentInstance.getId());
				if (managed!=null) {
					session.remove(managed);
				}
				session.getTransaction().commit();
				log.debug("delete successful");
			} catch (PersistenceException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
				throw new SaveFailedException("Delete from UnitTrayLabel table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public UnitTrayLabel merge(UnitTrayLabel detachedInstance) throws SaveFailedException {
		log.debug("merging UnitTrayLabel instance");
		try {
			UnitTrayLabel result = detachedInstance;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				result = session.merge(detachedInstance);
				session.getTransaction().commit();
				log.debug("merge successful");
			} catch (PersistenceException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
				throw new SaveFailedException("Save to UnitTrayLabel table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public UnitTrayLabel findById(java.lang.Integer id) {
		log.debug("getting UnitTrayLabel instance with id: " + id);
		try {
			UnitTrayLabel instance = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				instance = session.find(UnitTrayLabel.class, Long.valueOf(id));
				session.getTransaction().commit();
				if (instance == null) {
					log.debug("get successful, no instance found");
				} else {
					log.debug("get successful, instance found");
				}
			} catch (PersistenceException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	@SuppressWarnings("unchecked")
	public List<UnitTrayLabel> findByExample(UnitTrayLabel instance) {
		log.debug("finding UnitTrayLabel instance by example");
		try {
			List<UnitTrayLabel> results = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				CriteriaBuilder builder = session.getCriteriaBuilder();
				CriteriaQuery<UnitTrayLabel> query = builder.createQuery(UnitTrayLabel.class);
				Root<UnitTrayLabel> root = query.from(UnitTrayLabel.class);
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (instance.getFamily()!=null && instance.getFamily().length()>0) { 
					predicates.add(builder.equal(root.get("family"), instance.getFamily()));
				}
				if (instance.getGenus()!=null && instance.getGenus().length()>0) { 
					predicates.add(builder.equal(root.get("genus"), instance.getGenus()));
				}
				query.select(root).where(predicates.toArray(new Predicate[predicates.size()]));
				results = session.createSelectionQuery(query).getResultList();
				session.getTransaction().commit();
				log.debug("find by example successful, result size: "
						+ results.size());
			} catch (PersistenceException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());

			}
			try { session.close(); } catch (SessionException e) { }
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<UnitTrayLabel> findAll() {
		log.debug("finding all UnitTrayLabel start " + new Date());
		try {
			List<UnitTrayLabel> results = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				results = session.createSelectionQuery("from UnitTrayLabel u order by u.genus, u.specificEpithet ", UnitTrayLabel.class).getResultList();
				session.getTransaction().commit();
				log.debug("find all successful, result size: " + results.size());
			} catch (PersistenceException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
			} finally { 
			    try { session.close(); } catch (SessionException e) { }
			}
			log.debug("finding all UnitTrayLabel end " + new Date());
			return results;
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public boolean deleteAll() {
		boolean result = false;
		log.debug("deleting all UnitTrayLabel records");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				int rows = session.createMutationQuery("delete from UnitTrayLabel u ").executeUpdate();
				session.getTransaction().commit();
				log.debug("delete all successful.  Rows=" + rows);
				result = true;
			} catch (PersistenceException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
			} finally { 
			    try { session.close(); } catch (SessionException e) { }
			}
			return result;
		} catch (RuntimeException re) {
			log.error("delete all failed", re);
			throw re;
		}
	}	

	public Integer findMaxOrdinal() { 
		log.debug("finding max ordinal in UnitTrayLabel");
		Integer result = 0;
		
		try {

			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try {
				Integer max = session.createSelectionQuery("select max(u.ordinal) from UnitTrayLabel u", Integer.class).getSingleResult();
				session.getTransaction().commit();
				// max() is null when the table is empty, in which case return 0.
				if (max!=null) { 
					result = max;
				}
				log.debug(result);
			} catch (PersistenceException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
			} finally { 
			    try { session.close(); } catch (SessionException e) { }
			}

		} catch (RuntimeException re) {
			log.error("find max ordinal failed", re);
			throw re;
		}
		
		
		return result;
	}
	
	/**
	 * select count(*) from UnitTrayLabel;
	 * 
	 * @return the number of rows in the UnitTrayLabel table.
	 */
	public int count() {
		int result = 0;
		log.debug("counting UnitTrayLabel records");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				result = session.createSelectionQuery("select count(*) from UnitTrayLabel ", Long.class).getSingleResult().intValue();
				session.getTransaction().commit();
				log.debug("find all successful, count= " + result);
			} catch (PersistenceException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
			} finally { 
			    try { session.close(); } catch (SessionException e) { }
			}
			return result;
		} catch (RuntimeException re) {
			log.error("count failed", re);
			throw re;
		}
	}	
	
	/**
	 * Like count(), but passes on hibernate exceptions.  Used to test for 
	 * presence of tables at startup.
	 * 
	 * @return the number of rows in the UnitTrayLabel table.
	 */
	public int countAtStartup() throws Exception {
		int result = 0;
		log.debug("counting UnitTrayLabel records");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				result = session.createSelectionQuery("select count(*) from UnitTrayLabel ", Long.class).getSingleResult().intValue();
				session.getTransaction().commit();
				log.debug("find all successful, count= " + result);
			} catch (PersistenceException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
				throw e;
			} finally { 
			    try { session.close(); } catch (SessionException e) { }
			}
			return result;
		} catch (RuntimeException re) {
			log.error("count failed", re);
			throw re;
		}
	}	

	/**
	 * 
	 * @param leadingBlank if true, the first item in the return list is an empty string
	 * @return a list of the distinct family names in the taxon authority file.
	 */
	public List<String> findDistinctFamilies(boolean leadingBlank) {
		List<String> results = new ArrayList<String>();
		if (leadingBlank) { 
			results.add("");
		}
		log.debug("finding distinct families in UnitTrayLabel");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				results.addAll(session.createSelectionQuery("select distinct u.family from UnitTrayLabel u order by u.family ", String.class).getResultList());
				session.getTransaction().commit();
				log.debug("find distinct families successful, result size: " + results.size());
			} catch (PersistenceException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
			} finally { 
			    try { session.close(); } catch (SessionException e) { }
			}
		} catch (RuntimeException re) {
			log.error("find distinct families failed", re);
			throw re;
		}
		return results;
	}
	
	/**
	 * @param leadingBlank if true, the first item in the return list is an empty string.
	 * @return a list of the distinct generic names in the UnitTrayLabel data set.
	 */
	public List<String> findDistinctGenera(boolean leadingBlank) {
		List<String> results = new ArrayList<String>();
		if (leadingBlank) { 
			results.add("");
		}
		log.debug("finding distinct genera in UnitTrayLabel");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				results.addAll(session.createSelectionQuery("select distinct u.genus from UnitTrayLabel u order by u.genus ", String.class).getResultList());
				session.getTransaction().commit();
				log.debug("find distinct families successful, result size: " + results.size());
			} catch (PersistenceException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
			} finally { 
			    try { session.close(); } catch (SessionException e) { }
			}
		} catch (RuntimeException re) {
			log.error("find distinct genera failed", re);
			throw re;
		}
		return results;
	}



	
}
