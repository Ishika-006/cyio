package com.ishika.foodwaste.web.dto;

public class DeliveryRequestDTO {
    private int fid;

    private String ngoName;
    private String ngoAddress;
    private String ngoCity;

    private String donorName;
    private String donorAddress;
    private String donorCity;

    private String food;
    private String type;
    private String category;
    private String quantity;
	public int getFid() {
		return fid;
	}
	public void setFid(int fid) {
		this.fid = fid;
	}
	public String getNgoName() {
		return ngoName;
	}
	public void setNgoName(String ngoName) {
		this.ngoName = ngoName;
	}
	public String getNgoAddress() {
		return ngoAddress;
	}
	public void setNgoAddress(String ngoAddress) {
		this.ngoAddress = ngoAddress;
	}
	public String getNgoCity() {
		return ngoCity;
	}
	public void setNgoCity(String ngoCity) {
		this.ngoCity = ngoCity;
	}
	public String getDonorName() {
		return donorName;
	}
	public void setDonorName(String donorName) {
		this.donorName = donorName;
	}
	public String getDonorAddress() {
		return donorAddress;
	}
	public void setDonorAddress(String donorAddress) {
		this.donorAddress = donorAddress;
	}
	public String getDonorCity() {
		return donorCity;
	}
	public void setDonorCity(String donorCity) {
		this.donorCity = donorCity;
	}
	public String getFood() {
		return food;
	}
	public void setFood(String food) {
		this.food = food;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

//    private double distance;       // Optional
//    private String estimatedTime;  // Optional

    // Getters & Setters
    // (You can use Lombok if preferred)
    
}

