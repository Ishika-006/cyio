package com.ishika.foodwaste.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ishika.foodwaste.jpa.entity.UserFeedback;
import com.ishika.foodwaste.jpa.entityManager.UserFeedbackManager;
import com.ishika.foodwaste.web.interfaces.FeedbackFacade;

@Service
public class FeedbackService implements FeedbackFacade {
	  @Autowired
	    private UserFeedbackManager manager;

	    @Override
	    public boolean saveFeedback(UserFeedback feedback) {
	        return manager.saveFeedback(feedback);
	    }
	    
	    @Override
	    public boolean addFeedback(UserFeedback feedback) {
	        try {
	            manager.saveFeedback(feedback);
	            return true;
	        } catch (Exception e) {
	         e.printStackTrace();
	            return false;
	        }
	    }
	    @Override
	    public boolean deleteFeedbackById(int id) {
	        return manager.deleteFeedbackById(id);
	    }

	    @Override
	    public List<UserFeedback> getAllFeedbacks() {
	        return manager.getAllFeedbacks();
	    }

	    @Override
	    public long getFeedbackCount() {
	        return manager.count();
	    }
}
