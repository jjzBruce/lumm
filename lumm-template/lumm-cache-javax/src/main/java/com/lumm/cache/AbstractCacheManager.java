//package com.lumm.cache;
//
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.cache.Cache;
//import javax.cache.CacheException;
//import javax.cache.CacheManager;
//import javax.cache.configuration.Configuration;
//import javax.cache.configuration.MutableConfiguration;
//import javax.cache.spi.CachingProvider;
//import java.net.URI;
//import java.util.Collections;
//import java.util.Map;
//import java.util.Objects;
//import java.util.Properties;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.function.Consumer;
//
//import static java.lang.String.format;
//
///**
// * @Description Abstract {@link CacheManager}
// * @Author zhangj
// * @Date 2022/11/27 23:46
// */
//public abstract class AbstractCacheManager implements CacheManager {
//
//    private final Logger logger = LoggerFactory.getLogger(AbstractCacheManager.class.getName());
//
//    /**
//     * Cache 清空方法
//     */
//    private static final Consumer<Cache> CLEAR_CACHE_OPERATION = Cache::clear;
//
//    /**
//     * Cache 关闭方法
//     */
//    private static final Consumer<Cache> CLOSE_CACHE_OPERATION = Cache::close;
//
//    private final CachingProvider cachingProvider;
//
//    private final URI uri;
//
//    private final ClassLoader classLoader;
//
//    private final Serializers serializers;
//
//    private final Deserializers deserializers;
//
//    private final Properties properties;
//
//    private volatile boolean closed;
//
//    private ConcurrentHashMap<String, Map<KeyValueTypePair, Cache>> cacheRepository = new ConcurrentHashMap<>();
//
//    public AbstractCacheManager(CachingProvider cachingProvider, URI uri, ClassLoader classLoader, Properties properties) {
//        this.cachingProvider = cachingProvider;
//        this.uri = uri == null ? cachingProvider.getDefaultURI() : uri;
//        this.properties = properties == null ? cachingProvider.getDefaultProperties() : properties;
//        this.classLoader = classLoader == null ? cachingProvider.getDefaultClassLoader() : classLoader;
//        this.serializers = initSerializers(this.classLoader);
//        this.deserializers = initDeserializers(this.classLoader);
//    }
//
//    private Deserializers initDeserializers(ClassLoader classLoader) {
//        Deserializers deserializers = new Deserializers(classLoader);
//        deserializers.loadSPI();
//        return deserializers;
//    }
//
//    private Serializers initSerializers(ClassLoader classLoader) {
//        Serializers serializers = new Serializers(classLoader);
//        serializers.loadSPI();
//        return serializers;
//    }
//
//    @Override
//    public CachingProvider getCachingProvider() {
//        return cachingProvider;
//    }
//
//    @Override
//    public URI getURI() {
//        return uri;
//    }
//
//    @Override
//    public ClassLoader getClassLoader() {
//        return classLoader;
//    }
//
//    @Override
//    public Properties getProperties() {
//        return properties;
//    }
//
//    public Serializers getSerializers() {
//        return serializers;
//    }
//
//    public Deserializers getDeserializers() {
//        return deserializers;
//    }
//
//    @Override
//    public <K, V, C extends Configuration<K, V>> Cache<K, V> createCache(String cacheName, C configuration) throws IllegalArgumentException {
//        // If a Cache with the specified name is known to the CacheManager, a CacheException is thrown.
//        // 如果指定的Cache在cacheRepository已经存在，就抛出异常
//        if (!cacheRepository.getOrDefault(cacheName, Collections.emptyMap()).isEmpty()) {
//            throw new CacheException(format("The Cache whose name is '%s' is already existed, " +
//                    "please try another name to create a new Cache.", cacheName));
//        }
//        // If a Cache with the specified name is unknown the CacheManager, one is created according to
//        // the provided Configuration after which it becomes managed by the CacheManager.
//        return getOrCreateCache(cacheName, configuration, true);
//    }
//
//    /**
//     * 子类实现根据配置{@link Configuration configuration}创建一个{@link Cache cache}
//     */
//    protected abstract <K, V, C extends Configuration<K, V>> Cache doCreateCache(String cacheName, C configuration);
//
//    @Override
//    public <K, V> Cache<K, V> getCache(String cacheName, Class<K> keyType, Class<V> valueType) {
//        MutableConfiguration<K, V> configuration = new MutableConfiguration<K, V>().setTypes(keyType, valueType);
//        return getOrCreateCache(cacheName, configuration, false);
//    }
//
//    @Override
//    public <K, V> Cache<K, V> getCache(String cacheName) {
//        return getCache(cacheName, (Class<K>) Object.class, (Class<V>) Object.class);
//    }
//
//    @Override
//    public Iterable<String> getCacheNames() {
//        assertNotClosed();
//        return cacheRepository.keySet();
//    }
//
//    @Override
//    public void destroyCache(String cacheName) {
//        Objects.requireNonNull(cacheName);
//        assertNotClosed();
//        Map<KeyValueTypePair, Cache> remove = cacheRepository.remove(cacheName);
//        if (remove != null) {
//            iteratorCaches(remove.values(), CLEAR_CACHE_OPERATION, CLOSE_CACHE_OPERATION);
//        }
//    }
//
//    @Override
//    public void enableManagement(String cacheName, boolean enabled) {
//        assertNotClosed();
//        throw new UnsupportedOperationException("to be supported.");
//    }
//
//    @Override
//    public void enableStatistics(String cacheName, boolean enabled) {
//        assertNotClosed();
//        throw new UnsupportedOperationException("to be supported.");
//    }
//
//    @Override
//    public final void close() {
//        if (isClosed()) {
//            logger.warn("The CacheManager has been closed, current close operation will be ignored!");
//            return;
//        }
//        for (Map<KeyValueTypePair, Cache> valueMap : cacheRepository.values()) {
//            iteratorCaches(valueMap.values(), CLOSE_CACHE_OPERATION);
//        }
//        doClose();
//        this.closed = true;
//    }
//
//    /**
//     * 子类关闭扩展
//     */
//    protected void doClose() {
//    }
//
//    @Override
//    public boolean isClosed() {
//        return closed;
//    }
//
//    @Override
//    public <T> T unwrap(Class<T> clazz) {
//        T value = null;
//        try {
//            value = clazz.newInstance();
//        } catch (InstantiationException | IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//        return value;
//    }
//
//    protected <K, V, C extends Configuration<K, V>> Cache<K, V> getOrCreateCache(String cacheName, C configuration,
//                                                                                 boolean created) {
//        assertNotClosed();
//        Map<KeyValueTypePair, Cache> cacheMap = cacheRepository.computeIfAbsent(cacheName, k -> new ConcurrentHashMap<>());
//        return cacheMap.computeIfAbsent(new KeyValueTypePair(configuration.getKeyType(), configuration.getValueType()),
//                k -> created ? doCreateCache(cacheName, configuration) : null);
//    }
//
//
//    /**
//     * 断言：不能关闭
//     */
//    private void assertNotClosed() {
//        if (isClosed()) {
//            throw new IllegalStateException("The CacheManager has been closed, current operation should not be invoked!");
//        }
//    }
//
//    /**
//     * 遍历处理
//     */
//    protected final void iteratorCaches(Iterable<Cache> caches, Consumer<Cache>... consumers) {
//        for (Cache cache : caches) {
//            for (Consumer<Cache> cacheOperation : consumers) {
//                try {
//                    cacheOperation.accept(cache);
//                } catch (Throwable e) {
//                    logger.error(e.getMessage());
//                }
//            }
//        }
//    }
//
//
//}
