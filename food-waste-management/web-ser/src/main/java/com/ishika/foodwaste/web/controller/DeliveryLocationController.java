package com.ishika.foodwaste.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import com.ishika.foodwaste.jpa.entity.DeliveryPerson;
import com.ishika.foodwaste.jpa.utils.LocationDto;
import com.ishika.foodwaste.web.service.DeliveryLocationService;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/deliveries")
public class DeliveryLocationController {
	
    private final DeliveryLocationService dls;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public DeliveryLocationController(DeliveryLocationService dls, SimpMessagingTemplate messagingTemplate) {
        this.dls = dls;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/{deliveryId}/location")
    public ResponseEntity<Void> updateLocation(@PathVariable Long deliveryId, @RequestBody LocationDto location) {
        DeliveryPerson person = dls.findDeliveryPersonById(deliveryId);
        if (person == null) {
            return ResponseEntity.notFound().build();  // 404 if not found
        }

        dls.saveOrUpdateLocation(person, location);
        messagingTemplate.convertAndSend("/topic/delivery/" + deliveryId + "/location", location);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{deliveryId}/location")
    public ResponseEntity<LocationDto> getDeliveryPersonLocation(@PathVariable Long deliveryId) {
        DeliveryPerson person = dls.findDeliveryPersonById(deliveryId);
        if (person == null) {
            return ResponseEntity.notFound().build(); // 404 if not found
        }

        LocationDto location = dls.getLocation(person);
        return ResponseEntity.ok(location);
    }
    @GetMapping("/donor/location")
    public LocationDto getDonorLocation() {
        // Replace with actual donor location fetching logic
        return new LocationDto(28.6139, 77.209); // Delhi example
    }

    @GetMapping("/ngo/location")
    public LocationDto getNGOLocation() {
        // Replace with actual NGO location fetching logic
        return new LocationDto(28.5355, 77.3910); // Noida example
    }

}

