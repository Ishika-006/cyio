package com.ishika.foodwaste.jpa.entity;

import com.ishika.foodwaste.jpa.enums.Role;

import jakarta.persistence.*;

@Entity
@Table(name = "user_feedback")
public class UserFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int feedbackId;

    private String name;
    private String email;
    private String message;

    private String userType;
    private String aiFreshness;
    private String detailedFeedback;
    private String suggestions;
    public int getFeedbackId() {
		return feedbackId;
	}
	public void setFeedbackId(int feedbackId) {
		this.feedbackId = feedbackId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getAiFreshness() {
		return aiFreshness;
	}
	public void setAiFreshness(String aiFreshness) {
		this.aiFreshness = aiFreshness;
	}
	public String getDetailedFeedback() {
		return detailedFeedback;
	}
	public void setDetailedFeedback(String detailedFeedback) {
		this.detailedFeedback = detailedFeedback;
	}
	public String getSuggestions() {
		return suggestions;
	}
	public void setSuggestions(String suggestions) {
		this.suggestions = suggestions;
	}
	public int getOverallExperience() {
		return overallExperience;
	}
	public void setOverallExperience(int overallExperience) {
		this.overallExperience = overallExperience;
	}
	private int overallExperience;

}
