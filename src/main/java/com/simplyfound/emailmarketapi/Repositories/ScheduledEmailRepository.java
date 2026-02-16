package com.simplyfound.emailmarketapi.Repositories;

import com.simplyfound.emailmarketapi.Models.ScheduledEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduledEmailRepository extends JpaRepository<ScheduledEmail, Long> {
    List<ScheduledEmail> findBySentFalseAndScheduledAtLessThanEqual(LocalDateTime now);
    List<ScheduledEmail> findByNewsletterId(Long newsletterId);
    List<ScheduledEmail> findBySentFalseOrderByScheduledAtAsc();
}

