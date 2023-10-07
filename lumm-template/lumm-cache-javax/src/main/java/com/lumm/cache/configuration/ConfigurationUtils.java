package com.lumm.cache.configuration;

import javax.cache.configuration.*;
import javax.cache.event.CacheEntryEventFilter;
import javax.cache.event.CacheEntryListener;

/**
 * 缓存配置工具类
 */
public abstract class ConfigurationUtils {

    /**
     * 创建{@link MutableConfiguration}实例
     *
     * @param configuration 配置
     * @param <K>           缓存键类型泛型
     * @param <V>           缓存值类型泛型
     * @return non-null
     * @see MutableConfiguration
     */
    public static <K, V> MutableConfiguration<K, V> mutableConfiguration(Configuration<K, V> configuration) {
        MutableConfiguration mutableConfiguration;
        if (configuration instanceof MutableConfiguration) {
            mutableConfiguration = (MutableConfiguration) configuration;
        } else if (configuration instanceof CompleteConfiguration) {
            CompleteConfiguration config = (CompleteConfiguration) configuration;
            mutableConfiguration = new MutableConfiguration<>(config);
        } else {
            mutableConfiguration = new MutableConfiguration<K, V>()
                    .setTypes(configuration.getKeyType(), configuration.getValueType())
                    .setStoreByValue(configuration.isStoreByValue());
        }
        return mutableConfiguration;
    }

    /**
     * 创建{@link MutableConfiguration}实例
     *
     * @param configuration 配置
     * @param <K>           缓存键类型泛型
     * @param <V>           缓存值类型泛型
     * @return non-null
     * @see ImmutableCompleteConfiguration
     */
    public static <K, V> CompleteConfiguration<K, V> immutableConfiguration(Configuration<K, V> configuration) {
        return new ImmutableCompleteConfiguration(configuration);
    }

    public static <K, V> CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration(CacheEntryListener<? super K, ? super V> listener) {
        return cacheEntryListenerConfiguration(listener, null);
    }

    public static <K, V> CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration(CacheEntryListener<? super K, ? super V> listener,
                                                                                               CacheEntryEventFilter<? super K, ? super V> filter) {
        return cacheEntryListenerConfiguration(listener, filter, true);
    }

    public static <K, V> CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration(CacheEntryListener<? super K, ? super V> listener,
                                                                                               CacheEntryEventFilter<? super K, ? super V> filter,
                                                                                               boolean isOldValueRequired) {
        return cacheEntryListenerConfiguration(listener, filter, isOldValueRequired, true);
    }

    public static <K, V> CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration(CacheEntryListener<? super K, ? super V> listener,
                                                                                               CacheEntryEventFilter<? super K, ? super V> filter,
                                                                                               boolean isOldValueRequired,
                                                                                               boolean isSynchronous) {
        return new MutableCacheEntryListenerConfiguration<>(() -> listener, () -> filter, isOldValueRequired, isSynchronous);
    }
}
