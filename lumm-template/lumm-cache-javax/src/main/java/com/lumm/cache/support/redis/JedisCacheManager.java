package com.lumm.cache.support.redis;

import com.lumm.cache.AbstractCacheManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.cache.Cache;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.Properties;

/**
 * 缓存管理器实现，基于Jedis
 */
public class JedisCacheManager extends AbstractCacheManager {

    private final JedisPool jedisPool;

    public JedisCacheManager(CachingProvider cachingProvider, URI uri, ClassLoader classLoader, Properties properties) {
        super(cachingProvider, uri, classLoader, properties);
        this.jedisPool = new JedisPool(uri);
    }

    @Override
    protected <K, V, C extends Configuration<K, V>> Cache doCreateCache(String cacheName, C configuration) {
        Jedis jedis = jedisPool.getResource();
        return new JedisCache(this, cacheName, configuration, jedis);
    }

    @Override
    protected void doClose() {
        jedisPool.close();
    }
}
