package org.asbeza.frontend.utils;

import java.util.HashMap;
import java.util.Map;

public class CacheManager {
    private static CacheManager instance;
    private final Map<String, CacheItem> cache;
    private final long expiryDurationMs = 5 * 60 * 1000;

    private CacheManager() {
        cache = new HashMap<>();
    }

    public static synchronized CacheManager getInstance() {
        if (instance == null) {
            instance = new CacheManager();
        }
        return instance;
    }

    public void put(String key, Object value) {
        cache.put(key, new CacheItem(value, System.currentTimeMillis()));
    }

    public Object get(String key) {
        CacheItem item = cache.get(key);
        return (item != null) ? item.data : null;
    }

    public boolean containsKey(String key) {
        return cache.containsKey(key);
    }

    public void remove(String key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }

    public boolean isExpired(String key) {
        CacheItem item = cache.get(key);
        return item == null || (System.currentTimeMillis() - item.timestamp > expiryDurationMs);
    }

    private record CacheItem(Object data, long timestamp) {
    }
}
