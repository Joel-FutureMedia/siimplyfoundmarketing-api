package com.simplyfound.emailmarketapi.Services;

import com.simplyfound.emailmarketapi.Models.Subscriber;
import com.simplyfound.emailmarketapi.Repositories.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;

    @Transactional
    public Subscriber subscribe(String email) {
        log.info("Attempting to subscribe email: {}", email);
        
        Subscriber existingSubscriber = subscriberRepository.findByEmail(email)
                .orElse(null);

        if (existingSubscriber != null) {
            if (existingSubscriber.getSubscribed()) {
                log.warn("Email {} is already subscribed", email);
                throw new RuntimeException("Email is already subscribed");
            } else {
                // Resubscribe
                existingSubscriber.setSubscribed(true);
                existingSubscriber.setSubscribedAt(LocalDateTime.now());
                existingSubscriber.setUnsubscribedAt(null);
                log.info("Resubscribed email: {}", email);
                return subscriberRepository.save(existingSubscriber);
            }
        }

        Subscriber subscriber = new Subscriber();
        subscriber.setEmail(email);
        subscriber.setSubscribed(true);
        subscriber.setSubscribedAt(LocalDateTime.now());
        
        log.info("Successfully subscribed email: {}", email);
        return subscriberRepository.save(subscriber);
    }

    @Transactional
    public void unsubscribe(String email) {
        log.info("Attempting to unsubscribe email: {}", email);
        
        Subscriber subscriber = subscriberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Subscriber not found"));

        subscriber.setSubscribed(false);
        subscriber.setUnsubscribedAt(LocalDateTime.now());
        
        subscriberRepository.save(subscriber);
        log.info("Successfully unsubscribed email: {}", email);
    }

    public List<Subscriber> getAllSubscribers() {
        return subscriberRepository.findAll();
    }

    public List<Subscriber> getActiveSubscribers() {
        return subscriberRepository.findBySubscribedTrue();
    }

    public long countSubscribers() {
        return subscriberRepository.countBySubscribedTrue();
    }

    public long countUnsubscribed() {
        return subscriberRepository.countBySubscribedFalse();
    }

    @Transactional
    public void deleteSubscriber(Long id) {
        subscriberRepository.deleteById(id);
        log.info("Deleted subscriber with id: {}", id);
    }
}

