package com.simplyfound.emailmarketapi.Repositories;

import com.simplyfound.emailmarketapi.Models.EmailAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailAnalyticsRepository extends JpaRepository<EmailAnalytics, Long> {
    List<EmailAnalytics> findByNewsletterId(Long newsletterId);
    Optional<EmailAnalytics> findByNewsletterIdAndRecipientEmail(Long newsletterId, String email);
    long countByNewsletterId(Long newsletterId);
    long countByNewsletterIdAndOpenedTrue(Long newsletterId);
    
    @Query("SELECT COUNT(DISTINCT ea.newsletterId) FROM EmailAnalytics ea")
    long countDistinctNewsletters();
}


