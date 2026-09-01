package br.gov.ce.sps.projetoa.core.ratelimit;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryRateLimiter {

    private final ConcurrentHashMap<String, Deque<Instant>> buckets = new ConcurrentHashMap<>();

    public boolean tryConsume(String key, int maxAttempts, long windowSeconds) {
        Instant now = Instant.now();
        Instant windowStart = now.minusSeconds(windowSeconds);
        Deque<Instant> deque = buckets.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (deque) {
            purgeOlderThan(deque, windowStart);
            if (deque.size() >= maxAttempts) {
                return false;
            }
            deque.addLast(now);
            return true;
        }
    }

    public boolean isBlocked(String key, int maxAttempts, long windowSeconds) {
        Deque<Instant> deque = buckets.get(key);
        if (deque == null) {
            return false;
        }
        Instant windowStart = Instant.now().minusSeconds(windowSeconds);
        synchronized (deque) {
            purgeOlderThan(deque, windowStart);
            return deque.size() >= maxAttempts;
        }
    }

    public void recordFailure(String key, long windowSeconds) {
        Instant now = Instant.now();
        Instant windowStart = now.minusSeconds(windowSeconds);
        Deque<Instant> deque = buckets.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (deque) {
            purgeOlderThan(deque, windowStart);
            deque.addLast(now);
        }
    }

    public void reset(String key) {
        buckets.remove(key);
    }

    private static void purgeOlderThan(Deque<Instant> deque, Instant windowStart) {
        while (!deque.isEmpty() && deque.peekFirst().isBefore(windowStart)) {
            deque.pollFirst();
        }
    }
}
