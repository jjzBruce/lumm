package com.lumm.cache.annotation;

import com.lumm.cache.annotation.util.CacheAnnotationUtil;

import javax.cache.annotation.CacheInvocationParameter;
import javax.cache.annotation.CacheKey;
import javax.cache.annotation.CacheKeyInvocationContext;
import javax.cache.annotation.CacheValue;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * 缓存键调用上下文接口实现，基于反射
 */
public class ReflectiveCacheKeyInvocationContext<A extends Annotation> extends ReflectiveCacheInvocationContext<A>
        implements CacheKeyInvocationContext<A> {

    /**
     * 缓存值调用参数
     */
    private final CacheInvocationParameter valueParameter;

    /**
     * 缓存键调用参数数组
     */
    private final CacheInvocationParameter[] keyParameters;

    /**
     * 构造
     *
     * @param target          目标值
     * @param method          缓存方法
     * @param parameterValues 参数数组
     */
    public ReflectiveCacheKeyInvocationContext(Object target, Method method, Object... parameterValues) {
        super(target, method, parameterValues);
        CacheInvocationParameter[] allParameters = getAllParameters();
        this.valueParameter = resolveValueParameter(allParameters);
        this.keyParameters = resolveKeyParameters(allParameters);
    }

    /**
     * 解析并返回缓存键参数对象
     *
     * @param parameters
     * @return
     */
    private CacheInvocationParameter[] resolveKeyParameters(CacheInvocationParameter[] parameters) {
        List<CacheInvocationParameter> keyParameters = new LinkedList<>(Arrays.asList(parameters));
        if (valueParameter != null) {
            keyParameters.remove(valueParameter);
        }

        return keyParameters.stream()
                .filter(this::containsCacheKey)
                .toArray(CacheInvocationParameter[]::new);
    }

    /**
     * 解析并返回缓存值参数对象
     *
     * @param parameters 所有的参数
     * @return CacheInvocationParameter
     */
    private CacheInvocationParameter resolveValueParameter(CacheInvocationParameter[] parameters) {
        CacheInvocationParameter valueParameter = null;
        for (CacheInvocationParameter parameter : parameters) {
            if (containsCacheValue(parameter)) {
                valueParameter = parameter;
                break;
            }
        }
        return valueParameter;
    }

    /**
     * 是否包含 CacheKey 注解
     *
     * @param parameter
     * @return
     */
    private boolean containsCacheKey(CacheInvocationParameter parameter) {
        return CacheAnnotationUtil.contains(parameter.getAnnotations(), CacheKey.class);
    }

    /**
     * 是否包含 CacheValue 注解
     *
     * @param parameter
     * @return
     */
    private boolean containsCacheValue(CacheInvocationParameter parameter) {
        return CacheAnnotationUtil.contains(parameter.getAnnotations(), CacheValue.class);
    }

    @Override
    public CacheInvocationParameter[] getKeyParameters() {
        return keyParameters;
    }

    @Override
    public CacheInvocationParameter getValueParameter() {
        return valueParameter;
    }

    @Override
    public String toString() {
        return "ReflectiveCacheKeyInvocationContext{" +
                "valueParameter=" + valueParameter +
                ", keyParameters=" + Arrays.toString(keyParameters) +
                "} " + super.toString();
    }
}
