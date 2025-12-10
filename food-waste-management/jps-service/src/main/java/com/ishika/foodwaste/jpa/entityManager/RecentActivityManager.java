package com.ishika.foodwaste.jpa.entityManager;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.ishika.foodwaste.jpa.entity.RecentActivity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
@Repository
public class RecentActivityManager {
	
    @PersistenceContext
    private EntityManager entityManager;

        @Transactional
        public void logActivity(String name, String action, String target, String status) {
            RecentActivity activity = new RecentActivity();
            activity.setName(name);
            activity.setAction(action);
            activity.setTarget(target);
            activity.setStatus(status);
            activity.setTimestamp(LocalDateTime.now());

            entityManager.persist(activity);
        }
        
        public List<RecentActivity> findTop10ByOrderByTimestampDesc() {
            TypedQuery<RecentActivity> query = entityManager.createQuery(
                "SELECT r FROM RecentActivity r ORDER BY r.timestamp DESC", RecentActivity.class);
            query.setMaxResults(10);  // Limit to top 10 recent activities
            return query.getResultList();
        }
    }
