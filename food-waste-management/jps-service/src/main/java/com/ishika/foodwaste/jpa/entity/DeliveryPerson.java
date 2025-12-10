package com.ishika.foodwaste.jpa.entity;

import com.ishika.foodwaste.jpa.enums.Role;

import jakarta.persistence.*;

@Entity
@Table(name = "delivery_persons")
public class DeliveryPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int did;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String city;
    
    private Double latitude;   // new field
    private Double longitude;  // new field

    // getters and setters
    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public Double getLongitude() {
        return longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
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

    public int getDid() {
        return did;
    }

    public void setDid(int did) {
        this.did = did;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}

