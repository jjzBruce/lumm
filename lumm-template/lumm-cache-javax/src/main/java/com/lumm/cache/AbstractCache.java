package com.lumm.cache;

import com.lumm.cache.configuration.ConfigurationUtils;
import com.lumm.cache.event.CacheEntryEventPublisher;
import com.lumm.cache.event.GenericCacheEntryEvent;
import com.lumm.cache.integration.CompositeFallbackStorage;
import com.lumm.cache.integration.FallbackStorage;
import com.lumm.cache.management.CacheStatistics;
import com.lumm.cache.management.DummyCacheStatistics;
import com.lumm.cache.management.ManagementUtils;
import com.lumm.cache.management.SimpleCacheStatistics;
import com.lumm.cache.processor.MutableEntryAdapter;
import lombok.extern.slf4j.Slf4j;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.*;
import javax.cache.expiry.Duration;
import javax.cache.expiry.EternalExpiryPolicy;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;
import javax.cache.processor.MutableEntry;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;

/**
 * {@link Cache}
 */
@Slf4j
public abstract class AbstractCache<K, V> implements Cache<K, V> {

    /**
     * 缓存管理器
     */
    private final CacheManager cacheManager;

    /**
     * 缓存名称
     */
    private final String cacheName;

    /**
     * 可变的配置
     */
    private final MutableConfiguration<K, V> configuration;

    /**
     * 过期策略
     */
    private final ExpiryPolicy expiryPolicy;

    /**
     * 缓存击穿兜底类加载，如果未指定，默认为{@link #defaultFallbackStorage}
     */
    private final CacheLoader<K, V> cacheLoader;

    /**
     * 缓存击穿兜底类写入，如果未指定，默认为{@link #defaultFallbackStorage}
     */
    private final CacheWriter<K, V> cacheWriter;

    /**
     * 默认的缓存加载写入实现，具备缓存加载与缓存写入的功能
     */
    private final FallbackStorage defaultFallbackStorage;

    /**
     * 缓存事件发布器
     */
    private final CacheEntryEventPublisher cacheEntryEventPublisher;

    /**
     * 缓存统计类
     */
    private final CacheStatistics cacheStatistics;

    /**
     * 多线程执行器
     */
    private final Executor executor;

    private volatile boolean closed = false;

    /**
     * 构造
     *
     * @param cacheManager  缓存管理器
     * @param cacheName     缓存名
     * @param configuration 缓存配置
     */
    protected AbstractCache(CacheManager cacheManager, String cacheName, Configuration<K, V> configuration) {
        this.cacheManager = cacheManager;
        this.cacheName = cacheName;
        // 缓存配置转为不可变
        this.configuration = ConfigurationUtils.mutableConfiguration(configuration);
        // 获取过期策略
        this.expiryPolicy = resolveExpiryPolicy(this.configuration);
        // 缓存加载写入器
        this.defaultFallbackStorage = new CompositeFallbackStorage(getClassLoader());
        // 解析并获取缓存加载器和缓存写入器
        this.cacheLoader = resolveCacheLoader(this.configuration);
        this.cacheWriter = resolveCacheWriter(this.configuration);
        // 缓存事件发布器
        this.cacheEntryEventPublisher = new CacheEntryEventPublisher();
        // 多线程执行器
        this.executor = ForkJoinPool.commonPool();
        // 缓存统计
        this.cacheStatistics = resolveCacheStatistics();
        // 注册缓存监听器
        this.registerCacheEntryListenersFromConfiguration();
        ManagementUtils.registerMBeansIfRequired(this, cacheStatistics);
    }

    // Operations of CompleteConfiguration

    protected final CompleteConfiguration<K, V> getConfiguration() {
        return this.configuration;
    }

    protected final boolean isReadThrough() {
        return configuration.isReadThrough();
    }

    protected final boolean isWriteThrough() {
        return configuration.isWriteThrough();
    }

    protected final boolean isStatisticsEnabled() {
        return configuration.isStatisticsEnabled();
    }

    private CacheStatistics resolveCacheStatistics() {
        return isStatisticsEnabled() ? new SimpleCacheStatistics() : DummyCacheStatistics.INSTANCE;
    }


    // Operations of ExpiryPolicy and Duration

    private Duration getDuration(Supplier<Duration> durationSupplier) {
        Duration duration = null;
        try {
            duration = durationSupplier.get();
        } catch (Throwable ignore) {
            duration = Duration.ETERNAL;
        }
        return duration;
    }

