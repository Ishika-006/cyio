package com.ishika.foodwaste.jpa.entityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import com.ishika.foodwaste.jpa.entity.Admin;
import com.ishika.foodwaste.jpa.entity.DeliveryPerson;
import com.ishika.foodwaste.jpa.entity.Donor;
import com.ishika.foodwaste.jpa.entity.FoodDonation;
import com.ishika.foodwaste.jpa.entity.NGOS;
import com.ishika.foodwaste.jpa.utils.TopDonorDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
@Repository
@Transactional
public class FoodDonationManager {

    @PersistenceContext
    private EntityManager em;

    // ✅ Add a new food donation
    public boolean addDonation(FoodDonation donation) {
        try {
            em.getTransaction().begin();
            em.persist(donation);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }
        return false;
    }
    public Optional<FoodDonation> findById(int donationId) {
        FoodDonation donation = em.find(FoodDonation.class, donationId);
        return Optional.ofNullable(donation);
    }
//    
//    public FoodDonation findById(int id) {
//        try {
//            return em.find(FoodDonation.class, id);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Donation not found with ID: " + id);
//        }
//    }
    @Transactional
    public boolean save(FoodDonation donation) {
        try {
            if (donation.getFid() == 0) {
                em.persist(donation); // new donation
            } else {
                em.merge(donation);   // update existing
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    @Transactional
    public List<FoodDonation> findDeliveriesByDonorId(Long donorId) {
        try {
            TypedQuery<FoodDonation> query = em.createQuery(
                "SELECT f FROM FoodDonation f WHERE f.donor.id = :donorId", FoodDonation.class);
            query.setParameter("donorId", donorId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Transactional
    public Optional<FoodDonation> findById(Long fid) {
        try {
            TypedQuery<FoodDonation> query = em.createQuery(
                "SELECT f FROM FoodDonation f WHERE f.fid = :fid", FoodDonation.class);
            query.setParameter("fid", fid);
            FoodDonation result = query.getSingleResult();
            return Optional.ofNullable(result);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Transactional
    public boolean save1(Donor donor) {
        try {
            if (donor.getId() == 0) {
                em.persist(donor); // new donation
            } else {
                em.merge(donor);   // update existing
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Transactional
    public boolean save2(NGOS ngo) {
        try {
            if (ngo.getId() == 0) {
                em.persist(ngo); // new donation
            } else {
                em.merge(ngo);   // update existing
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Transactional
    public boolean save3(DeliveryPerson dp) {
        try {
            if (dp.getDid() == 0) {
                em.persist(dp); // new donation
            } else {
                em.merge(dp);   // update existing
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Transactional
    public boolean save4(Admin a) {
        try {
            if (a.getAid() == 0) {
                em.persist(a); // new donation
            } else {
                em.merge(a);   // update existing
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // ✅ Get donations by location
    public List<FoodDonation> getDonationsByLocation(String location) {
        try {
            TypedQuery<FoodDonation> query = em.createNamedQuery("FoodDonation.findByLocation", FoodDonation.class);
            query.setParameter("location", location);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<FoodDonation> getRecentDonationsByDonor(Long donorId) {
        try {
            TypedQuery<FoodDonation> query = em.createNamedQuery("FoodDonation.findRecentByDonor", FoodDonation.class);
            query.setParameter("donorId", donorId);
            query.setMaxResults(5);  // Limit to 5 most recent
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean existsById(int id) {
        try {
            FoodDonation donation = em.find(FoodDonation.class, id);
            return donation != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Get all donations
    public List<FoodDonation> getAllDonations() {
        try {
            TypedQuery<FoodDonation> query = em.createQuery("SELECT f FROM FoodDonation f", FoodDonation.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

//     ✅ Update donation by ID
    public boolean updateDonation(int id, FoodDonation updated) {
        try {
            em.getTransaction().begin();
            FoodDonation existing = em.find(FoodDonation.class, id);
            if (existing != null) {
                existing.setEmail(updated.getEmail());
                existing.setFood(updated.getFood());
                existing.setType(updated.getType());
                existing.setCategory(updated.getCategory());
                existing.setPhoneno(updated.getPhoneno());
                existing.setLocation(updated.getLocation());
                existing.setAddress(updated.getAddress());
                existing.setName(updated.getName());
                existing.setQuantity(updated.getQuantity());
                em.merge(existing);
                em.getTransaction().commit();
                return true;
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }
        return false;
    }

//     ✅ Delete donation by ID
    @Transactional
    public boolean deleteDonationById(int id) {
        try {
//            em.getTransaction().begin();
            FoodDonation donation = em.find(FoodDonation.class, id);
            if (donation != null) {
                em.remove(donation);
//                em.getTransaction().commit();
                return true;
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Count total donations
    public long count() {
        try {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(f) FROM FoodDonation f", Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    @Transactional
    public long countByDeliveryPerson(DeliveryPerson person) {
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(f) FROM FoodDonation f WHERE f.deliveryPerson = :person", Long.class
            );
            query.setParameter("person", person);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    @Transactional
    public long countByDeliveryPersonInLastWeek(DeliveryPerson person, LocalDateTime fromDate) {
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(f) FROM FoodDonation f WHERE f.deliveryPerson = :person AND f.date >= :fromDate",
                Long.class
            );
            query.setParameter("person", person);
            query.setParameter("fromDate", fromDate);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    
    public int countByDeliveryPersonAndDate(DeliveryPerson person, LocalDate date) {
        try {
            return em.createQuery(
                "SELECT COUNT(f) FROM FoodDonation f WHERE f.deliveryPerson = :person AND DATE(f.date) = :date",
                Long.class
            )
            .setParameter("person", person)
            .setParameter("date", date)
            .getSingleResult()
            .intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<TopDonorDTO> getTopDonorsThisMonth() {
        try {
            return em.createNamedQuery("FoodDonation.findTopDonorsThisMonth", TopDonorDTO.class)
                     .setMaxResults(5) // optional: limit top 5
                     .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }
    
    public List<FoodDonation> getDonationsThisMonth() {
        try {
            LocalDate now = LocalDate.now();
            TypedQuery<FoodDonation> query = em.createQuery(
                "SELECT f FROM FoodDonation f WHERE MONTH(f.date) = :month AND YEAR(f.date) = :year", FoodDonation.class);
            query.setParameter("month", now.getMonthValue());
            query.setParameter("year", now.getYear());
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Object[]> countByNGO() {
        try {
            // Replace 'ngo.name' with the actual property of NGO (adjust if needed)
            return em.createQuery(
                "SELECT f.assignedNGO.name, COUNT(f) FROM FoodDonation f GROUP BY f.assignedNGO.name\r\n"
                + "", Object[].class)
                .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<FoodDonation> findAll() {
        try {
            TypedQuery<FoodDonation> query = em.createQuery("SELECT f FROM FoodDonation f", FoodDonation.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    
 // Get count grouped by location
    public List<Object[]> countByLocation() {
        try {
            return em.createNamedQuery("FoodDonation.countByLocation", Object[].class)
                     .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Get count grouped by status
    public List<Object[]> countByStatus() {
        try {
            return em.createNamedQuery("FoodDonation.countByStatus", Object[].class)
                     .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Transactional
    public List<Map<String, Object>> countDonationsGroupedByMonth() {
        try {
            int currentYear = LocalDate.now().getYear();
            
            // Query: month number and count grouped by month for current year
            List<Object[]> result = em.createQuery(
                "SELECT MONTH(f.date), COUNT(f) FROM FoodDonation f WHERE YEAR(f.date) = :year GROUP BY MONTH(f.date) ORDER BY MONTH(f.date)",
                Object[].class)
                .setParameter("year", currentYear)
                .getResultList();
            
            // Map month number to donation count
            Map<Integer, Long> monthCountMap = new HashMap<>();
            for (Object[] row : result) {
                Integer monthNum = (Integer) row[0];
                Long count = (Long) row[1];
                monthCountMap.put(monthNum, count);
            }
            
            // Month names map
            Map<Integer, String> monthNames = new HashMap<>();
            monthNames.put(1, "January");
            monthNames.put(2, "February");
            monthNames.put(3, "March");
            monthNames.put(4, "April");
            monthNames.put(5, "May");
            monthNames.put(6, "June");
            monthNames.put(7, "July");
            monthNames.put(8, "August");
            monthNames.put(9, "September");
            monthNames.put(10, "October");
            monthNames.put(11, "November");
            monthNames.put(12, "December");
            
            int currentMonth = LocalDate.now().getMonthValue();
            List<Map<String, Object>> response = new ArrayList<>();
            
            // Prepare response for months till current month, missing months get 0 count
            for (int m = 1; m <= currentMonth; m++) {
                Map<String, Object> map = new HashMap<>();
                map.put("month", monthNames.get(m));
                map.put("donations", monthCountMap.getOrDefault(m, 0L).intValue());
                response.add(map);
            }
            
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }
}
