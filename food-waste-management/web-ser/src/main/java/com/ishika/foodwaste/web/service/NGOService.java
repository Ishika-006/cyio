package com.ishika.foodwaste.web.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ishika.foodwaste.jpa.entity.Donor;
import com.ishika.foodwaste.jpa.entity.NGOS;
import com.ishika.foodwaste.jpa.entityManager.DonorManager;
import com.ishika.foodwaste.jpa.entityManager.FoodDonationManager;
import com.ishika.foodwaste.jpa.entityManager.NGOManager;
import com.ishika.foodwaste.web.interfaces.NGOFacade;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;


@Service
public class NGOService implements NGOFacade {
	@Autowired
	NGOManager nm;
	   @Autowired
	    private EntityManager entityManager;
	   
	   @Autowired
	    private PasswordEncoder passwordEncoder;
	   @Autowired
	   FoodDonationManager repo;
	   
	  @Override
	    public NGOS authenticate(String email) {
		  NGOS ngo = nm.findByEmail(email);
           return ngo;
	    }
	    @Override
	    public boolean registerNGO(NGOS ngo) {
	    	ngo.setPassword(passwordEncoder.encode(ngo.getPassword()));
	        return nm.save(ngo);
	    }
	    @Transactional
	    public void save(NGOS ngo) {
	        repo.save2(ngo);
	    }
}