    /**
     * @see ExpiryPolicy#getExpiryForCreation()
     */
    protected final Duration getExpiryForCreation() {
        return getDuration(expiryPolicy::getExpiryForCreation);
    }

    /**
     * @see ExpiryPolicy#getExpiryForUpdate()
     */
    protected final Duration getExpiryForUpdate() {
        return getDuration(expiryPolicy::getExpiryForUpdate);
    }

    /**
     * @see ExpiryPolicy#getExpiryForAccess()
     */
    protected final Duration getExpiryForAccess() {
        return getDuration(expiryPolicy::getExpiryForAccess);
    }

    private boolean handleExpiryPolicyForCreation(ExpirableEntry<K, V> entry) {
        return handleExpiryPolicy(entry, getExpiryForCreation(), false);
    }

    private boolean handleExpiryPolicyForAccess(ExpirableEntry<K, V> entry) {
        return handleExpiryPolicy(entry, getExpiryForAccess(), true);
    }

    private boolean handleExpiryPolicyForUpdate(ExpirableEntry<K, V> entry) {
        return handleExpiryPolicy(entry, getExpiryForUpdate(), true);
    }


    /**
     * @param duration Creation : If a {@link Duration#ZERO} is returned the new Cache.Entry is considered
     *                 to be already expired and will not be added to the Cache.
     *                 Access or Update : If a {@link Duration#ZERO} is returned a Cache.Entry will be considered
     *                 immediately expired.
     *                 <code>null</code> will result in no change to the previously understood expiry
     * @see ExpiryPolicy
     */
    private boolean handleExpiryPolicy(ExpirableEntry<K, V> entry, Duration duration, boolean removedExpiredEntry) {
        if (entry == null) {
            return false;
        }
        boolean expired = false;
        if (entry.isExpired()) {
            expired = true;

        } else if (duration != null) {
            if (duration.isZero()) {
                expired = true;
            } else {
                long timeStamp = duration.getAdjustedTime(System.currentTimeMillis());
                entry.setTimestamp(timeStamp);
            }
        }

        if (removedExpiredEntry && expired) {
            // Remove Cache.Entry
            K key = entry.getKey();
            V value = entry.getValue();
            removeEntry(key);
            publishExpiredEvent(key, value);
            cacheStatistics.cacheEvictions();
        }

        return expired;
    }


    /**
     * 解析缓存配置，并返回过期策略工厂
     */
    private ExpiryPolicy resolveExpiryPolicy(CompleteConfiguration<?, ?> configuration) {
        Factory<ExpiryPolicy> expiryPolicyFactory = configuration.getExpiryPolicyFactory();
        if (expiryPolicyFactory == null) {
            expiryPolicyFactory = EternalExpiryPolicy::new;
        }
        return expiryPolicyFactory.create();
    }

    // Operations of Cache.Entry and ExpirableEntry

    private ExpirableEntry<K, V> createEntry(K key, V value) {
        return ExpirableEntry.of(key, value);
    }

    private Entry<K, V> createAndPutEntry(K key, V value) {
        ExpirableEntry<K, V> entry = createEntry(key, value);
        if (handleExpiryPolicyForCreation(entry)) {
            // The new Cache.Entry is already expired and will not be added to the Cache.
            return null;
        }
        putEntry(entry);
        publishCreatedEvent(key, value);
        return entry;
    }

    private Entry<K, V> updateEntry(K key, V value) {
        // get current entry
        ExpirableEntry<K, V> entry = getEntry(key);
        // get old value
        V oldValue = entry.getValue();
        // replace value
        entry.setValue(value);
        // update cache
        putEntry(entry);
        // publish update event
        publishUpdatedEvent(key, oldValue, value);

        if (handleExpiryPolicyForUpdate(entry)) {
            // The entry of new value is already expired and will be returned null
            return null;
        }

        return entry;
    }

    /**
     * Put the specified {@link Entry} into cache.
     *
     * @param entry The new instance of {@link Entry<K,V>} is created by {@link Cache}
     * @throws CacheException     if there is a problem doing the put
     * @throws ClassCastException if the implementation is configured to perform
     *                            runtime-type-checking, and the key or value
     *                            types are incompatible with those that have been
     *                            configured for the {@link Cache}
     */
    protected abstract void putEntry(ExpirableEntry<K, V> entry) throws CacheException, ClassCastException;

