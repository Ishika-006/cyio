package com.ishika.foodwaste.jpa.utils;

public class TopDonorDTO {
    private String name;
    private String email;
    private Long donationCount;

    public TopDonorDTO(String name, String email, Long donationCount) {
        this.name = name;
        this.email = email;
        this.donationCount = donationCount;
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

	public Long getDonationCount() {
		return donationCount;
	}

	public void setDonationCount(Long donationCount) {
		this.donationCount = donationCount;
	}

    // Getters and Setters
    
}
