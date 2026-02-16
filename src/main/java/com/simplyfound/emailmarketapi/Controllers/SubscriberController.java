package com.simplyfound.emailmarketapi.Controllers;

import com.simplyfound.emailmarketapi.Models.Subscriber;
import com.simplyfound.emailmarketapi.Services.SubscriberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Email;
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
    public ResponseEntity<Map<String, Object>> subscribe(@RequestParam @Email String email) {
        try {
            Subscriber subscriber = subscriberService.subscribe(email);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Successfully subscribed");
            response.put("subscriber", subscriber);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
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

