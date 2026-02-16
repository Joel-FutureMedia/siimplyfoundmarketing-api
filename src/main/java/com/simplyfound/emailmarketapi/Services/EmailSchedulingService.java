package com.simplyfound.emailmarketapi.Services;

import com.simplyfound.emailmarketapi.Models.ScheduledEmail;
import com.simplyfound.emailmarketapi.Repositories.ScheduledEmailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSchedulingService {

    private final ScheduledEmailRepository scheduledEmailRepository;
    private final EmailService emailService;

    @Transactional
    public ScheduledEmail scheduleEmail(Long newsletterId, LocalDateTime scheduledAt) {
        log.info("Scheduling email for newsletter {} at {}", newsletterId, scheduledAt);
        
        ScheduledEmail scheduledEmail = new ScheduledEmail();
        scheduledEmail.setNewsletterId(newsletterId);
        scheduledEmail.setScheduledAt(scheduledAt);
        scheduledEmail.setSent(false);
        
        ScheduledEmail saved = scheduledEmailRepository.save(scheduledEmail);
        log.info("Email scheduled successfully with id: {}", saved.getId());
        
        return saved;
    }

    @Scheduled(fixedRate = 60000) // Check every minute
    @Transactional
    public void processScheduledEmails() {
        LocalDateTime now = LocalDateTime.now();
        List<ScheduledEmail> scheduledEmails = scheduledEmailRepository
                .findBySentFalseAndScheduledAtLessThanEqual(now);

        if (scheduledEmails.isEmpty()) {
            return;
        }

        log.info("Processing {} scheduled emails", scheduledEmails.size());

        for (ScheduledEmail scheduledEmail : scheduledEmails) {
            try {
                emailService.sendNewsletter(scheduledEmail.getNewsletterId());
                scheduledEmail.setSent(true);
                scheduledEmail.setSentAt(LocalDateTime.now());
                scheduledEmailRepository.save(scheduledEmail);
                log.info("Scheduled email sent successfully for newsletter {}", scheduledEmail.getNewsletterId());
            } catch (Exception e) {
                log.error("Error sending scheduled email for newsletter {}", scheduledEmail.getNewsletterId(), e);
            }
        }
    }

    public List<ScheduledEmail> getScheduledEmails() {
        return scheduledEmailRepository.findBySentFalseOrderByScheduledAtAsc();
    }

    public List<ScheduledEmail> getScheduledEmailsByNewsletter(Long newsletterId) {
        return scheduledEmailRepository.findByNewsletterId(newsletterId);
    }

    @Transactional
    public void cancelScheduledEmail(Long id) {
        ScheduledEmail scheduledEmail = scheduledEmailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scheduled email not found"));
        
        if (scheduledEmail.getSent()) {
            throw new RuntimeException("Cannot cancel already sent email");
        }
        
        scheduledEmailRepository.deleteById(id);
        log.info("Scheduled email cancelled: {}", id);
    }
}


