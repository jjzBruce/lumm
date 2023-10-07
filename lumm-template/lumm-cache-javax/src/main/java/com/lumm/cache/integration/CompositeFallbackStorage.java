package com.lumm.cache.integration;

import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

/**
 * 缓存加载写入接口组合实现
 */
public class CompositeFallbackStorage extends AbstractFallbackStorage<Object, Object> {

    /**
     * 维护所有的缓存加载写入器与类加载的映射
     */
    private static final ConcurrentMap<ClassLoader, List<FallbackStorage>> fallbackStoragesCache =
            new ConcurrentHashMap<>();

    /**
     * 缓存加载写入器列表
     */
    private final List<FallbackStorage> fallbackStorages;

    public CompositeFallbackStorage() {
        this(Thread.currentThread().getContextClassLoader());
    }

    /**
     * 构造
     * @param classLoader 类加载器
     */
    public CompositeFallbackStorage(ClassLoader classLoader) {
        // 最高优先级
        super(Integer.MIN_VALUE);
        this.fallbackStorages = fallbackStoragesCache.computeIfAbsent(classLoader, this::loadFallbackStorages);
    }


    /**
     * 通过类加载器加载对应的缓存加载写入实现类
     * @param classLoader 类加载器
     * @return List<FallbackStorage>
     */
    private List<FallbackStorage> loadFallbackStorages(ClassLoader classLoader) {
        // SPI 获取对应的实现类
        return StreamSupport.stream(ServiceLoader.load(FallbackStorage.class, classLoader).spliterator(), false)
                .sorted(PRIORITY_COMPARATOR)
                .collect(toList());
    }



    @Override
    public Object load(Object key) throws CacheLoaderException {
        Object value = null;
        for (FallbackStorage fallbackStorage : fallbackStorages) {
            value = fallbackStorage.load(key);
            if (value != null) {
                break;
            }
        }
        return value;
    }

    @Override
    public void write(Cache.Entry entry) throws CacheWriterException {
        fallbackStorages.forEach(fallbackStorage -> fallbackStorage.write(entry));
    }

    @Override
    public void delete(Object key) throws CacheWriterException {
        fallbackStorages.forEach(fallbackStorage -> fallbackStorage.delete(key));
    }

    @Override
    public void destroy() {
        fallbackStorages.forEach(FallbackStorage::destroy);
    }
}
