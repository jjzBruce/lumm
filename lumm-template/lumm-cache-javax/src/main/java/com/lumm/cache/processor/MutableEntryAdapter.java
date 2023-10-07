package com.lumm.cache.processor;

import javax.cache.Cache;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.processor.MutableEntry;

/**
 * 可变缓存值适配器
 *
 */
public class MutableEntryAdapter<K, V> implements MutableEntry<K, V> {

    private final K key;

    private final Cache<K, V> cache;

    private MutableEntryAdapter(K key, Cache<K, V> cache) {
        this.key = key;
        this.cache = cache;
    }

    @Override
    public boolean exists() {
        return cache.containsKey(getKey());
    }

    @Override
    public void remove() {
        cache.remove(getKey());
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        CompleteConfiguration configuration = cache.getConfiguration(CompleteConfiguration.class);
        // 如果读穿透未打开，当缓存键未命中的时候会返回null
        return configuration.isReadThrough() ? null : cache.get(key);
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        return cache.unwrap(clazz);
    }

    @Override
    public void setValue(V value) {
        cache.put(key, value);
    }

    public static <K, V> MutableEntry<K, V> of(K key, Cache<K, V> cache) {
        return new MutableEntryAdapter<>(key, cache);
    }
}
