package com.ishika.foodwaste.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ishika.foodwaste.jpa.entity.UserFeedback;
import com.ishika.foodwaste.web.service.FeedbackService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService facade;

    // POST /feedbacks/add
    @PostMapping("/add")
    public ResponseEntity<String> submitFeedback(@RequestBody UserFeedback feedback) {
        boolean result = facade.addFeedback(feedback);
        if (result) {
            return ResponseEntity.ok("Feedback submitted successfully!");
        } else {
            return ResponseEntity.status(500).body("Failed to submit feedback.");
        }
    }

    // GET /feedbacks/all
    @GetMapping("/all")
    public ResponseEntity<List<UserFeedback>> getAllFeedbacks() {
        return ResponseEntity.ok(facade.getAllFeedbacks());
    }

    // GET /feedbacks/count
    @GetMapping("/count")
    public ResponseEntity<Long> getFeedbackCount() {
        return ResponseEntity.ok(facade.getFeedbackCount());
    }
    
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<String> deleteFeedback(@PathVariable int id) {
        boolean result = facade.deleteFeedbackById(id);
        if (result) {
            return ResponseEntity.ok("Feedback deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Feedback not found.");
        }
    }
}
