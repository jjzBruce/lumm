package com.lumm.cache.annotation;


import javax.cache.annotation.CacheKeyGenerator;
import javax.cache.annotation.CacheKeyInvocationContext;
import javax.cache.annotation.GeneratedCacheKey;
import java.lang.annotation.Annotation;

/**
 * 默认的缓存键生成器接口实现
 */
public class DefaultCacheKeyGenerator implements CacheKeyGenerator {

    @Override
    public GeneratedCacheKey generateCacheKey(CacheKeyInvocationContext<? extends Annotation> cacheKeyInvocationContext) {
        return new DefaultGeneratedCacheKey(cacheKeyInvocationContext);
    }



}
