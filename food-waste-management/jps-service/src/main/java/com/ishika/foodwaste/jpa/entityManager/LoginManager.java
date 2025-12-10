package com.ishika.foodwaste.jpa.entityManager;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ishika.foodwaste.jpa.entity.Admin;
import com.ishika.foodwaste.jpa.entity.DeliveryPerson;
import com.ishika.foodwaste.jpa.entity.NGOS;
import com.ishika.foodwaste.jpa.utils.PasswordUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
@Repository
public class LoginManager {
	
	

	 @PersistenceContext
	    private EntityManager em;


    // ✅ Check if email already exists
    public boolean isEmailAdminRegistered(String email) {
        TypedQuery<Long> query = em.createNamedQuery("Admin.existsByEmail", Long.class);
        query.setParameter("email", email);
        Long count = query.getSingleResult();
        return count != null && count > 0;
    }

    // ✅ Register admin
    public boolean registerAdmin(Admin admin) {
        try {
            admin.setPassword(PasswordUtils.hashPassword(admin.getPassword()));
            em.getTransaction().begin();
            em.persist(admin);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Login using named query
    public Admin loginA(String email, String plainPassword) {
        try {
            TypedQuery<Admin> query = em.createNamedQuery("Admin.findByEmail", Admin.class);
            query.setParameter("email", email);
            Admin admin = query.getSingleResult();

            if (PasswordUtils.verifyPassword(plainPassword, admin.getPassword())) {
                return admin;
            }
        } catch (NoResultException e) {
            // No account found
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    public boolean isEmailDeliveryRegistered(String email) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(d) FROM DeliveryPerson d WHERE d.email = :email", Long.class);
        query.setParameter("email", email);
        Long count = query.getSingleResult();
        return count != null && count > 0;
    }

    // ✅ Register a new delivery person
    public boolean registerDeliveryPerson(DeliveryPerson deliveryPerson) {
        try {
            deliveryPerson.setPassword(PasswordUtils.hashPassword(deliveryPerson.getPassword()));
            em.getTransaction().begin();
            em.persist(deliveryPerson);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Login a delivery person
    public DeliveryPerson loginD(String email, String plainPassword) {
        try {
            TypedQuery<DeliveryPerson> query = em.createQuery(
                "SELECT d FROM DeliveryPerson d WHERE d.email = :email", DeliveryPerson.class);
            query.setParameter("email", email);
            DeliveryPerson deliveryPerson = query.getSingleResult();

            if (PasswordUtils.verifyPassword(plainPassword, deliveryPerson.getPassword())) {
                return deliveryPerson;
            }
        } catch (NoResultException e) {
            // No delivery person found
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    
    // ✅ Check if email is already registered
    public boolean isEmailUserRegistered(String email) {
        String queryStr = "SELECT COUNT(u) FROM User u WHERE u.email = :email";
        Long count = em.createQuery(queryStr, Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count != null && count > 0;
    }

    // ✅ Register a new user
    public boolean registerUser(NGOS user) {
        try {
            em.getTransaction().begin();
            user.setPassword(PasswordUtils.hashPassword(user.getPassword()));
            em.persist(user);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Login user
    public Optional<NGOS> loginUser(String email, String password) {
        try {
        	NGOS user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", NGOS.class)
                    .setParameter("email", email)
                    .getSingleResult();

            if (PasswordUtils.verifyPassword(password, user.getPassword())) {
                return Optional.of(user);
            }
        } catch (NoResultException e) {
            // email not found
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public long count() {
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(a) FROM Admin a", Long.class
            );
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
