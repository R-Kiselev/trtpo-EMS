package com.example.employeemanagementsystem.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@SuppressWarnings("squid:S6829")
@Component
public class InMemoryCache<K, V> {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryCache.class);

    private static class CacheEntry<V> {
        @Getter
        private final V value;

        public CacheEntry(V value) {
            this.value = value;
        }
    }

    private final Map<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final LinkedBlockingDeque<K> queue = new LinkedBlockingDeque<>();
    private final int capacity;

    public InMemoryCache() {
        this(128);
    }

    public InMemoryCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
    }

    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) {
            return null;
        }
        boolean removed = queue.remove(key);
        if (!removed) {
            logger.warn("Key {} not found in queue during get operation.", key);
        }
        boolean offered = queue.offer(key);
        if (!offered) {
            logger.error("Failed to offer key {} to the queue during get operation.", key);
        }

        return entry.getValue();
    }

    public void put(K key, V value) {
        CacheEntry<V> entry = new CacheEntry<>(value);

        if (cache.containsKey(key)) {

            cache.put(key, entry);
            boolean removed = queue.remove(key);
            if (!removed) {
                logger.warn("Key {} not found in queue during put operation "
                    + "when key already exists.", key);
            }
            boolean offered = queue.offer(key);
            if (!offered) {
                logger.error("Failed to offer key {} to the "
                    + "queue during put operation when key already exists.", key);
            }
            return;
        }

        if (cache.size() >= capacity) {

            K lruKey = queue.poll();
            if (lruKey != null) {
                cache.remove(lruKey);
            }
        }

        cache.put(key, entry);
        boolean offered = queue.offer(key);
        if (!offered) {
            logger.error("Failed to offer key {} to the queue during put operation.", key);
        }
    }

    public void evict(K key) {
        cache.remove(key);
        boolean removed = queue.remove(key);
        if (!removed) {
            logger.warn("Key {} not found in queue during evict operation.", key);
        }
    }

    public V remove(K key) {
        CacheEntry<V> entry = cache.remove(key);
        if (entry != null) {
            boolean removed = queue.remove(key);
            if (!removed) {
                logger.warn("Key {} not found in queue during remove operation.", key);
            }
            return entry.getValue();
        }
        return null;
    }

    public void clear() {
        cache.clear();
        queue.clear();
    }
}