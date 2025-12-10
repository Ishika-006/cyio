package com.ishika.foodwaste.web.interfaces;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ishika.foodwaste.jpa.entity.DeliveryPerson;
import com.ishika.foodwaste.jpa.entity.FoodDonation;
@Component
public interface DeliveryFacade {
    String login(String email, String password);
    String register(DeliveryPerson person);
    List<FoodDonation> getUnassignedOrders(String city);
    List<FoodDonation> getMyOrders(int deliveryPersonId);
    String takeOrder(int orderId, int deliveryPersonId);
}
