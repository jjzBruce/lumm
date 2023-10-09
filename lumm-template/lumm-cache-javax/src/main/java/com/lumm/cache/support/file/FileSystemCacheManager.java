package com.lumm.cache.support.file;


import com.lumm.cache.AbstractCacheManager;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.Properties;

/**
 * 缓存管理器实现，基于文件系统
 */
public class FileSystemCacheManager extends AbstractCacheManager {

    public FileSystemCacheManager(CachingProvider cachingProvider, URI uri, ClassLoader classLoader,
                                  Properties properties) {
        super(cachingProvider, uri, classLoader, properties);
    }

    @Override
    protected <K, V, C extends Configuration<K, V>> Cache doCreateCache(String cacheName, C configuration) {
        return new FileSystemCache(this, cacheName, configuration);
    }
}
