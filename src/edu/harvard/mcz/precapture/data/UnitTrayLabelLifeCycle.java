/**
 * UnitTrayLabelLifeCycle.java
 * edu.harvard.mcz.precapture.data
 * Copyright © 2009 President and Fellows of Harvard College
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
 */
package edu.harvard.mcz.precapture.data;

import static org.hibernate.criterion.Example.create;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionException;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

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
    	int numberLoaded = 0;
    	CsvReader reader = new CsvReader(filename);
    	reader.readHeaders();
    	UnitTrayLabelLifeCycle uls = new UnitTrayLabelLifeCycle();
    	uls.deleteAll();
    	while (reader.readRecord()) { 
    		UnitTrayLabel unitTrayLabel = new UnitTrayLabel();
    		unitTrayLabel.setFamily(reader.get("Family"));
    		unitTrayLabel.setGenus(reader.get("Genus"));
    		unitTrayLabel.setSpecificEpithet(reader.get("SpecificEpithet"));
    		unitTrayLabel.setSubspecificEpithet(reader.get("SubspecificEpithet"));
    		unitTrayLabel.setInfraspecificEpithet(reader.get("InfraspecificEpithet"));
    		unitTrayLabel.setInfraspecificRank(reader.get("InfraspecificRank"));
    		unitTrayLabel.setAuthorship(reader.get("Authorship"));
    		try {
				uls.persist(unitTrayLabel);
				numberLoaded++;
			} catch (SaveFailedException e) {
				log.error(e.getMessage());
			}
    	}
    	reader.close();
    	return numberLoaded;
    }

    public static int exportToCSV(String filename) throws IOException {
    	int numberWritten = 0;
    	CsvWriter writer = new CsvWriter(filename);
    	String[] header = {"Family","Genus","SpecificEpithet","SubspecificEpithet","InfraspecificEpithet","InfraspecificRank","Authorship"};
    	writer.writeRecord(header,false);
    	UnitTrayLabelLifeCycle uls = new UnitTrayLabelLifeCycle();
    	List<UnitTrayLabel> list = uls.findAll();
    	Iterator<UnitTrayLabel> i = list.iterator();
    	while (i.hasNext()) { 
    		UnitTrayLabel unitTrayLabel = i.next();
    		ArrayList<String> row = new ArrayList<String>();
    		row.add(unitTrayLabel.getFamily());
    		row.add(unitTrayLabel.getGenus());
    		row.add(unitTrayLabel.getSpecificEpithet());
    		row.add(unitTrayLabel.getSubspecificEpithet());
    		row.add(unitTrayLabel.getInfraspecificEpithet());
    		row.add(unitTrayLabel.getInfraspecificRank());
    		row.add(unitTrayLabel.getAuthorship());
    		writer.writeRecord(row.toArray(new String[row.size()]), true);
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
			} catch (HibernateException e) { 
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
				session.saveOrUpdate(instance);
				session.getTransaction().commit();
				log.debug("attach successful");
			} catch (HibernateException e) { 
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
			} catch (HibernateException e) { 
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
				session.delete(persistentInstance);
				session.getTransaction().commit();
				log.debug("delete successful");
			} catch (HibernateException e) { 
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
				result = (UnitTrayLabel) session.merge(detachedInstance);
				session.getTransaction().commit();
				log.debug("merge successful");
			} catch (HibernateException e) { 
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
				instance = (UnitTrayLabel) session.get("edu.harvard.mcz.precapture.data.UnitTrayLabel", id);
				session.getTransaction().commit();
				if (instance == null) {
					log.debug("get successful, no instance found");
				} else {
					log.debug("get successful, instance found");
				}
			} catch (HibernateException e) { 
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
				Criteria criteria = session.createCriteria("edu.harvard.mcz.precapture.data.UnitTrayLabel");
				if (instance.getFamily()!=null && instance.getFamily().length()>0) { 
					criteria.add(Restrictions.eq("family", instance.getFamily()));
				}
				if (instance.getGenus()!=null && instance.getGenus().length()>0) { 
					criteria.add(Restrictions.eq("genus", instance.getGenus()));
				}
				results = (List<UnitTrayLabel>) criteria.list();
				session.getTransaction().commit();
				log.debug("find by example successful, result size: "
						+ results.size());
			} catch (HibernateException e) { 
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
		log.debug("finding all UnitTrayLabel");
		try {
			List<UnitTrayLabel> results = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				results = (List<UnitTrayLabel>) session.createQuery("from UnitTrayLabel u order by u.ordinal, u.family, u.subfamily, u.tribe, u.genus, u.specificEpithet ").list();
				session.getTransaction().commit();
				log.debug("find all successful, result size: " + results.size());
			} catch (HibernateException e) { 
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

	public boolean deleteAll() {
		boolean result = false;
		log.debug("deleting all UnitTrayLabel records");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				Query delete = session.createQuery("delete from UnitTrayLabel u ");
				int rowsAffected = delete.executeUpdate();
				session.getTransaction().commit();
				log.debug("delete all successful.  Rows=" + rowsAffected);
				result = true;
			} catch (HibernateException e) { 
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
				SQLQuery query = session.createSQLQuery("select max(Ordinal) from UNIT_TRAY_LABEL");
				List queryresult = query.list();
				if (!queryresult.isEmpty()) { 
					// MySQL returns an integer, Oracle returns a BigDecimal
					// Need to cast from either in a system independent way.
					// NOTE: This will fail if maximum value of ordinal exceeds the size of Integer.
					String temp  = queryresult.get(0).toString();
					result = Integer.valueOf(temp);
					log.debug(result);
				}
			} catch (HibernateException e) { 
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
				result = ((Long)session.createQuery("select count(*) from UnitTrayLabel ").iterate().next()).intValue();
				session.getTransaction().commit();
				log.debug("find all successful, count= " + result);
			} catch (HibernateException e) { 
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
				result = ((Long)session.createQuery("select count(*) from UnitTrayLabel ").iterate().next()).intValue();
				session.getTransaction().commit();
				log.debug("find all successful, count= " + result);
			} catch (HibernateException e) { 
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
				results.addAll((List<String>) session.createQuery("select distinct u.family from UnitTrayLabel u order by u.family ").list());
				session.getTransaction().commit();
				log.debug("find distinct families successful, result size: " + results.size());
			} catch (HibernateException e) { 
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
				results.addAll((List<String>) session.createQuery("select distinct u.genus from UnitTrayLabel u order by u.genus ").list());
				session.getTransaction().commit();
				log.debug("find distinct families successful, result size: " + results.size());
			} catch (HibernateException e) { 
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
