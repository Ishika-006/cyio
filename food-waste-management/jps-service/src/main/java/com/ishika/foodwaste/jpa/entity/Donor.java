package com.ishika.foodwaste.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ishika.foodwaste.jpa.enums.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "donor")
public class Donor {
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String name;
	    private String password;
	    public String getPassword() {
			return password;
		}



		public void setPassword(String password) {
			this.password = password;
		}


		 private String city;
		public String getCity() {
			return city;
		}



		 public void setCity(String city) {
			 this.city = city;
		 }


		private String email;
	    private String contact;
	    private String address;
	    private String donorType; // e.g., Individual / Restaurant

	
	
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



		public String getEmail() {
			return email;
		}



		public void setEmail(String email) {
			this.email = email;
		}



		public String getContact() {
			return contact;
		}



		public void setContact(String contact) {
			this.contact = contact;
		}



		public String getAddress() {
			return address;
		}



		public void setAddress(String address) {
			this.address = address;
		}



		public String getDonorType() {
			return donorType;
		}



		public void setDonorType(String donorType) {
			this.donorType = donorType;
		}



		public Role getRole() {
			return role;
		}



		public void setRole(Role role) {
			this.role = role;
		}



	@Enumerated(EnumType.STRING)
    private Role role; 
}
