package com.lumm.cache.event;

import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.event.CacheEntryEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * 缓存事件发布器
 */
public class CacheEntryEventPublisher {

    private Set<ConditionalCacheEntryEventListener> listeners = new HashSet<>();

    public void registerCacheEntryListener(CacheEntryListenerConfiguration configuration) {
        CacheEntryEventListenerAdapter listenerAdapter = new CacheEntryEventListenerAdapter(configuration);
        listeners.add(listenerAdapter);
    }

    public void deregisterCacheEntryListener(CacheEntryListenerConfiguration configuration) {
        CacheEntryEventListenerAdapter listenerAdapter = new CacheEntryEventListenerAdapter(configuration);
        listeners.remove(listenerAdapter);
    }

    public <K, V> void publish(CacheEntryEvent<? extends K, ? extends V> event) {
        listeners.forEach(listener -> listener.onEvent(event));
    }


}
