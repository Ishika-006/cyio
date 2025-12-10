package com.ishika.foodwaste.web.interfaces;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ishika.foodwaste.jpa.entity.UserFeedback;
@Component
public interface FeedbackFacade {
	 boolean saveFeedback(UserFeedback feedback);
	 List<UserFeedback> getAllFeedbacks();
	 long getFeedbackCount();
	 boolean addFeedback(UserFeedback feedback);
	   boolean deleteFeedbackById(int id);
}
