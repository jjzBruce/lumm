package com.lumm.cache.annotation;

import cn.hutool.core.util.ArrayUtil;
import com.lumm.cache.annotation.util.CacheAnnotationUtils;
import lombok.Getter;

import javax.cache.annotation.CacheMethodDetails;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 被缓存注解注解了的方法详情{@link CacheMethodDetails}接口的实现类，基于反射。 <br/>
 * 提供方法、注解信息、缓存注解和缓存名信息
 */
public class ReflectiveCacheMethodDetails<A extends Annotation> implements CacheMethodDetails<A> {

    /**
     * 方法
     */
    @Getter
    private final Method method;

    /**
     * 注解集合
     */
    @Getter
    private final Set<Annotation> annotations;

    /**
     * 缓存注解
     */
    private A cacheAnnotation;

    /**
     * 缓存方法
     */
    private String cacheName;

    /**
     * 构造
     *
     * @param method 方法
     */
    public ReflectiveCacheMethodDetails(Method method) {
        Objects.requireNonNull(method, "方法不能为null!");
        this.method = method;
        // 收集方法上面的注解信息
        this.annotations = getAnnotations(method.getAnnotations());
    }

    /**
     * 解析并返回缓存名
     *
     * @return 缓存名
     */
    protected String resolveCacheName() {
        return CacheAnnotationUtils.findCacheName(getCacheAnnotation(), getMethod());
    }

    /**
     * 解析并返回缓存注解
     *
     * @return
     */
    protected A resolveCacheAnnotation() {
        return CacheAnnotationUtils.findCacheAnnotation(getMethod());
    }

    /**
     * 获取注解列表，原数据将变为不可能列表
     *
     * @param annotations
     * @return
     */
    static Set<Annotation> getAnnotations(Annotation[] annotations) {
        if (ArrayUtil.isEmpty(annotations)) {
            return Collections.emptySet();
        }
        Set<Annotation> annotationsSet = new LinkedHashSet<>();
        for (Annotation annotation : annotations) {
            annotationsSet.add(annotation);
        }
        return Collections.unmodifiableSet(annotationsSet);
    }


    @Override
    public final A getCacheAnnotation() {
        if (cacheAnnotation == null) {
            cacheAnnotation = resolveCacheAnnotation();
        }
        return cacheAnnotation;
    }

    @Override
    public final String getCacheName() {
        if (cacheName == null) {
            cacheName = resolveCacheName();
        }
        return cacheName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ReflectiveCacheMethodDetails{");
        sb.append("method=").append(getMethod());
        sb.append(", annotations=").append(getAnnotations());
        sb.append(", cacheAnnotation=").append(getCacheAnnotation());
        sb.append(", cacheName='").append(getCacheName()).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
