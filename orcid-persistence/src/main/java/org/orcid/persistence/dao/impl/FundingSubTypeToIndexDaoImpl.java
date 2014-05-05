package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.orcid.persistence.dao.FundingSubTypeToIndexDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class FundingSubTypeToIndexDaoImpl implements FundingSubTypeToIndexDao {
    
    private static final Logger LOG = LoggerFactory.getLogger(FundingSubTypeToIndexDaoImpl.class);
    
    @PersistenceContext(unitName = "orcid")
    protected EntityManager entityManager;
    
    public void addSubTypes(String subtype, String orcid) {
        if(!exists(subtype)) {
            Query query = entityManager.createNativeQuery("INSERT INTO funding_subtype_to_index(orcid, subtype) values(orcid=:orcid, subtype=:subtype)");
            query.setParameter("orcid", orcid);
            query.setParameter("subtype", subtype.trim());
            query.executeUpdate();
        } else {
            LOG.debug("Subtype %s already exists", subtype);
        }
    }
    
    public void removeSubTypes(String subtype) {
        Query query = entityManager.createNativeQuery("DELETE FROM funding_subtype_to_index WHERE subtype=:subtype");
        query.setParameter("subtype", subtype.trim());
        query.executeUpdate();
    }
    
    public void removeSubTypes(String subtype, String orcid) {
        Query query = entityManager.createNativeQuery("DELETE FROM funding_subtype_to_index WHERE subtype=:subtype and orcid=:orcid");
        query.setParameter("subtype", subtype.trim());
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }
    
    private boolean exists(String subtype) {
        Query query = entityManager.createNativeQuery("SELECT count(*) FROM funding_subtype_to_index WHERE subtype=:subtype");
        query.setParameter("subtype", subtype.trim());
        return query.getFirstResult() > 0;
    } 
    
    @SuppressWarnings("unchecked")
    public List<String> getSubTypes() {
        Query query = entityManager
                .createNativeQuery("SELECT subtype FROM funding_subtype_to_index", String.class);
        return query.getResultList(); 
    }
}
