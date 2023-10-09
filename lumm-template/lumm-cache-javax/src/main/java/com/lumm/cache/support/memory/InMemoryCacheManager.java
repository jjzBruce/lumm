package com.lumm.cache.support.memory;

import com.lumm.cache.AbstractCacheManager;

import javax.cache.Cache;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.Properties;

/**
 * 缓存管理器实现 - 基于内存
 */
public class InMemoryCacheManager extends AbstractCacheManager {

    /**
     * 构造
     *
     * @param cachingProvider 缓存提供者
     * @param uri             uri
     * @param classLoader     类加载器
     * @param properties      配置
     */
    public InMemoryCacheManager(CachingProvider cachingProvider, URI uri, ClassLoader classLoader, Properties properties) {
        super(cachingProvider, uri, classLoader, properties);
    }

    @Override
    protected <K, V, C extends Configuration<K, V>> Cache doCreateCache(String cacheName, C configuration) {
        return new InMemoryCache<K, V>(this, cacheName, configuration);
    }
}
