package com.ishika.foodwaste.jpa.entityManager;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ishika.foodwaste.jpa.entity.UserFeedback;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
@Repository
public class UserFeedbackManager {
	  @PersistenceContext
	    private EntityManager em;

	    // ✅ Save feedback
	  @Transactional
	    public boolean saveFeedback(UserFeedback feedback) {
	        try {
//	            em.getTransaction().begin();
	            em.persist(feedback);
//	            em.getTransaction().commit();
	            return true;
	        } catch (Exception e) {
//	            em.getTransaction().rollback();
	            e.printStackTrace();
	        }
	        return false;
	    }

	  @Transactional
	    public boolean deleteFeedbackById(int id) {
	        try {
	            UserFeedback feedback = em.find(UserFeedback.class, id);
	            if (feedback != null) {
	                em.remove(feedback);
	                return true;
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return false;
	    }
	  
	    // ✅ Get all feedbacks
	    public List<UserFeedback> getAllFeedbacks() {
	        try {
	            TypedQuery<UserFeedback> query = em.createQuery("SELECT f FROM UserFeedback f", UserFeedback.class);
	            return query.getResultList();
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }


	    // ✅ Optional: Count total feedbacks
	    public long count() {
	        try {
	            TypedQuery<Long> query = em.createQuery("SELECT COUNT(f) FROM UserFeedback f", Long.class);
	            return query.getSingleResult();
	        } catch (Exception e) {
	            e.printStackTrace();
	            return 0;
	        }
	    }
}
