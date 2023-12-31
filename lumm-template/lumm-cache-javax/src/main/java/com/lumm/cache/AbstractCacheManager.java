package com.lumm.cache;


import com.lumm.cache.serialization.Deserializers;
import com.lumm.cache.serialization.Serializers;
import lombok.extern.slf4j.Slf4j;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static java.lang.String.format;

/**
 * 抽象的缓存管理器
 */
@Slf4j
public abstract class AbstractCacheManager implements CacheManager {

    /**
     * 缓存清空方法
     */
    private static final Consumer<Cache> CLEAR_CACHE_OPERATION = Cache::clear;

    /**
     * 缓存关闭方法
     */
    private static final Consumer<Cache> CLOSE_CACHE_OPERATION = Cache::close;

    /**
     * 缓存提供者
     */
    private final CachingProvider cachingProvider;

    /**
     * uri
     */
    private final URI uri;

    /**
     * 类加载器
     */
    private final ClassLoader classLoader;

    /**
     * 序列化
     */
    private final Serializers serializers;

    /**
     * 反序列化
     */
    private final Deserializers deserializers;

    /**
     * 配置
     */
    private final Properties properties;

    private volatile boolean closed;

    /**
     * 缓存注册中心，基于Map
     */
    private ConcurrentHashMap<String, Map<KeyValueTypePair, Cache>> cacheRepository = new ConcurrentHashMap<>();

    /**
     * 构造
     *
     * @param cachingProvider 缓存提供者
     * @param uri             uri
     * @param classLoader     类加载器
     * @param properties      配置
     */
    public AbstractCacheManager(CachingProvider cachingProvider, URI uri, ClassLoader classLoader, Properties properties) {
        this.cachingProvider = cachingProvider;
        this.uri = uri == null ? cachingProvider.getDefaultURI() : uri;
        this.classLoader = classLoader == null ? cachingProvider.getDefaultClassLoader() : classLoader;
        this.properties = properties == null ? cachingProvider.getDefaultProperties() : properties;
        this.serializers = initSerializers(this.classLoader);
        this.deserializers = initDeserializers(this.classLoader);
    }

    /**
     * 初始化反序列化
     *
     * @param classLoader 类加载器
     * @return Deserializers
     */
    private Deserializers initDeserializers(ClassLoader classLoader) {
        Deserializers deserializers = new Deserializers(classLoader);
        deserializers.loadSPI();
        return deserializers;
    }

    /**
     * 初始化序列化
     *
     * @param classLoader 类加载器
     * @return Serializers
     */
    private Serializers initSerializers(ClassLoader classLoader) {
        Serializers serializers = new Serializers(classLoader);
        serializers.loadSPI();
        return serializers;
    }

    @Override
    public CachingProvider getCachingProvider() {
        return cachingProvider;
    }

    @Override
    public URI getURI() {
        return uri;
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    public Serializers getSerializers() {
        return serializers;
    }

    public Deserializers getDeserializers() {
        return deserializers;
    }

    @Override
    public <K, V, C extends Configuration<K, V>> Cache<K, V> createCache(String cacheName, C configuration) throws IllegalArgumentException {
        // 如果指定的Cache在cacheRepository已经存在，就抛出异常
        if (!cacheRepository.getOrDefault(cacheName, Collections.emptyMap()).isEmpty()) {
            throw new CacheException(format("缓存名[%s]已存在，请尝试其他的缓存名称", cacheName));
        }
        // If a Cache with the specified name is unknown the CacheManager, one is created according to
        // the provided Configuration after which it becomes managed by the CacheManager.
        return getOrCreateCache(cacheName, configuration, true);
    }

    /**
     * 子类实现根据配置{@link Configuration configuration}创建一个{@link Cache cache}
     */
    protected abstract <K, V, C extends Configuration<K, V>> Cache doCreateCache(String cacheName, C configuration);

    @Override
    public <K, V> Cache<K, V> getCache(String cacheName, Class<K> keyType, Class<V> valueType) {
        MutableConfiguration<K, V> configuration = new MutableConfiguration<K, V>().setTypes(keyType, valueType);
        return getOrCreateCache(cacheName, configuration, false);
    }

    @Override
    public <K, V> Cache<K, V> getCache(String cacheName) {
        return getCache(cacheName, (Class<K>) Object.class, (Class<V>) Object.class);
    }

    @Override
    public Iterable<String> getCacheNames() {
        assertNotClosed();
        return cacheRepository.keySet();
    }

    @Override
    public void destroyCache(String cacheName) {
        Objects.requireNonNull(cacheName);
        assertNotClosed();
        Map<KeyValueTypePair, Cache> remove = cacheRepository.remove(cacheName);
        if (remove != null) {
            iteratorCaches(remove.values(), CLEAR_CACHE_OPERATION, CLOSE_CACHE_OPERATION);
        }
    }

    @Override
    public void enableManagement(String cacheName, boolean enabled) {
        assertNotClosed();
        throw new UnsupportedOperationException("to be supported.");
    }

    @Override
    public void enableStatistics(String cacheName, boolean enabled) {
        assertNotClosed();
        throw new UnsupportedOperationException("to be supported.");
    }

    @Override
    public final void close() {
        if (isClosed()) {
            log.warn("The CacheManager has been closed, current close operation will be ignored!");
            return;
        }
        for (Map<KeyValueTypePair, Cache> valueMap : cacheRepository.values()) {
            iteratorCaches(valueMap.values(), CLOSE_CACHE_OPERATION);
        }
        doClose();
        this.closed = true;
    }

    /**
     * 子类关闭扩展
     */
    protected void doClose() {
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        T value = null;
        try {
            value = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    protected <K, V, C extends Configuration<K, V>> Cache<K, V> getOrCreateCache(String cacheName, C configuration,
                                                                                 boolean created) {
        assertNotClosed();
        Map<KeyValueTypePair, Cache> cacheMap = cacheRepository.computeIfAbsent(cacheName, k -> new ConcurrentHashMap<>());
        return cacheMap.computeIfAbsent(new KeyValueTypePair(configuration.getKeyType(), configuration.getValueType()),
                k -> created ? doCreateCache(cacheName, configuration) : null);
    }


    /**
     * 断言：不能关闭
     */
    private void assertNotClosed() {
        if (isClosed()) {
            throw new IllegalStateException("The CacheManager has been closed, current operation should not be invoked!");
        }
    }

    /**
     * 遍历处理
     */
    protected final void iteratorCaches(Iterable<Cache> caches, Consumer<Cache>... consumers) {
        for (Cache cache : caches) {
            for (Consumer<Cache> cacheOperation : consumers) {
                try {
                    cacheOperation.accept(cache);
                } catch (Throwable e) {
                    log.error(e.getMessage());
                }
            }
        }
    }


}
