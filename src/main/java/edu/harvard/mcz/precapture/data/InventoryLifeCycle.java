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
import java.util.Iterator;
import java.util.List;
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

/** UnitTrayLabelLifeCycle
 * 
 * @author Paul J. Morris
 *
 */
public class InventoryLifeCycle  {
	
private static final Log log = LogFactory.getLog(InventoryLifeCycle.class);
	

	public void persist(Inventory transientInstance) throws SaveFailedException {
		log.debug("persisting Inventory instance");
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
				throw new SaveFailedException("Save to Inventory table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Inventory instance) throws SaveFailedException {
		log.debug("attaching dirty Inventory instance");
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
	
	public void attachClean(Inventory instance) {
		log.debug("attaching clean Inventory instance");
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

	public void delete(Inventory persistentInstance) throws SaveFailedException {
		log.debug("deleting Inventory instance");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				Inventory managed = session.find(Inventory.class, persistentInstance.getId());
				if (managed!=null) {
					session.remove(managed);
				}
				session.getTransaction().commit();
				log.debug("delete successful");
			} catch (PersistenceException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
				throw new SaveFailedException("Delete from Inventory table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Inventory merge(Inventory detachedInstance) throws SaveFailedException {
		log.debug("merging Inventory instance");
		try {
			Inventory result = detachedInstance;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				result = session.merge(detachedInstance);
				session.getTransaction().commit();
				log.debug("merge successful");
			} catch (PersistenceException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
				throw new SaveFailedException("Save to Inventory table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Inventory findById(java.lang.Integer id) {
		log.debug("getting Inventory instance with id: " + id);
		try {
			Inventory instance = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				instance = session.find(Inventory.class, Long.valueOf(id));
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
	public List<Inventory> findByExample(Inventory instance) {
		log.debug("finding Inventory instance by example");
		try {
			List<Inventory> results = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				// Query by example: match on each non-null property except the identifier
				// (equivalent of the legacy org.hibernate.criterion.Example.create(instance)).
				CriteriaBuilder builder = session.getCriteriaBuilder();
				CriteriaQuery<Inventory> query = builder.createQuery(Inventory.class);
				Root<Inventory> root = query.from(Inventory.class);
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (instance.getCabinet()!=null) {
					predicates.add(builder.equal(root.get("cabinet"), instance.getCabinet()));
				}
				if (instance.getTaxon()!=null) {
					predicates.add(builder.equal(root.get("taxon"), instance.getTaxon()));
				}
				predicates.add(builder.equal(root.get("thickness"), instance.getThickness()));
				predicates.add(builder.equal(root.get("sheetsPerUnitThickness"), instance.getSheetsPerUnitThickness()));
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
	 * @return a list of all the Inventory records as Inventory objects
	 */
	@SuppressWarnings("unchecked")
	public List<Inventory> findAll() {
		log.debug("finding all Inventory records");
		try {
			List<Inventory> results = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				results = session.createSelectionQuery("from Inventory i order by i.cabinet, i.taxon ", Inventory.class).getResultList();
				session.getTransaction().commit();
				log.debug("find all successful, result size: "
						+ results.size());
			} catch (PersistenceException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
			} finally { 
			    try { session.close(); } catch (SessionException e) { }
			}
			return results;
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	/**
	 * select count(*) from Inventory;
	 * 
	 * @return the number of rows in the Inventory table.
	 */
	public int count() {
		int result = 0;
		log.debug("counting Inventory records");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				result = session.createSelectionQuery("select count(*) from Inventory i", Long.class).getSingleResult().intValue();
				session.getTransaction().commit();
				log.debug("count successful, count= " + result);
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
	 * like count() but throws exceptions.
	 * 
	 * Used in startup to test for the existance of database tables.
	 * 
	 * @return the number of rows in the Inventory table.
	 */
	public int countAtStartup() throws Exception {
		int result = 0;
		log.debug("counting Inventory records");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				result = session.createSelectionQuery("select count(*) from Inventory i", Long.class).getSingleResult().intValue();
				session.getTransaction().commit();
				log.debug("count successful, count= " + result);
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
	
	public boolean deleteAll() {
		boolean result = false;
		log.debug("deleting all Inventory records");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				int rows = session.createMutationQuery("delete from Inventory ").executeUpdate();
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
	
    public static int exportToCSV(String filename) throws IOException {
    	int numberWritten = 0;
    	CSVPrinter writer = CsvUtility.openForWriting(filename, StandardCharsets.ISO_8859_1);
    	String[] header = {"Cabinet","Taxon","Thickness","SheetsPerUnitThickness"};
    	writer.printRecord((Object[])header);
    	InventoryLifeCycle ils = new InventoryLifeCycle();
    	List<Inventory> list = ils.findAll();
    	Iterator<Inventory> i = list.iterator();
    	while (i.hasNext()) { 
    		Inventory inventory = i.next();
    		ArrayList<String> row = new ArrayList<String>();
    		row.add(inventory.getCabinet());
    		row.add(inventory.getTaxon());
    		row.add(((Float)inventory.getThickness()).toString());
    		row.add(((Float)inventory.getSheetsPerUnitThickness()).toString());
    		writer.printRecord(row);
    		numberWritten++;
    	}
    	writer.close();
    	return numberWritten;
    } 
    
    public static int loadFromCSV(String filename, boolean deleteAllFirst) throws IOException {
    	int numberLoaded = 0;
    	CSVParser reader = CsvUtility.openForReading(filename, StandardCharsets.ISO_8859_1);
    	InventoryLifeCycle ils = new InventoryLifeCycle();
    	if (deleteAllFirst) { 
    	    ils.deleteAll();
    	}
    	for (CSVRecord record : reader) { 
    		Inventory inventory = new Inventory();
    		inventory.setCabinet(CsvUtility.get(record, "Cabinet"));
    		inventory.setTaxon(CsvUtility.get(record, "Taxon"));
    		inventory.setThickness(Float.parseFloat(CsvUtility.get(record, "Thickness")));
    		inventory.setSheetsPerUnitThickness(Float.parseFloat(CsvUtility.get(record, "SheetsPerUnitThickness")));
    		try {
				ils.persist(inventory);
				numberLoaded++;
			} catch (SaveFailedException e) {
				log.error(e.getMessage());
			}
    	}
    	reader.close();
    	return numberLoaded;
    }    
	
}
