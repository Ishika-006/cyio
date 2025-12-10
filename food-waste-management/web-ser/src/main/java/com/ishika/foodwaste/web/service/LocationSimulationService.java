package com.ishika.foodwaste.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ishika.foodwaste.jpa.entity.DeliveryLocation;
import com.ishika.foodwaste.jpa.entity.DeliveryPerson;
import com.ishika.foodwaste.jpa.entityManager.DeliveryLocationManager;
import com.ishika.foodwaste.jpa.utils.LocationDto;

@Service
public class LocationSimulationService {

    @Autowired
    private DeliveryLocationManager deliveryLocationRepo;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Simulate movement every 5 seconds
    @Scheduled(fixedRate = 5000)
    public void simulateMovement() {
        // Step 1: Get the most recent DeliveryLocation record (latest timestamp)
        DeliveryLocation latestLocation = deliveryLocationRepo.findLatestLocation();

        if (latestLocation == null) {
            System.out.println("No delivery locations found to simulate.");
            return;
        }

        // Step 2: Get delivery person object and its ID from the latest record
        DeliveryPerson deliveryPerson = latestLocation.getDeliveryPerson();
        if (deliveryPerson == null) {
            System.out.println("Latest location has no delivery person assigned.");
            return;
        }
        int deliveryPersonId = deliveryPerson.getDid();

        // Step 3: Find current location of this delivery person (based on DeliveryPerson entity)
        DeliveryLocation loc = deliveryLocationRepo.findByDeliveryPersonId(deliveryPersonId);
        if (loc == null) {
            System.out.println("No location found for delivery person ID " + deliveryPersonId);
            return;
        }

        // Step 4: Slightly change lat/lng to simulate movement
        loc.setLatitude(loc.getLatitude() + 0.0001);
        loc.setLongitude(loc.getLongitude() + 0.0001);

        // Step 5: Save updated location
        deliveryLocationRepo.saveOrUpdate(loc);

        // Step 6: Send new location via WebSocket
        LocationDto dto = new LocationDto(loc.getLatitude(), loc.getLongitude());

        messagingTemplate.convertAndSend("/topic/delivery/" + deliveryPersonId + "/location", dto);

        System.out.println("📡 Sent simulated location for deliveryPersonId " + deliveryPersonId + ": " + dto.getLatitude() + ", " + dto.getLongitude());
    }

}


