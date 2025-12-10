package com.ishika.foodwaste.jpa.entityManager;

import com.ishika.foodwaste.jpa.entity.DeliveryLocation;
import com.ishika.foodwaste.jpa.entity.DeliveryPerson;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class DeliveryLocationManager {

    @PersistenceContext
    private EntityManager em;

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

    @Transactional(readOnly = true)
    public DeliveryLocation findByDid(int did) {
        try {
            TypedQuery<DeliveryLocation> query = em.createQuery(
                "SELECT l FROM DeliveryLocation l WHERE l.deliveryPerson.did = :did",
                DeliveryLocation.class
            );
            query.setParameter("did", did);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null; // No location found for this ID
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public DeliveryLocation saveOrUpdate(DeliveryLocation location) {
        if (location.getId() == null) {
            em.persist(location);
            return location;
        } else {
            return em.merge(location);
        }
    }
    public DeliveryLocation findLatestLocation() {
        try {
            return em.createQuery(
                "SELECT dl FROM DeliveryLocation dl ORDER BY dl.timestamp DESC", DeliveryLocation.class)
                .setMaxResults(1)
                .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public DeliveryLocation findByDeliveryPersonId(int deliveryPersonId) {
        try {
            return em.createQuery(
                "SELECT dl FROM DeliveryLocation dl WHERE dl.deliveryPerson.id = :dpId", DeliveryLocation.class)
                .setParameter("dpId", deliveryPersonId)
                .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
