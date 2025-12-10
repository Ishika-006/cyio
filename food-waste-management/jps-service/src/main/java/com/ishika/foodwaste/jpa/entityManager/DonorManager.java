package com.ishika.foodwaste.jpa.entityManager;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ishika.foodwaste.jpa.entity.Admin;
import com.ishika.foodwaste.jpa.entity.Donor;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;

@Repository
public class DonorManager {

    @PersistenceContext
    private EntityManager em;

//    @Transactional
//    public void save(Donor donor) {
//        try {
//            if (donor.getId() <= 0) {
//                em.persist(donor);
//            } else {
//                em.merge(donor);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    
    @Transactional
    public boolean save(Donor donor) {
        try {
//        	entityManager.getTransaction().begin();
        	em.persist(donor);
//        	entityManager.getTransaction().commit();
            return true;
        } catch (Exception e) {
//        	entityManager.getTransaction().rollback();
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public Donor findByEmail(String email) {
        try {
            TypedQuery<Donor> query = em.createQuery(
                "SELECT d FROM Donor d WHERE d.email = :email",
                Donor.class
            );
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public List<Donor> getAllDonors() {
        try {
            return em.createQuery("SELECT d FROM Donor d", Donor.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public Donor findById(int id) {
        try {
            return em.find(Donor.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public long count() {
        try {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(d) FROM Donor d", Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}

