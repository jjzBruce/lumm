package com.lumm.cache.annotation.util;


import com.lumm.cache.annotation.DefaultCacheKeyGenerator;
import com.lumm.cache.annotation.DefaultCacheResolverFactory;

import javax.cache.annotation.*;
import java.util.function.Supplier;

import static java.lang.Boolean.TRUE;

/**
 * 缓存操作注解信息
 */
public class CacheOperationAnnotationInfo {

    private static Class<? extends Throwable>[] EMPTY_FAILURE = new Class[0];

    private final String cacheName;

    private final Boolean afterInvocation;

    private final Class<? extends CacheResolverFactory> cacheResolverFactoryClass;

    /**
     * 缓存键生成器类
     */
    private final Class<? extends CacheKeyGenerator> cacheKeyGeneratorClass;

    private final Class<? extends Throwable>[] appliedFailures;

    private final Class<? extends Throwable>[] nonAppliedFailures;

    private final Boolean skipGet;

    private final String exceptionCacheName;

    /**
     * 构造
     * @param that
     */
    public CacheOperationAnnotationInfo(CacheOperationAnnotationInfo that) {
        this.cacheName = that.cacheName;
        this.afterInvocation = that.afterInvocation;
        this.cacheResolverFactoryClass = that.cacheResolverFactoryClass;
        this.cacheKeyGeneratorClass = that.cacheKeyGeneratorClass;
        this.appliedFailures = that.appliedFailures;
        this.nonAppliedFailures = that.nonAppliedFailures;
        this.skipGet = that.skipGet;
        this.exceptionCacheName = null;
    }

    /**
     * 构造
     * @param cachePut
     * @param cacheDefaults
     */
    public CacheOperationAnnotationInfo(CachePut cachePut, CacheDefaults cacheDefaults) {
        this.cacheName = getCacheName(cachePut::cacheName, cacheDefaults::cacheName);
        this.afterInvocation = cachePut.afterInvocation();
        this.cacheResolverFactoryClass = getCacheResolverFactoryClass(cachePut::cacheResolverFactory, cacheDefaults::cacheResolverFactory);
        this.cacheKeyGeneratorClass = getCacheKeyGeneratorClass(cachePut::cacheKeyGenerator, cacheDefaults::cacheKeyGenerator);
        this.appliedFailures = afterInvocation ? cachePut.cacheFor() : EMPTY_FAILURE;
        this.nonAppliedFailures = afterInvocation ? cachePut.noCacheFor() : EMPTY_FAILURE;
        this.skipGet = null;
        this.exceptionCacheName = null;
    }

    /**
     * 构造
     * @param cacheRemove
     * @param cacheDefaults
     */
    public CacheOperationAnnotationInfo(CacheRemove cacheRemove, CacheDefaults cacheDefaults) {
        this.cacheName = getCacheName(cacheRemove::cacheName, cacheDefaults::cacheName);
        this.afterInvocation = cacheRemove.afterInvocation();
        this.cacheResolverFactoryClass = getCacheResolverFactoryClass(cacheRemove::cacheResolverFactory, cacheDefaults::cacheResolverFactory);
        this.cacheKeyGeneratorClass = getCacheKeyGeneratorClass(cacheRemove::cacheKeyGenerator, cacheDefaults::cacheKeyGenerator);
        this.appliedFailures = afterInvocation ? cacheRemove.evictFor() : EMPTY_FAILURE;
        this.nonAppliedFailures = afterInvocation ? cacheRemove.noEvictFor() : EMPTY_FAILURE;
        this.skipGet = null;
        this.exceptionCacheName = null;
    }

    /**
     * 构造
     * @param cacheRemoveAll
     * @param cacheDefaults
     */
    public CacheOperationAnnotationInfo(CacheRemoveAll cacheRemoveAll, CacheDefaults cacheDefaults) {
        this.cacheName = getCacheName(cacheRemoveAll::cacheName, cacheDefaults::cacheName);
        this.afterInvocation = cacheRemoveAll.afterInvocation();
        this.cacheResolverFactoryClass = getCacheResolverFactoryClass(cacheRemoveAll::cacheResolverFactory, cacheDefaults::cacheResolverFactory);
        this.cacheKeyGeneratorClass = null;
        this.appliedFailures = afterInvocation ? cacheRemoveAll.evictFor() : EMPTY_FAILURE;
        this.nonAppliedFailures = afterInvocation ? cacheRemoveAll.noEvictFor() : EMPTY_FAILURE;
        this.skipGet = null;
        this.exceptionCacheName = null;
    }

