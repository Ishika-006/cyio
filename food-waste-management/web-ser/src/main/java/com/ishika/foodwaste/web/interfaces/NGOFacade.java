package com.ishika.foodwaste.web.interfaces;

import org.springframework.stereotype.Component;

import com.ishika.foodwaste.jpa.entity.Donor;
import com.ishika.foodwaste.jpa.entity.NGOS;

@Component
public interface NGOFacade {
    NGOS authenticate(String email);
    boolean registerNGO(NGOS ngo);

}
