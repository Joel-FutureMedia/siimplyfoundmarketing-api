package com.simplyfound.emailmarketapi.Controllers;

import com.simplyfound.emailmarketapi.Models.Newsletter;
import com.simplyfound.emailmarketapi.Services.EmailService;
import com.simplyfound.emailmarketapi.Services.NewsletterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/newsletters")
@RequiredArgsConstructor
@Slf4j
public class NewsletterController {

    private final NewsletterService newsletterService;
    private final EmailService emailService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createNewsletter(
            @RequestParam String title,
            @RequestParam String subtitle,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) MultipartFile mediaFile) {
        try {
            Newsletter newsletter = newsletterService.createNewsletter(title, subtitle, content, mediaFile);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Newsletter created successfully");
            response.put("newsletter", newsletter);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating newsletter", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/send/{id}")
    public ResponseEntity<Map<String, Object>> sendNewsletter(@PathVariable Long id) {
        try {
            emailService.sendNewsletter(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Newsletter sending started");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error sending newsletter", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateNewsletter(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String subtitle,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) MultipartFile mediaFile) {
        try {
            Newsletter newsletter = newsletterService.updateNewsletter(id, title, subtitle, content, mediaFile);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Newsletter updated successfully");
            response.put("newsletter", newsletter);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating newsletter", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteNewsletter(@PathVariable Long id) {
        try {
            newsletterService.deleteNewsletter(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Newsletter deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting newsletter", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Newsletter>> getAllNewsletters() {
        List<Newsletter> newsletters = newsletterService.getAllNewsletters();
        return ResponseEntity.ok(newsletters);
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalEmailsSent", newsletterService.countSentEmails());
        return ResponseEntity.ok(analytics);
    }
}


