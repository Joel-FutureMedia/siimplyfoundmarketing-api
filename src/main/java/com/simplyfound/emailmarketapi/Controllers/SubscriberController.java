package com.simplyfound.emailmarketapi.Controllers;

import com.simplyfound.emailmarketapi.Models.Subscriber;
import com.simplyfound.emailmarketapi.Services.SubscriberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscribers")
@RequiredArgsConstructor
@Slf4j
public class SubscriberController {

    private final SubscriberService subscriberService;

    @PostMapping("/subscribe")
    public ResponseEntity<Map<String, Object>> subscribe(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            
            if (email == null || email.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Email is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            Subscriber subscriber = subscriberService.subscribe(email.trim());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Successfully subscribed");
            response.put("subscriber", subscriber);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error subscribing email", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Unexpected error subscribing email", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "An error occurred while processing your request");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/unsubscribe")
    public ResponseEntity<Map<String, Object>> unsubscribe(@RequestParam String email) {
        try {
            subscriberService.unsubscribe(email);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Successfully unsubscribed");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Subscriber>> getAllSubscribers() {
        List<Subscriber> subscribers = subscriberService.getAllSubscribers();
        return ResponseEntity.ok(subscribers);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getSubscriberCounts() {
        Map<String, Long> counts = new HashMap<>();
        counts.put("totalSubscribed", subscriberService.countSubscribers());
        counts.put("totalUnsubscribed", subscriberService.countUnsubscribed());
        return ResponseEntity.ok(counts);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSubscriber(@PathVariable Long id) {
        try {
            subscriberService.deleteSubscriber(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Subscriber deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}


