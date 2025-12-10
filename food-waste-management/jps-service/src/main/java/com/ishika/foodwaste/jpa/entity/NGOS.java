package com.ishika.foodwaste.jpa.entity;

import com.ishika.foodwaste.jpa.enums.Role;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class NGOS {

	   @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String name;
      
	    private String registrationNumber;

	    private String email;
	    
	    private String password;

	    public String getPassword() {
			return password;
		}


		public void setPassword(String password) {
			this.password = password;
		}


		private String contactNumber;

	    private String address;

	    private String city;

	    private String state;

	    private String country;

	    private String website;

	    private String typeOfWork;

	    private String founderName;

	    private int yearOfEstablishment;

	    private boolean isActive;

    
    public Long getId() {
			return id;
		}


		public void setId(Long id) {
			this.id = id;
		}


		public String getName() {
			return name;
		}


		public void setName(String name) {
			this.name = name;
		}


		public String getRegistrationNumber() {
			return registrationNumber;
		}


		public void setRegistrationNumber(String registrationNumber) {
			this.registrationNumber = registrationNumber;
		}


		public String getEmail() {
			return email;
		}


		public void setEmail(String email) {
			this.email = email;
		}


		public String getContactNumber() {
			return contactNumber;
		}


		public void setContactNumber(String contactNumber) {
			this.contactNumber = contactNumber;
		}


		public String getAddress() {
			return address;
		}


		public void setAddress(String address) {
			this.address = address;
		}


		public String getCity() {
			return city;
		}


		public void setCity(String city) {
			this.city = city;
		}


		public String getState() {
			return state;
		}


		public void setState(String state) {
			this.state = state;
		}


		public String getCountry() {
			return country;
		}


		public void setCountry(String country) {
			this.country = country;
		}


		public String getWebsite() {
			return website;
		}


		public void setWebsite(String website) {
			this.website = website;
		}


		public String getTypeOfWork() {
			return typeOfWork;
		}


		public void setTypeOfWork(String typeOfWork) {
			this.typeOfWork = typeOfWork;
		}


		public String getFounderName() {
			return founderName;
		}


		public void setFounderName(String founderName) {
			this.founderName = founderName;
		}


		public int getYearOfEstablishment() {
			return yearOfEstablishment;
		}


		public void setYearOfEstablishment(int yearOfEstablishment) {
			this.yearOfEstablishment = yearOfEstablishment;
		}


		public boolean isActive() {
			return isActive;
		}


		public void setActive(boolean isActive) {
			this.isActive = isActive;
		}


		public Role getRole() {
			return role;
		}


		public void setRole(Role role) {
			this.role = role;
		}


	@Enumerated(EnumType.STRING)
    private Role role; 

    // Getters and Setters
}