    /**
     * Remove the specified {@link Entry} from cache.
     *
     * @param key the key of {@link Entry}
     * @return the removed {@link Entry} associated with the given key
     * @throws CacheException     if there is a problem doing the remove
     * @throws ClassCastException if the implementation is configured to perform
     *                            runtime-type-checking, and the key or value
     *                            types are incompatible with those that have been
     *                            configured for the {@link Cache}
     */
    protected abstract ExpirableEntry<K, V> removeEntry(K key) throws CacheException, ClassCastException;

    /**
     * Get the {@link Entry} by the specified key
     *
     * @param key the key of {@link Entry}
     * @return the existed {@link Entry} associated with the given key
     * @throws CacheException     if there is a problem fetching the value
     * @throws ClassCastException if the implementation is configured to perform
     *                            runtime-type-checking, and the key or value
     *                            types are incompatible with those that have been
     *                            configured for the {@link Cache}
     */
    protected abstract ExpirableEntry<K, V> getEntry(K key) throws CacheException, ClassCastException;

    /**
     * Contains the {@link Entry} by the specified key or not.
     *
     * @param key the key of {@link Entry}
     * @return <code>true</code> if contains, or <code>false</code>
     * @throws CacheException     it there is a problem checking the mapping
     * @throws ClassCastException if the implementation is configured to perform
     *                            runtime-type-checking, and the key or value
     *                            types are incompatible with those that have been
     *                            configured for the {@link Cache}
     */
    protected abstract boolean containsEntry(K key) throws CacheException, ClassCastException;

    /**
     * Get all keys of {@link Entry} in the {@link Cache}
     *
     * @return the non-null read-only {@link Set}
     */
    protected abstract Set<K> keySet();

    /**
     * Clear all {@link Entry enties} from cache.
     *
     * @throws CacheException if there is a problem doing the clear
     */
    protected abstract void clearEntries() throws CacheException;


    // Operations of CacheLoader and CacheWriter

    protected CacheWriter<K, V> getCacheWriter() {
        return this.cacheWriter;
    }

    protected CacheLoader<K, V> getCacheLoader() {
        return this.cacheLoader;
    }

    /**
     * 解析缓存配置，并返回缓存加载器实现
     */
    private CacheLoader<K, V> resolveCacheLoader(CompleteConfiguration<K, V> configuration) {
        Factory<CacheLoader<K, V>> cacheLoaderFactory = configuration.getCacheLoaderFactory();
        CacheLoader<K, V> cacheLoader = null;
        if (cacheLoaderFactory != null) {
            cacheLoader = cacheLoaderFactory.create();
        }
        if (cacheLoader == null) {
            cacheLoader = defaultFallbackStorage;
        }
        return cacheLoader;
    }

    /**
     * {@link CompleteConfiguration} -> {@link CacheWriter}
     */
    private CacheWriter<K, V> resolveCacheWriter(CompleteConfiguration<K, V> configuration) {
        Factory<CacheWriter<? super K, ? super V>> cacheWriteFactory = configuration.getCacheWriterFactory();
        CacheWriter<K, V> cacheWriter = null;
        if (cacheWriteFactory != null) {
            cacheWriter = (CacheWriter<K, V>) cacheWriteFactory.create();
        }
        if (cacheWriter == null) {
            cacheWriter = defaultFallbackStorage;
        }
        return cacheWriter;
    }

    protected ClassLoader getClassLoader() {
        return getCacheManager().getClassLoader();
    }

    private void writeEntryIfWriteThrough(Entry<K, V> entry) {
        if (entry != null && isWriteThrough()) {
            getCacheWriter().write(entry);
        }
    }

    private void deleteIfWriteThrough(K key) {
        if (key != null && isWriteThrough()) {
            getCacheWriter().delete(key);
        }
    }

    private V loadValue(K key) {
        return getCacheLoader().load(key);
    }

    private V loadValue(K key, boolean storedEntry) {
        V value = loadValue(key);
        if (value != null && storedEntry) {
            put(key, value);
        }
        return value;
    }

    // Operations of CacheEntryEvent and CacheEntryListenerConfiguration

    private void publishCreatedEvent(K key, V value) {
        cacheEntryEventPublisher.publish(GenericCacheEntryEvent.createdEvent(this, key, value));
    }

