package com.lumm.cache;

import lombok.Data;
import lombok.Getter;

import javax.cache.Cache;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * {@link Cache.Entry}的实现类，可以过期
 *
 * @param <K> 键类泛型
 * @param <V> 值类泛型
 * @see Cache.Entry
 */
@Data
public class ExpirableEntry<K, V> implements Cache.Entry<K, V>, Serializer {

    /**
     * 键
     */
    private final K key;

    /**
     * 值
     */
    private V value;

    /**
     * 过期时间戳
     */
    private long timestamp;

    public ExpirableEntry(K key, V value) {
        Objects.requireNonNull(key);
        this.key = key;
        this.value = value;
        this.timestamp = Long.MAX_VALUE;
    }

    @Override
    public byte[] serialize(Object source) throws IOException {
        return new byte[0];
    }

    public void setValue(V value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public boolean isExpired() {
        return getExpiredTime() < 1;
    }

    public boolean isEternal() {
        return Long.MAX_VALUE == getTimestamp();
    }

    public long getExpiredTime() {
        return getTimestamp() - System.currentTimeMillis();
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        T value;
        try {
            value = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    @Override
    public String toString() {
        return "ExpirableEntry{" +
                "key=" + key +
                ", value=" + value +
                ", timestamp=" + timestamp +
                '}';
    }

    public static <K, V> ExpirableEntry<K, V> of(Map.Entry<K, V> entry) {
        return new ExpirableEntry(entry.getKey(), entry.getValue());
    }

    public static <K, V> ExpirableEntry<K, V> of(K key, V value) {
        return new ExpirableEntry(key, value);
    }

    public static <K> void requireKeyNotNull(K key) {
        requireNonNull(key, "键不能为null。");
    }

    public static <V> void requireValueNotNull(V value) {
        requireNonNull(value, "值不能为null。");
    }

    public static <V> void requireOldValueNotNull(V oldValue) {
        requireNonNull(oldValue, "旧值不能为null。");
    }

}