    /**
     * 构造
     * @param cacheResult
     * @param cacheDefaults
     */
    public CacheOperationAnnotationInfo(CacheResult cacheResult, CacheDefaults cacheDefaults) {
        this.cacheName = getCacheName(cacheResult::cacheName, cacheDefaults::cacheName);
        this.afterInvocation = null;
        this.cacheResolverFactoryClass = getCacheResolverFactoryClass(cacheResult::cacheResolverFactory, cacheDefaults::cacheResolverFactory);
        this.cacheKeyGeneratorClass = getCacheKeyGeneratorClass(cacheResult::cacheKeyGenerator, cacheDefaults::cacheKeyGenerator);
        this.appliedFailures = cacheResult.cachedExceptions();
        this.nonAppliedFailures = cacheResult.nonCachedExceptions();
        this.skipGet = cacheResult.skipGet();
        this.exceptionCacheName = cacheResult.exceptionCacheName();
    }

    public String getCacheName() {
        return cacheName;
    }

    public Boolean getAfterInvocation() {
        return afterInvocation;
    }

    public boolean isAfterInvocation() {
        return TRUE.equals(getAfterInvocation());
    }

    public Class<? extends CacheResolverFactory> getCacheResolverFactoryClass() {
        return cacheResolverFactoryClass;
    }

    public Class<? extends CacheKeyGenerator> getCacheKeyGeneratorClass() {
        return cacheKeyGeneratorClass;
    }

    public Class<? extends Throwable>[] getAppliedFailures() {
        return appliedFailures;
    }

    public Class<? extends Throwable>[] getNonAppliedFailures() {
        return nonAppliedFailures;
    }

    public Boolean getSkipGet() {
        return skipGet;
    }

    public boolean isSkipGet() {
        return TRUE.equals(getSkipGet());
    }

    public String getExceptionCacheName() {
        return exceptionCacheName;
    }


    private String getCacheName(Supplier<String> cacheNameSupplier, Supplier<String> defaultCacheNameSupplier) {
        String cacheName = cacheNameSupplier.get();
        if ("".equals(cacheName)) {
            cacheName = defaultCacheNameSupplier.get();
        }
        return cacheName;
    }

    private Class<? extends CacheKeyGenerator> getCacheKeyGeneratorClass(
            Supplier<Class<? extends CacheKeyGenerator>> cacheKeyGeneratorClassSupplier,
            Supplier<Class<? extends CacheKeyGenerator>> defaultCacheKeyGeneratorClassSupplier) {
        Class<? extends CacheKeyGenerator> cacheKeyGeneratorClass = cacheKeyGeneratorClassSupplier.get();
        if (CacheKeyGenerator.class.equals(cacheKeyGeneratorClass)) {
            cacheKeyGeneratorClass = defaultCacheKeyGeneratorClassSupplier.get();
        }

        if (CacheKeyGenerator.class.equals(cacheKeyGeneratorClass)) { // Default value as well
            cacheKeyGeneratorClass = DefaultCacheKeyGenerator.class;
        }

        return cacheKeyGeneratorClass;
    }

    private Class<? extends CacheResolverFactory> getCacheResolverFactoryClass(
            Supplier<Class<? extends CacheResolverFactory>> cacheResolverFactoryClassSupplier,
            Supplier<Class<? extends CacheResolverFactory>> defaultCacheResolverFactoryClassSupplier) {
        Class<? extends CacheResolverFactory> cacheResolverFactoryClass = cacheResolverFactoryClassSupplier.get();
        if (CacheResolverFactory.class.equals(cacheResolverFactoryClass)) {
            cacheResolverFactoryClass = defaultCacheResolverFactoryClassSupplier.get();
        }

        if (CacheResolverFactory.class.equals(cacheResolverFactoryClass)) { // Default value as well
            cacheResolverFactoryClass = DefaultCacheResolverFactory.class;
        }

        return cacheResolverFactoryClass;
    }
}
