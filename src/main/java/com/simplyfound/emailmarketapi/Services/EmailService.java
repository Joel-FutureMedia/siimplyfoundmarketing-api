package com.simplyfound.emailmarketapi.Services;

import com.simplyfound.emailmarketapi.Models.EmailAnalytics;
import com.simplyfound.emailmarketapi.Models.Newsletter;
import com.simplyfound.emailmarketapi.Models.Subscriber;
import com.simplyfound.emailmarketapi.Repositories.EmailAnalyticsRepository;
import com.simplyfound.emailmarketapi.Repositories.NewsletterRepository;
import com.simplyfound.emailmarketapi.Repositories.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.internet.MimeMessage;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final SubscriberRepository subscriberRepository;
    private final NewsletterRepository newsletterRepository;
    private final EmailAnalyticsRepository analyticsRepository;
    private final NewsletterService newsletterService;

    @Value("${spring.mail.from}")
    private String fromEmail;

    @Value("${server.port:8585}")
    private String serverPort;

    @Async
    @Transactional
    public void sendNewsletter(Long newsletterId) {
        log.info("Starting to send newsletter with id: {}", newsletterId);

        Newsletter newsletter = newsletterRepository.findById(newsletterId)
                .orElseThrow(() -> new RuntimeException("Newsletter not found"));

        List<Subscriber> subscribers = subscriberRepository.findBySubscribedTrue();

        if (subscribers.isEmpty()) {
            log.warn("No active subscribers found. Cannot send newsletter.");
            throw new RuntimeException("No active subscribers found");
        }

        int sentCount = 0;
        int failedCount = 0;

        for (Subscriber subscriber : subscribers) {
            try {
                String htmlContent = buildEmailHtml(newsletter, subscriber.getEmail());
                sendEmail(subscriber.getEmail(), newsletter.getTitle(), htmlContent);
                
                // Create analytics record
                EmailAnalytics analytics = new EmailAnalytics();
                analytics.setNewsletterId(newsletterId);
                analytics.setRecipientEmail(subscriber.getEmail());
                analytics.setOpened(false);
                analyticsRepository.save(analytics);
                
                sentCount++;
                log.debug("Email sent to: {}", subscriber.getEmail());
            } catch (Exception e) {
                failedCount++;
                log.error("Failed to send email to: {}", subscriber.getEmail(), e);
            }
        }

        newsletterService.markAsSent(newsletterId, sentCount);
        log.info("Newsletter sending completed. Sent: {}, Failed: {}", sentCount, failedCount);
    }

    private void sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.debug("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Error sending email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String buildEmailHtml(Newsletter newsletter, String recipientEmail) {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"en\">");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">");
        html.append("<title>").append(escapeHtml(newsletter.getTitle())).append("</title>");
        html.append("<style>");
        html.append("* { margin: 0; padding: 0; box-sizing: border-box; }");
        html.append("body { margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; background-color: #f0f2f5; -webkit-font-smoothing: antialiased; -moz-osx-font-smoothing: grayscale; }");
        html.append(".email-wrapper { background-color: #f0f2f5; padding: 30px 10px; }");
        html.append(".email-container { max-width: 600px; margin: 0 auto; background-color: #ffffff; box-shadow: 0 4px 12px rgba(0,0,0,0.08); border-radius: 12px; overflow: hidden; }");
        
        // Header with #323e4a background
        html.append(".header { background-color: #323e4a; padding: 35px 30px; text-align: center; }");
        html.append(".logo-container { padding: 25px; display: inline-block; }");
        html.append(".logo { max-width: 200px; height: auto; display: block; margin: 0 auto; }");
        
        // Title Section with gradient accent
        html.append(".title-section { padding: 45px 35px 30px 35px; background: linear-gradient(to bottom, #ffffff 0%, #fafbff 100%); position: relative; }");
        html.append(".title-section::before { content: ''; position: absolute; top: 0; left: 0; right: 0; height: 4px; background: linear-gradient(90deg, #667eea 0%, #764ba2 100%); }");
        html.append(".title { font-size: 34px; font-weight: 700; color: #1a202c; margin-bottom: 12px; line-height: 1.2; letter-spacing: -0.8px; }");
        html.append(".subtitle { font-size: 20px; color: #4a5568; font-weight: 400; line-height: 1.5; margin-top: 8px; }");
        
        // Image Section
        html.append(".image-section { padding: 0; margin: 0; background-color: #ffffff; }");
        html.append(".image-container { width: 100%; margin: 0; padding: 0; display: block; background-color: #f7fafc; }");
        html.append(".image-container img { width: 100%; height: auto; display: block; margin: 0; border: none; }");
        
        // Content Section
        html.append(".content-section { padding: 35px 35px 45px 35px; background-color: #ffffff; }");
        html.append(".text-content { font-size: 16px; color: #2d3748; line-height: 1.8; margin: 0; }");
        html.append(".text-content p { margin-bottom: 18px; }");
        html.append(".text-content h1, .text-content h2, .text-content h3 { color: #1a202c; margin-top: 28px; margin-bottom: 14px; font-weight: 600; }");
        html.append(".text-content h1 { font-size: 24px; }");
        html.append(".text-content h2 { font-size: 22px; }");
        html.append(".text-content h3 { font-size: 20px; }");
        html.append(".text-content ul, .text-content ol { margin: 18px 0; padding-left: 28px; }");
        html.append(".text-content li { margin-bottom: 10px; }");
        html.append(".text-content a { color: #667eea; text-decoration: none; font-weight: 500; border-bottom: 1px solid rgba(102, 126, 234, 0.3); }");
        html.append(".text-content a:hover { color: #764ba2; border-bottom-color: #764ba2; }");
        html.append(".text-content blockquote { border-left: 4px solid #667eea; padding-left: 20px; margin: 20px 0; color: #4a5568; font-style: italic; }");
        html.append(".text-content code { background-color: #f7fafc; padding: 2px 6px; border-radius: 4px; font-family: 'Courier New', monospace; font-size: 14px; }");
        
        // Footer with #323e4a background
        html.append(".footer { background-color: #323e4a; padding: 35px 25px; text-align: center; }");
        html.append(".company-info { font-size: 15px; color: #e2e8f0; margin-bottom: 24px; line-height: 1.7; }");
        html.append(".company-info p { margin: 6px 0; }");
        html.append(".company-info strong { color: #ffffff; font-weight: 600; font-size: 16px; }");
        html.append(".company-info a { color: #93c5fd; text-decoration: none; }");
        html.append(".company-info a:hover { color: #bfdbfe; text-decoration: underline; }");
        html.append(".unsubscribe-btn { display: inline-block; padding: 14px 32px; background: linear-gradient(135deg, #dc3545 0%, #c82333 100%); color: #ffffff !important; text-decoration: none; border-radius: 8px; font-size: 14px; font-weight: 600; box-shadow: 0 4px 6px rgba(220, 53, 69, 0.25); transition: all 0.3s ease; }");
        html.append(".unsubscribe-btn:hover { background: linear-gradient(135deg, #c82333 0%, #bd2130 100%); box-shadow: 0 6px 12px rgba(220, 53, 69, 0.35); transform: translateY(-1px); }");
        
        // Divider
        html.append(".divider { height: 1px; background: linear-gradient(90deg, transparent, #e2e8f0, transparent); margin: 30px 0; }");
        
        // Responsive
        html.append("@media only screen and (max-width: 600px) {");
        html.append(".email-wrapper { padding: 15px 5px; }");
        html.append(".email-container { width: 100% !important; border-radius: 0; }");
        html.append(".header { padding: 25px 20px; }");
        html.append(".logo-container { padding: 20px; }");
        html.append(".logo { max-width: 160px; }");
        html.append(".title-section { padding: 35px 25px 25px 25px; }");
        html.append(".content-section { padding: 30px 25px 35px 25px; }");
        html.append(".title { font-size: 28px; }");
        html.append(".subtitle { font-size: 18px; }");
        html.append(".text-content { font-size: 15px; }");
        html.append(".footer { padding: 30px 20px; }");
        html.append(".unsubscribe-btn { padding: 12px 24px; font-size: 13px; }");
        html.append("}");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class=\"email-wrapper\">");
        html.append("<div class=\"email-container\">");
        
        // Header with Logo (white background for visibility)
        html.append("<div class=\"header\">");
        html.append("<div class=\"logo-container\">");
        html.append("<img src=\"https://www.simplyfound.com.na/assets/logo-CtF7uxpB.png\" alt=\"Simply Found Logo\" class=\"logo\" />");
        html.append("</div>");
        html.append("</div>");
        
        // Title Section
        html.append("<div class=\"title-section\">");
        html.append("<h1 class=\"title\">").append(escapeHtml(newsletter.getTitle())).append("</h1>");
        html.append("<h2 class=\"subtitle\">").append(escapeHtml(newsletter.getSubtitle())).append("</h2>");
        html.append("</div>");
        
        // Image Section (after title and subtitle)
        if (newsletter.getMediaUrl() != null && !newsletter.getMediaUrl().isEmpty() && 
            newsletter.getMediaType() == Newsletter.MediaType.IMAGE) {
            html.append("<div class=\"image-section\">");
            html.append("<div class=\"image-container\">");
            // Use the stored URL from database (already full URL)
            html.append("<img src=\"").append(newsletter.getMediaUrl()).append("\" alt=\"").append(escapeHtml(newsletter.getTitle())).append("\" style=\"width: 100%; height: auto; display: block;\" />");
            html.append("</div>");
            html.append("</div>");
        }
        
        // Content Section (after image)
        html.append("<div class=\"content-section\">");
        if (newsletter.getContent() != null && !newsletter.getContent().isEmpty()) {
            html.append("<div class=\"text-content\">").append(newsletter.getContent()).append("</div>");
        }
        
        // Video (if video type, show after content)
        if (newsletter.getMediaUrl() != null && !newsletter.getMediaUrl().isEmpty() && 
            newsletter.getMediaType() == Newsletter.MediaType.VIDEO) {
            html.append("<div style=\"margin-top: 30px; text-align: center; background-color: #f7fafc; padding: 20px; border-radius: 12px;\">");
            html.append("<video controls style=\"max-width: 100%; height: auto; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);\">");
            html.append("<source src=\"").append(newsletter.getMediaUrl()).append("\" type=\"video/mp4\">");
            html.append("Your browser does not support the video tag.");
            html.append("</video>");
            html.append("</div>");
        }
        html.append("</div>");
        
        // Divider
        html.append("<div class=\"divider\"></div>");
        
        // Footer
        html.append("<div class=\"footer\">");
        html.append("<div class=\"company-info\">");
        html.append("<p><strong>Simply Found</strong></p>");
        html.append("<p>Email: <a href=\"mailto:info@simplyfound.com.na\">info@simplyfound.com.na</a></p>");
        html.append("<p style=\"margin-top: 12px; font-size: 13px; color: #cbd5e0;\">Thank you for being part of our community!</p>");
        html.append("</div>");
        
        String unsubscribeUrl = "http://localhost:" + serverPort + "/api/subscribers/unsubscribe?email=" + 
                                java.net.URLEncoder.encode(recipientEmail, java.nio.charset.StandardCharsets.UTF_8);
        html.append("<a href=\"").append(unsubscribeUrl).append("\" class=\"unsubscribe-btn\">Unsubscribe</a>");
        html.append("</div>");
        
        html.append("</div>");
        html.append("</div>");
        
        // Tracking Pixel
        String trackingUrl = "http://localhost:" + serverPort + "/api/track/" + newsletter.getId() + "/" + 
                            java.net.URLEncoder.encode(recipientEmail, java.nio.charset.StandardCharsets.UTF_8);
        html.append("<img src=\"").append(trackingUrl).append("\" width=\"1\" height=\"1\" style=\"display:none;\" />");
        
        html.append("</body>");
        html.append("</html>");
        
        return html.toString();
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }

    @Transactional
    public void trackEmailOpen(Long newsletterId, String email) {
        EmailAnalytics analytics = analyticsRepository
                .findByNewsletterIdAndRecipientEmail(newsletterId, email)
                .orElse(null);

        if (analytics != null && !analytics.getOpened()) {
            analytics.setOpened(true);
            analytics.setOpenedAt(java.time.LocalDateTime.now());
            analyticsRepository.save(analytics);
            log.info("Email opened tracked for newsletter {} and email {}", newsletterId, email);
        }
    }
}

