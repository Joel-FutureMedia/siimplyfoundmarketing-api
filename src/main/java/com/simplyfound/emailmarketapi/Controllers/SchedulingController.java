package com.simplyfound.emailmarketapi.Controllers;

import com.simplyfound.emailmarketapi.Models.ScheduledEmail;
import com.simplyfound.emailmarketapi.Services.EmailSchedulingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scheduling")
@RequiredArgsConstructor
@Slf4j
public class SchedulingController {

    private final EmailSchedulingService schedulingService;

    @PostMapping("/schedule")
    public ResponseEntity<Map<String, Object>> scheduleEmail(
            @RequestParam Long newsletterId,
            @RequestParam String scheduledAt) {
        try {
            LocalDateTime scheduledDateTime = LocalDateTime.parse(scheduledAt);
            
            if (scheduledDateTime.isBefore(LocalDateTime.now())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Scheduled time must be in the future");
                return ResponseEntity.badRequest().body(response);
            }

            ScheduledEmail scheduled = schedulingService.scheduleEmail(newsletterId, scheduledDateTime);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Email scheduled successfully");
            response.put("scheduledEmail", scheduled);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error scheduling email", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<ScheduledEmail>> getAllScheduledEmails() {
        List<ScheduledEmail> scheduledEmails = schedulingService.getScheduledEmails();
        return ResponseEntity.ok(scheduledEmails);
    }

    @GetMapping("/newsletter/{newsletterId}")
    public ResponseEntity<List<ScheduledEmail>> getScheduledEmailsByNewsletter(@PathVariable Long newsletterId) {
        List<ScheduledEmail> scheduledEmails = schedulingService.getScheduledEmailsByNewsletter(newsletterId);
        return ResponseEntity.ok(scheduledEmails);
    }

    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<Map<String, Object>> cancelScheduledEmail(@PathVariable Long id) {
        try {
            schedulingService.cancelScheduledEmail(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Scheduled email cancelled successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}


