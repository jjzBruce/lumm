package com.lumm.cache.annotation;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.annotation.CacheInvocationContext;
import javax.cache.annotation.CacheResolver;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import java.lang.annotation.Annotation;

import static java.util.Objects.requireNonNull;

/**
 * 缓存解析器接口{@link CacheResolver}的默认实现。 <br/>
 * 缓存解析器接口{@link CacheResolver}：用于在使用缓存注解时确定哪个缓存应该用于缓存操作。
 * 解耦缓存操作和具体的缓存管理实现，允许在运行时动态选择和配置缓存，以满足不同的应用需求。
 */
public class DefaultCacheResolver implements CacheResolver {

    private final CacheManager cacheManager;

    public DefaultCacheResolver(CacheManager cacheManager) throws NullPointerException {
        requireNonNull(cacheManager, "The 'cacheManager' argument must be not null!");
        this.cacheManager = cacheManager;
    }

    @Override
    public <K, V> Cache<K, V> resolveCache(CacheInvocationContext<? extends Annotation> cacheInvocationContext) {
        String cacheName = cacheInvocationContext.getCacheName();
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            Configuration<K, V> configuration = new MutableConfiguration<>();
            cache = cacheManager.createCache(cacheName, configuration);
        }
        return cache;
    }
}
