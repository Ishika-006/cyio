package com.ishika.foodwaste.jpa.entityManager;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ishika.foodwaste.jpa.entity.Admin;
import com.ishika.foodwaste.jpa.entity.NGOS;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
@Repository
public class NGOManager {
    
	  @PersistenceContext
	    private EntityManager em;
	
    public NGOS findByEmail(String email) {
    	TypedQuery<NGOS> query = em.createQuery(
    		    "SELECT a FROM NGOS a WHERE a.email = :email", NGOS.class);
    		query.setParameter("email", email);

    		List<NGOS> result = query.getResultList();
    		if (!result.isEmpty()) {
    		    return result.get(0);
    		}
    		return null;
    }
    
    @Transactional
    public boolean save(NGOS ngo) {
        try {
//        	entityManager.getTransaction().begin();
        	em.persist(ngo);
//        	entityManager.getTransaction().commit();
            return true;
        } catch (Exception e) {
//        	entityManager.getTransaction().rollback();
            e.printStackTrace();
            return false;
        }
    }
    public Optional<NGOS> findById(int ngoId) {
        NGOS ngo = em.find(NGOS.class, ngoId);
        return Optional.ofNullable(ngo);  // safe wrapping
    }
    public long count() {
        try {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(n) FROM NGOS n", Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
