package com.lumm.cache.annotation;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.annotation.*;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 缓存解析器工厂接口{@link CacheResolverFactory}的默认实现。<br/>
 * 缓存解析器工厂接口{@link CacheResolverFactory}是用于定义和配置缓存解析器（Cache Resolver）的工厂接口。<br/>
 * 缓存解析器的作用是确定在执行方法时应该使用哪个缓存。 <br/>
 */
public class DefaultCacheResolverFactory implements CacheResolverFactory {

    /**
     * 获取在运行时用于解析{@link CacheResult}, {@link CachePut},
     * {@link CacheRemove}, 或 {@link CacheRemoveAll}批注的缓存。
     *
     * @param cacheMethodDetails The details of the annotated method to get the
     *                           {@link CacheResolver} for. @return The {@link
     *                           CacheResolver} instance to be
     *                           used by the interceptor.
     * @return CacheResolver
     */
    @Override
    public CacheResolver getCacheResolver(CacheMethodDetails<? extends Annotation> cacheMethodDetails) {
        CacheManager cacheManager = getCacheManager(cacheMethodDetails);
        return new DefaultCacheResolver(cacheManager);
    }

    /**
     * 获取在运行时用于解析{@link CacheResult}注释的缓存异常的解析器。
     *
     * @param cacheMethodDetails The details of the annotated method to get the
     *                           {@link CacheResolver} for.
     * @return
     */
    @Override
    public CacheResolver getExceptionCacheResolver(CacheMethodDetails<CacheResult> cacheMethodDetails) {
        CacheManager cacheManager = getCacheManager(cacheMethodDetails);
        CacheResult cacheResult = cacheMethodDetails.getCacheAnnotation();
        String exceptionCacheName = cacheResult.exceptionCacheName();
        return new CacheResolver() {

            @Override
            public <K, V> Cache<K, V> resolveCache(CacheInvocationContext<? extends Annotation> cacheInvocationContext) {
                Cache exceptionCache = cacheManager.getCache(exceptionCacheName);
                if (exceptionCache == null) {
                    exceptionCache = cacheManager.createCache(exceptionCacheName, new MutableConfiguration<>());
                }
                return exceptionCache;
            }
        };
    }

    private CacheManager getCacheManager(CacheMethodDetails<? extends Annotation> cacheMethodDetails) {
        Method method = cacheMethodDetails.getMethod();
        Class<?> declaringClass = method.getDeclaringClass();
        ClassLoader classLoader = declaringClass.getClassLoader();
        CachingProvider cachingProvider = Caching.getCachingProvider(classLoader);
        return cachingProvider.getCacheManager(cachingProvider.getDefaultURI(), classLoader);
    }
}
