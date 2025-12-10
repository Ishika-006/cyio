package com.ishika.foodwaste.jpa.entityManager;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.ishika.foodwaste.jpa.entity.DeliveryLocation;
import com.ishika.foodwaste.jpa.entity.DeliveryPerson;
import com.ishika.foodwaste.jpa.entity.FoodDonation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
@Repository
public class DeliveryPersonManager {
	   @PersistenceContext
	    private EntityManager em;

	    // ✅ Get unassigned orders in a specific city
	    @Transactional 
	    public List<FoodDonation> getUnassignedOrders(String city) {
	        try {
	            TypedQuery<FoodDonation> query = em.createQuery(
	                "SELECT f FROM FoodDonation f WHERE f.assignedTo IS NOT NULL AND f.deliveryBy IS NULL AND f.location = :city",
	                FoodDonation.class
	            );
	            query.setParameter("city", city);
	            return query.getResultList();
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	    @Transactional
	    public DeliveryPerson findById(int id) {
	        try {
	            return em.find(DeliveryPerson.class, id); // simple entity lookup
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }


	    // ✅ Get orders assigned to this delivery person
	    @Transactional 
	    public List<FoodDonation> getOrdersByDeliveryPerson(int deliveryPersonId) {
	        try {
	            TypedQuery<FoodDonation> query = em.createQuery(
	                "SELECT f FROM FoodDonation f WHERE f.deliveryBy = :deliveryPersonId",
	                FoodDonation.class
	            );
	            query.setParameter("deliveryPersonId", deliveryPersonId);
	            return query.getResultList();
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	    
	    // ✅ Assign delivery to current delivery person (if not already assigned)
	    @Transactional
	    public boolean takeOrder(int orderId, int deliveryPersonId) {
	        try {
	            FoodDonation order = em.find(FoodDonation.class, orderId);
	            DeliveryPerson deliveryPerson = em.find(DeliveryPerson.class, deliveryPersonId);

	            if (order == null || deliveryPerson == null) {
	                return false; // Either order or delivery person doesn't exist
	            }

	            if (order.getDeliveryPerson() != null) {
	                return false; // Already assigned
	            }

	            order.setDeliveryPerson(deliveryPerson);
	            em.merge(order);
	            return true;

	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        }
	    }

	    @Transactional 
	    public DeliveryPerson findByEmail(String email) {
	        try {
	            TypedQuery<DeliveryPerson> query = em.createQuery(
	                "SELECT d FROM DeliveryPerson d WHERE d.email = :email",
	                DeliveryPerson.class
	            );
	            query.setParameter("email", email);
	            return query.getSingleResult();
	        } catch (NoResultException e) {
	            return null; // Not found
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	    @Transactional
	    public DeliveryPerson findById(Long id) {
	        try {
	            TypedQuery<DeliveryPerson> query = em.createQuery(
	                "SELECT d FROM DeliveryPerson d WHERE d.did = :id",
	                DeliveryPerson.class
	            );
	            query.setParameter("id", id);
	            return query.getSingleResult();
	        } catch (NoResultException e) {
	            return null; // Not found
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	    
	    @Transactional
	    public DeliveryLocation findByDeliveryPerson(DeliveryPerson person) {
	        try {
	            TypedQuery<DeliveryLocation> query = em.createQuery(
	                "SELECT l FROM DeliveryLocation l WHERE l.deliveryPerson = :person",
	                DeliveryLocation.class
	            );
	            query.setParameter("person", person);
	            return query.getSingleResult();
	        } catch (NoResultException e) {
	            return null; // Not found
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }


	    
	    @Transactional 
	    public void save(DeliveryPerson person) {
	        try {
	            if (person.getDid() <= 0) {
	                // New record
	                em.persist(person);
	            } else {
	                // Existing record - update
	                em.merge(person);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    public long count() {
	        try {
	            TypedQuery<Long> query = em.createQuery("SELECT COUNT(dp) FROM DeliveryPerson dp", Long.class);
	            return query.getSingleResult();
	        } catch (Exception e) {
	            e.printStackTrace();
	            return 0;
	        }
	    }
	    
	    public List<DeliveryPerson> getAll() {
	        try {
	            return em.createQuery("SELECT d FROM DeliveryPerson d", DeliveryPerson.class).getResultList();
	        } catch (Exception e) {
	            e.printStackTrace();
	            return new ArrayList<>();
	        }
	    }
	    




}
