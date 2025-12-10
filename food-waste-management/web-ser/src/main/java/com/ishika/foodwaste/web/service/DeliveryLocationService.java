package com.ishika.foodwaste.web.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ishika.foodwaste.jpa.entity.DeliveryLocation;
import com.ishika.foodwaste.jpa.entity.DeliveryPerson;
import com.ishika.foodwaste.jpa.entityManager.DeliveryLocationManager;
import com.ishika.foodwaste.jpa.entityManager.DeliveryPersonManager;
import com.ishika.foodwaste.jpa.utils.LocationDto;
@Service
public class DeliveryLocationService {

    @Autowired
    private DeliveryPersonManager deliveryPersonRepository;

    @Autowired
    private DeliveryLocationManager deliveryLocationRepository;

    // Helper method to get DeliveryPerson by ID
    public DeliveryPerson findDeliveryPersonById(Long deliveryId) {
        return deliveryPersonRepository.findById(deliveryId);
    }

    // Overloaded save method with DeliveryPerson passed in
    public void saveOrUpdateLocation(DeliveryPerson person, LocationDto location) {
        if(person == null) return;

        DeliveryLocation existingLocation = deliveryLocationRepository.findByDeliveryPerson(person);

        if (existingLocation == null) {
            existingLocation = new DeliveryLocation();
            existingLocation.setDeliveryPerson(person);
        }

        existingLocation.setLatitude(location.getLatitude());
        existingLocation.setLongitude(location.getLongitude());
        existingLocation.setTimestamp(LocalDateTime.now());

        deliveryLocationRepository.saveOrUpdate(existingLocation);
    }

    // Overloaded getLocation with DeliveryPerson passed in
    public LocationDto getLocation(DeliveryPerson person) {
        if(person == null) {
            return new LocationDto(0.0, 0.0);
        }

        DeliveryLocation location = deliveryLocationRepository.findByDeliveryPerson(person);

        if (location != null) {
            return new LocationDto(location.getLatitude(), location.getLongitude());
        }

        return new LocationDto(0.0, 0.0);
    }
}
