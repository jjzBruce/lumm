package com.lumm.cache.annotation.interceptor;

import com.lumm.cache.annotation.ReflectiveCacheKeyInvocationContext;
import com.lumm.cache.annotation.util.CacheOperationAnnotationInfo;
import com.lumm.cache.interceptor.AnnotatedInterceptor;
import com.lumm.cache.interceptor.util.CacheAnnotationUtils;
import com.lumm.cache.util.ClassUtils;
import lombok.NoArgsConstructor;

import javax.cache.Cache;
import javax.cache.annotation.*;
import javax.interceptor.InvocationContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 缓存式操作拦截器
 *
 * @param <A>
 */
@NoArgsConstructor
public abstract class CacheOperationInterceptor<A extends Annotation> extends AnnotatedInterceptor<A> {

    private final ConcurrentMap<A, CacheResolverFactory> cacheResolverFactoryCache = new ConcurrentHashMap<>();

    private final ConcurrentMap<A, CacheKeyGenerator> cacheKeyGeneratorCache = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldRegisterSyntheticInterceptorBindingType() {
        return true;
    }
    
    @Override
    protected Object intercept(InvocationContext context, A cacheOperationAnnotation) throws Throwable {
        Object target = context.getTarget();
        Method method = context.getMethod();
        Object[] parameters = context.getParameters();

        CacheKeyInvocationContext<A> cacheKeyInvocationContext = new ReflectiveCacheKeyInvocationContext<>(target, method, parameters);

        CacheDefaults cacheDefaults = CacheAnnotationUtils.findCacheDefaults(method, target);

        CacheOperationAnnotationInfo cacheOperationAnnotationInfo = getCacheOperationAnnotationInfo(cacheOperationAnnotation, cacheDefaults);

        Object result = null;

        Cache cache = resolveCache(cacheOperationAnnotation, cacheKeyInvocationContext, cacheOperationAnnotationInfo);

        Optional<GeneratedCacheKey> cacheKey = generateCacheKey(cacheOperationAnnotation, cacheKeyInvocationContext, cacheOperationAnnotationInfo);

        try {
            result = beforeExecute(cacheOperationAnnotation, cacheKeyInvocationContext, cacheOperationAnnotationInfo, cache, cacheKey);
            if (result == null) {
                result = context.proceed();
                afterExecute(cacheOperationAnnotation, cacheKeyInvocationContext, cacheOperationAnnotationInfo, cache, cacheKey, result);
            }
        } catch (Throwable e) {
            Throwable failure = getFailure(e);
            if (shouldHandleFailure(failure, cacheOperationAnnotationInfo)) {
                handleFailure(cacheOperationAnnotation, cacheKeyInvocationContext, cacheOperationAnnotationInfo, cache, cacheKey, failure);
            }
        }

        return result;
    }

    protected abstract CacheOperationAnnotationInfo getCacheOperationAnnotationInfo(A cacheOperationAnnotation, CacheDefaults cacheDefaults);

    protected abstract Object beforeExecute(A cacheOperationAnnotation, CacheKeyInvocationContext<A> cacheKeyInvocationContext,
                                            CacheOperationAnnotationInfo cacheOperationAnnotationInfo,
                                            Cache cache, Optional<GeneratedCacheKey> cacheKey);

    protected abstract void afterExecute(A cacheOperationAnnotation, CacheKeyInvocationContext<A> cacheKeyInvocationContext,
                                         CacheOperationAnnotationInfo cacheOperationAnnotationInfo,
                                         Cache cache, Optional<GeneratedCacheKey> cacheKey, Object result);

    protected abstract void handleFailure(A cacheOperationAnnotation, CacheKeyInvocationContext<A> cacheKeyInvocationContext,
                                          CacheOperationAnnotationInfo cacheOperationAnnotationInfo,
                                          Cache cache, Optional<GeneratedCacheKey> cacheKey, Throwable failure);

    private Cache resolveCache(A cacheOperationAnnotation, CacheKeyInvocationContext<A> cacheKeyInvocationContext,
                               CacheOperationAnnotationInfo cacheOperationAnnotationInfo) {
        CacheResolverFactory cacheResolverFactory = getCacheResolverFactory(cacheOperationAnnotation,
                cacheKeyInvocationContext, cacheOperationAnnotationInfo);
        CacheResolver cacheResolver = cacheResolverFactory.getCacheResolver(cacheKeyInvocationContext);
        return cacheResolver.resolveCache(cacheKeyInvocationContext);
    }

    protected CacheResolverFactory getCacheResolverFactory(A cacheOperationAnnotation,
                                                           CacheKeyInvocationContext<A> cacheKeyInvocationContext,
                                                           CacheOperationAnnotationInfo cacheOperationAnnotationInfo) {
        return cacheResolverFactoryCache.computeIfAbsent(cacheOperationAnnotation, key -> {
            Class<? extends CacheResolverFactory> cacheResolverFactoryClass = cacheOperationAnnotationInfo.getCacheResolverFactoryClass();
            return cacheKeyInvocationContext.unwrap(cacheResolverFactoryClass);
        });
    }

    private Optional<GeneratedCacheKey> generateCacheKey(A cacheOperationAnnotation,
                                                         CacheKeyInvocationContext<A> cacheKeyInvocationContext,
                                                         CacheOperationAnnotationInfo cacheOperationAnnotationInfo) {
        CacheKeyGenerator cacheKeyGenerator = getCacheKeyGenerator(cacheOperationAnnotation, cacheKeyInvocationContext, cacheOperationAnnotationInfo);

        if (cacheKeyGenerator == null) {
            return Optional.empty();
        }

        return Optional.of(cacheKeyGenerator.generateCacheKey(cacheKeyInvocationContext));
    }

    private CacheKeyGenerator getCacheKeyGenerator(A cacheOperationAnnotation,
                                                   CacheKeyInvocationContext<A> cacheKeyInvocationContext,
                                                   CacheOperationAnnotationInfo cacheOperationAnnotationInfo) {

        Class<? extends CacheKeyGenerator> cacheKeyGeneratorClass = cacheOperationAnnotationInfo.getCacheKeyGeneratorClass();

        if (cacheKeyGeneratorClass == null) {
            return null;
        }

        return cacheKeyGeneratorCache.computeIfAbsent(cacheOperationAnnotation, key ->
                cacheKeyInvocationContext.unwrap(cacheKeyGeneratorClass)
        );
    }

    private boolean shouldHandleFailure(Throwable failure, CacheOperationAnnotationInfo cacheOperationAnnotationInfo) {
        Class<? extends Throwable>[] appliedFailures = cacheOperationAnnotationInfo.getAppliedFailures();
        Class<? extends Throwable>[] nonAppliedFailures = cacheOperationAnnotationInfo.getNonAppliedFailures();

        boolean hasAppliedFailures = appliedFailures.length > 0;
        boolean hasNonAppliedFailures = nonAppliedFailures.length > 0;

        if (!hasAppliedFailures && !hasNonAppliedFailures) {
            return true;
        }

        Class<? extends Throwable> failureType = failure.getClass();

        if (hasAppliedFailures && !hasNonAppliedFailures) {
            return ClassUtils.isDerived(failureType, appliedFailures);
        } else if (!hasAppliedFailures && hasNonAppliedFailures) {
            return !ClassUtils.isDerived(failureType, nonAppliedFailures);
        } else if (hasAppliedFailures && hasNonAppliedFailures) {
            return ClassUtils.isDerived(failureType, appliedFailures) && !ClassUtils.isDerived(failureType, nonAppliedFailures);
        }

        return false;
    }
}
