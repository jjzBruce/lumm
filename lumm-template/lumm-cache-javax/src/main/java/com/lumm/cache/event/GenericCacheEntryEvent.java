package com.lumm.cache.event;

import javax.cache.Cache;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.EventType;

import static java.util.Objects.requireNonNull;

/**
 * 一般的缓存事件
 *
 * @param <K> 缓存键类泛型
 * @param <V> 缓存值类泛型
 */
public class GenericCacheEntryEvent<K, V> extends CacheEntryEvent<K, V> {

    /**
     * 缓存键
     */
    private final K key;

    /**
     * 缓存旧值
     */
    private final V oldValue;

    /**
     * 缓存值
     */
    private final V value;

    /**
     * 构造
     *
     * @param source    缓存资源
     * @param eventType 缓存事件类型
     * @param key       缓存键
     * @param oldValue  缓存旧值
     * @param value     缓存值
     */
    public GenericCacheEntryEvent(Cache source, EventType eventType, K key, V oldValue, V value) {
        super(source, eventType);
        requireNonNull(key, "缓存键不能为null!");
        requireNonNull(value, "缓存值不能为null!");
        this.key = key;
        this.oldValue = oldValue;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getOldValue() {
        return oldValue;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public boolean isOldValueAvailable() {
        return oldValue != null;
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        return getSource().getCacheManager().unwrap(clazz);
    }

    @Override
    public String toString() {
        return "GenericCacheEntryEvent{" +
                "key=" + getKey() +
                ", oldValue=" + getOldValue() +
                ", value=" + getValue() +
                ", evenType=" + getEventType() +
                ", source=" + getSource().getName() +
                '}';
    }

    public static <K, V> CacheEntryEvent<K, V> createdEvent(Cache source, K key, V value) {
        return of(source, EventType.CREATED, key, null, value);
    }

    public static <K, V> CacheEntryEvent<K, V> updatedEvent(Cache source, K key, V oldValue, V value) {
        return of(source, EventType.UPDATED, key, oldValue, value);
    }

    public static <K, V> CacheEntryEvent<K, V> expiredEvent(Cache source, K key, V oldValue) {
        return of(source, EventType.EXPIRED, key, oldValue, oldValue);
    }

    public static <K, V> CacheEntryEvent<K, V> removedEvent(Cache source, K key, V oldValue) {
        return of(source, EventType.REMOVED, key, oldValue, oldValue);
    }

    public static <K, V> CacheEntryEvent<K, V> of(Cache source, EventType eventType, K key, V oldValue, V value) {
        return new GenericCacheEntryEvent<>(source, eventType, key, oldValue, value);
    }
}
