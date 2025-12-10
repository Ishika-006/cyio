package com.ishika.foodwaste.jpa.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "donations")
public class Donation {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String foodItem;
    private double quantityInKg;
    private int peopleHelped;
    private LocalDateTime date;

    @ManyToOne
    private Donor donor;

    // Getters & Setters
    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFoodItem() {
		return foodItem;
	}

	public void setFoodItem(String foodItem) {
		this.foodItem = foodItem;
	}

	public double getQuantityInKg() {
		return quantityInKg;
	}

	public void setQuantityInKg(double quantityInKg) {
		this.quantityInKg = quantityInKg;
	}

	public int getPeopleHelped() {
		return peopleHelped;
	}

	public void setPeopleHelped(int peopleHelped) {
		this.peopleHelped = peopleHelped;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public Donor getDonor() {
		return donor;
	}

	public void setDonor(Donor donor) {
		this.donor = donor;
	}


}