    private void publishUpdatedEvent(K key, V oldValue, V value) {
        cacheEntryEventPublisher.publish(GenericCacheEntryEvent.updatedEvent(this, key, oldValue, value));
    }

    private void publishRemovedEvent(K key, V oldValue) {
        cacheEntryEventPublisher.publish(GenericCacheEntryEvent.removedEvent(this, key, oldValue));
    }

    private void publishExpiredEvent(K key, V oldValue) {
        cacheEntryEventPublisher.publish(GenericCacheEntryEvent.expiredEvent(this, key, oldValue));
    }

    /**
     * 从缓存配置中获取缓存事件监听器并注册
     */
    private void registerCacheEntryListenersFromConfiguration() {
        this.configuration.getCacheEntryListenerConfigurations().forEach(this::registerCacheEntryListener);
    }

    /**
     * {@inheritDoc}
     *
     * @param key the key whose associated value is to be returned
     * @return the element, or null, if it does not exist.
     */
    @Override
    public V get(K key) {
        // require key not null
        Objects.requireNonNull(key);
        // require cache not closed
        assertNotClosed();
        ExpirableEntry<K, V> entry = null;
        V value = null;
        long startTime = System.currentTimeMillis();
        try {
            // get from cache
            entry = getEntry(key);
            // check if expired
            if (handleExpiryPolicyForAccess(entry)) {
                return null;
            }
            // If cache missing and read-through enabled, try to load value by {@link CacheLoader}
            if (entry == null && isReadThrough()) {
                // if read through is open , load value and store it in cache
                value = loadValue(key, true);
            } else {
                // if read through is not open, get from entry
                value = getValue(entry);
            }
        } catch (Throwable e) {
            log.error(e.getMessage());
        } finally {
            // do statistics
            // todo statistics
        }

        return value;
    }

    @Override
    public Map<K, V> getAll(Set<? extends K> keys) {
        Map<K, V> result = new LinkedHashMap<>();
        keys.forEach(key -> {
            result.put(key, get(key));
        });
        return result;
    }

    @Override
    public boolean containsKey(K key) {
        assertNotClosed();
        return containsEntry(key);
    }

    @Override
    public void loadAll(Set<? extends K> keys, boolean replaceExistingValues, CompletionListener completionListener) {
        assertNotClosed();
        // 如果配置穿透读取没有打开，则直接返回
        if (!configuration.isReadThrough()) {
            // FIXME: The specification does not mention that
            // CompletionListener#onCompletion() method should be invoked or not.
            completionListener.onCompletion();
            return;
        }

        CompletableFuture.runAsync(() -> {
            // Implementations may choose to load multiple keys from the provided Set in parallel.
            // Iteration however must not occur in parallel, thus allow for non-thread-safe Sets to be used.
            keys.forEach(key -> {
                // If an entry for a key already exists in the Cache, a value will be loaded
                V value = loadValue(key, false);
                // if and only if replaceExistingValues is true.
                if (replaceExistingValues) {
                    replace(key, value);
                } else {
                    put(key, value);
                }
            });
        }, executor).whenComplete((v, e) -> {
            // the CompletionListener may be null
            if (completionListener != null) {
                // completed exceptionally
                if (e instanceof Exception && e.getCause() instanceof Exception) {
                    completionListener.onException((Exception) e.getCause());
                } else {
                    completionListener.onCompletion();
                }
            }
        });
    }

    @Override
    public void put(K key, V value) {
        assertNotClosed();
        Entry<K, V> entry = null;
        long startTime = System.currentTimeMillis();
        try {
            if (!containsKey(key)) {
                createAndPutEntry(key, value);
            } else {
                updateEntry(key, value);
            }
        } finally {
            // load entry if write through is open
            writeEntryIfWriteThrough(entry);
            // do statistics todo...
        }
    }

    @Override
    public V getAndPut(K key, V value) {
        V oldValue = get(key);
        put(key, value);
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        map.forEach((k, v) -> put(k, v));
    }

