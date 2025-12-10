package com.ishika.foodwaste.jpa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_locations")
public class DeliveryLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double latitude;

    private double longitude;

    private LocalDateTime timestamp;

    @OneToOne
    @JoinColumn(name = "delivery_person_id", unique = true)
    private DeliveryPerson deliveryPerson;

    // Constructors
    public DeliveryLocation() {}

    public DeliveryLocation(DeliveryPerson deliveryPerson, double latitude, double longitude, LocalDateTime timestamp) {
        this.deliveryPerson = deliveryPerson;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public DeliveryPerson getDeliveryPerson() {
        return deliveryPerson;
    }

    public void setDeliveryPerson(DeliveryPerson deliveryPerson) {
        this.deliveryPerson = deliveryPerson;
    }
}
