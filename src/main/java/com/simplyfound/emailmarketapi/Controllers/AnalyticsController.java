package com.simplyfound.emailmarketapi.Controllers;

import com.simplyfound.emailmarketapi.Models.EmailAnalytics;
import com.simplyfound.emailmarketapi.Repositories.EmailAnalyticsRepository;
import com.simplyfound.emailmarketapi.Services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {

    private final EmailService emailService;
    private final EmailAnalyticsRepository analyticsRepository;

    @GetMapping("/track/{newsletterId}/{email}")
    public ResponseEntity<byte[]> trackEmailOpen(
            @PathVariable Long newsletterId,
            @PathVariable String email) {
        try {
            emailService.trackEmailOpen(newsletterId, email);
            
            // Return 1x1 transparent PNG
            byte[] transparentPixel = new byte[]{
                    (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
                    0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
                    0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
                    0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, (byte) 0xC4,
                    (byte) 0x89, 0x00, 0x00, 0x00, 0x0A, 0x49, 0x44, 0x41,
                    0x54, 0x78, (byte) 0x9C, 0x63, 0x00, 0x01, 0x00, 0x00,
                    0x05, 0x00, 0x01, 0x0D, 0x0A, 0x2D, (byte) 0xB4, 0x00,
                    0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, (byte) 0xAE,
                    0x42, 0x60, (byte) 0x82
            };
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(transparentPixel);
        } catch (Exception e) {
            log.error("Error tracking email open", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/analytics/{newsletterId}")
    public ResponseEntity<Map<String, Object>> getNewsletterAnalytics(@PathVariable Long newsletterId) {
        try {
            List<EmailAnalytics> analytics = analyticsRepository.findByNewsletterId(newsletterId);
            long totalSent = analytics.size();
            long totalOpened = analytics.stream().filter(EmailAnalytics::getOpened).count();
            double openRate = totalSent > 0 ? (double) totalOpened / totalSent * 100 : 0;

            Map<String, Object> response = new HashMap<>();
            response.put("newsletterId", newsletterId);
            response.put("totalSent", totalSent);
            response.put("totalOpened", totalOpened);
            response.put("openRate", Math.round(openRate * 100.0) / 100.0);
            response.put("analytics", analytics);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting analytics", e);
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/analytics/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardAnalytics() {
        try {
            Map<String, Object> dashboard = new HashMap<>();
            
            // Get all analytics
            List<EmailAnalytics> allAnalytics = analyticsRepository.findAll();
            long totalEmailsSent = allAnalytics.size();
            long totalEmailsOpened = allAnalytics.stream().filter(EmailAnalytics::getOpened).count();
            double overallOpenRate = totalEmailsSent > 0 ? (double) totalEmailsOpened / totalEmailsSent * 100 : 0;

            dashboard.put("totalEmailsSent", totalEmailsSent);
            dashboard.put("totalEmailsOpened", totalEmailsOpened);
            dashboard.put("overallOpenRate", Math.round(overallOpenRate * 100.0) / 100.0);

            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            log.error("Error getting dashboard analytics", e);
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}


