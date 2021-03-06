package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.ResearchResourceDao;
import org.orcid.persistence.jpa.entities.ResearchResourceEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

public class ResearchResourceDaoImpl extends GenericDaoImpl<ResearchResourceEntity, Long> implements ResearchResourceDao{

    public ResearchResourceDaoImpl() {
        super(ResearchResourceEntity.class);
    }
    
    public ResearchResourceEntity getResearchResource(String userOrcid, Long researchResourceId) {
        Query query = entityManager.createQuery("from ResearchResourceEntity where profile.id=:userOrcid and id=:researchResourceId");
        query.setParameter("userOrcid", userOrcid);
        query.setParameter("researchResourceId", Long.valueOf(researchResourceId));
        return (ResearchResourceEntity) query.getSingleResult();
    }

    @Override
    @Transactional
    public boolean removeResearchResource(String userOrcid, Long researchResourceId) {
        Query queryItem = entityManager.createQuery("delete from ResearchResourceItemEntity where research_resource_id=:researchResourceId");
        queryItem.setParameter("researchResourceId", researchResourceId);
        queryItem.executeUpdate();
        Query query = entityManager.createQuery("delete from ResearchResourceEntity where profile.id=:userOrcid and id=:researchResourceId");
        query.setParameter("userOrcid", userOrcid);
        query.setParameter("researchResourceId", researchResourceId);
        return query.executeUpdate() > 0 ? true : false;
    }    
    
    //note these are not cacheable entities as they require a session to work.
    @Override
    public List<ResearchResourceEntity> getByUser(String userOrcid, long lastModified) {
        TypedQuery<ResearchResourceEntity> query = entityManager.createQuery("from ResearchResourceEntity where profile.id=:userOrcid", ResearchResourceEntity.class);
        query.setParameter("userOrcid", userOrcid);
        return query.getResultList();
    }

    @Override
    public void removeResearchResources(String userOrcid) {
        Query queryItem = entityManager.createQuery("delete from ResearchResourceItemEntity where research_resource_id in (SELECT id from ResearchResourceEntity where profile.id=:userOrcid)");
        queryItem.setParameter("userOrcid", userOrcid);
        queryItem.executeUpdate();

        Query query = entityManager.createQuery("delete from ResearchResourceEntity where profile.id = :userOrcid");
        query.setParameter("userOrcid", userOrcid);
        query.executeUpdate();
    }

}
