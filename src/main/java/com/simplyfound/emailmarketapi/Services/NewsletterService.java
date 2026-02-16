package com.simplyfound.emailmarketapi.Services;

import com.simplyfound.emailmarketapi.Models.Newsletter;
import com.simplyfound.emailmarketapi.Models.Subscriber;
import com.simplyfound.emailmarketapi.Repositories.NewsletterRepository;
import com.simplyfound.emailmarketapi.Repositories.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsletterService {

    private final NewsletterRepository newsletterRepository;
    private final SubscriberRepository subscriberRepository;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Value("${app.base-url:http://localhost:8585/api/music/view}")
    private String baseUrl;

    @Transactional
    public Newsletter createNewsletter(String title, String subtitle, String content, 
                                       MultipartFile mediaFile) throws IOException {
        log.info("Creating newsletter with title: {}", title);

        Newsletter newsletter = new Newsletter();
        newsletter.setTitle(title);
        newsletter.setSubtitle(subtitle);
        newsletter.setContent(content);
        newsletter.setCreatedAt(LocalDateTime.now());

        if (mediaFile != null && !mediaFile.isEmpty()) {
            String mediaUrl = saveMediaFile(mediaFile);
            newsletter.setMediaUrl(mediaUrl);
            
            String contentType = mediaFile.getContentType();
            if (contentType != null) {
                if (contentType.startsWith("image/")) {
                    newsletter.setMediaType(Newsletter.MediaType.IMAGE);
                } else if (contentType.startsWith("video/")) {
                    newsletter.setMediaType(Newsletter.MediaType.VIDEO);
                }
            }
        }

        Newsletter saved = newsletterRepository.save(newsletter);
        log.info("Newsletter created with id: {}", saved.getId());
        return saved;
    }

    @Transactional
    public Newsletter updateNewsletter(Long id, String title, String subtitle, String content,
                                      MultipartFile mediaFile) throws IOException {
        log.info("Updating newsletter with id: {}", id);

        Newsletter newsletter = newsletterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Newsletter not found"));

        newsletter.setTitle(title);
        newsletter.setSubtitle(subtitle);
        newsletter.setContent(content);

        if (mediaFile != null && !mediaFile.isEmpty()) {
            // Delete old media file if exists
            if (newsletter.getMediaUrl() != null) {
                deleteMediaFile(newsletter.getMediaUrl());
            }
            
            String mediaUrl = saveMediaFile(mediaFile);
            newsletter.setMediaUrl(mediaUrl);
            
            String contentType = mediaFile.getContentType();
            if (contentType != null) {
                if (contentType.startsWith("image/")) {
                    newsletter.setMediaType(Newsletter.MediaType.IMAGE);
                } else if (contentType.startsWith("video/")) {
                    newsletter.setMediaType(Newsletter.MediaType.VIDEO);
                }
            }
        }

        Newsletter updated = newsletterRepository.save(newsletter);
        log.info("Newsletter updated with id: {}", updated.getId());
        return updated;
    }

    @Transactional
    public void markAsSent(Long newsletterId, int totalRecipients) {
        Newsletter newsletter = newsletterRepository.findById(newsletterId)
                .orElseThrow(() -> new RuntimeException("Newsletter not found"));
        
        newsletter.setSentAt(LocalDateTime.now());
        newsletter.setTotalRecipients(totalRecipients);
        newsletterRepository.save(newsletter);
        log.info("Newsletter {} marked as sent to {} recipients", newsletterId, totalRecipients);
    }

    @Transactional
    public void deleteNewsletter(Long id) {
        Newsletter newsletter = newsletterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Newsletter not found"));

        // Delete media file if exists
        if (newsletter.getMediaUrl() != null) {
            deleteMediaFile(newsletter.getMediaUrl());
        }

        newsletterRepository.deleteById(id);
        log.info("Newsletter deleted with id: {}", id);
    }

    public List<Newsletter> getAllNewsletters() {
        return newsletterRepository.findAllByOrderByCreatedAtDesc();
    }

    public Newsletter getNewsletterById(Long id) {
        return newsletterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Newsletter not found"));
    }

    public long countSentEmails() {
        return newsletterRepository.findAll().stream()
                .filter(n -> n.getSentAt() != null)
                .mapToLong(n -> n.getTotalRecipients() != null ? n.getTotalRecipients() : 0)
                .sum();
    }

    private String saveMediaFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : "";
        String filename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(filename);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Generate full URL like https://api.owellserver.ggff.net/api/music/view/{filename}
        String mediaUrl = baseUrl + "/" + filename;
        log.info("Media file saved: {} -> Full URL: {}", filename, mediaUrl);
        return mediaUrl;
    }

    private void deleteMediaFile(String mediaUrl) {
        try {
            // Extract filename from full URL
            String filename = mediaUrl.substring(mediaUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadDir, filename);
            Files.deleteIfExists(filePath);
            log.info("Media file deleted: {}", filename);
        } catch (IOException e) {
            log.error("Error deleting media file: {}", mediaUrl, e);
        }
    }
}