    @Override
    public boolean putIfAbsent(K key, V value) {
        if (!containsKey(key)) {
            put(key, value);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean remove(K key) {
        assertNotClosed();
        Objects.requireNonNull(key);
        boolean removed = false;
        long startTime = System.currentTimeMillis();
        try {
            ExpirableEntry<K, V> oldEntry = removeEntry(key);
            removed = oldEntry != null;
            if (removed) {
                publishRemovedEvent(key, oldEntry.getValue());
            }
        } finally {
            deleteIfWriteThrough(key);
            cacheStatistics.cacheRemovals();
            cacheStatistics.cacheRemovesTime(System.currentTimeMillis() - startTime);
        }
        return removed;
    }

    @Override
    public boolean remove(K key, V oldValue) {
        if (containsKey(key) && Objects.equals(oldValue, get(key))) {
            return remove(key);
        }
        return false;
    }

    @Override
    public V getAndRemove(K key) {
        V oldValue = get(key);
        remove(key);
        return oldValue;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        Objects.requireNonNull(oldValue);
        if (containsKey(key) && Objects.equals(oldValue, get(key))) {
            return replace(key, newValue);
        } else {
            return false;
        }
    }

    @Override
    public boolean replace(K key, V value) {
        if (containsKey(key)) {
            put(key, value);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public V getAndReplace(K key, V value) {
        V oldValue = get(key);
        replace(key, value);
        return oldValue;
    }

    @Override
    public void removeAll(Set<? extends K> keys) {
        keys.forEach(this::remove);
    }

    @Override
    public void removeAll() {
        removeAll(keySet());
    }

    @Override
    public void clear() {
        assertNotClosed();
        clearEntries();
        defaultFallbackStorage.destroy();
        cacheStatistics.reset();
    }

    /**
     * {@inheritDoc}
     *
     * @param clazz the configuration interface or class to return. This includes
     *              {@link Configuration}.class and
     *              {@link CompleteConfiguration}s.
     */
    @Override
    public <C extends Configuration<K, V>> C getConfiguration(Class<C> clazz) {
        if (!Configuration.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("The class must be inherited of " + Configuration.class.getName());
        }
        return (C) ConfigurationUtils.immutableConfiguration(configuration);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Current method calls the methods of {@link ExpiryPolicy}:
     * <ul>
     *     <li>Yes {@link ExpiryPolicy#getExpiryForCreation} (for the following cases:
     *     (1) setValue called and an entry did not exist for key before invoke was called.
     *     (2) if read-through enabled and getValue() is called and causes a new entry to be loaded for key)
     *     </li>
     *     <li>Yes {@link ExpiryPolicy#getExpiryForAccess} (when getValue was called and no other mutations
     *     occurred during entry processor execution. note: Create, modify or remove take precedence over Access)
     *     </li>
     *     <li>Yes {@link ExpiryPolicy#getExpiryForUpdate} (when setValue was called and the entry already existed
     *     before entry processor was called)</li>
     * </ul>
     */
    @Override
    public <T> T invoke(K key, EntryProcessor<K, V, T> entryProcessor, Object... arguments) throws EntryProcessorException {
        MutableEntry<K, V> mutableEntry = MutableEntryAdapter.of(key, this);
        return entryProcessor.process(mutableEntry, arguments);
    }

    @Override
    public <T> Map<K, EntryProcessorResult<T>> invokeAll(Set<? extends K> keys, EntryProcessor<K, V, T> entryProcessor, Object... arguments) {
        Map<K, EntryProcessorResult<T>> result = new LinkedHashMap<>();
        keys.forEach(key -> {
            result.put(key, () -> invoke(key, entryProcessor, arguments));
        });
        return result;
    }

    @Override
    public String getName() {
        return cacheName;
    }

    @Override
    public CacheManager getCacheManager() {
        return cacheManager;
    }

    @Override
    public void close() {
        if (isClosed()) {
            return;
        }
        doClose();

        closed = true;
    }

    /**
     * Subclass could override this method.
     */
    protected void doClose() {
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        return getCacheManager().unwrap(clazz);
    }

    @Override
    public void registerCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        cacheEntryEventPublisher.registerCacheEntryListener(cacheEntryListenerConfiguration);
    }

    @Override
    public void deregisterCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        cacheEntryEventPublisher.deregisterCacheEntryListener(cacheEntryListenerConfiguration);
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        assertNotClosed();
        List<Entry<K, V>> entries = new LinkedList<>();

        keySet().forEach(key -> {
            entries.add(ExpirableEntry.of(key, get(key)));
        });

        return entries.iterator();
    }


    // Other Operations
    private void assertNotClosed() {
        if (isClosed()) {
            throw new IllegalStateException("Current cache has been closed! No operation should be executed.");
        }
    }

    private static <K, V> V getValue(Entry<K, V> entry) {
        return entry != null ? entry.getValue() : null;
    }

}
