package com.lumm.cache.annotation;

import javax.cache.annotation.CacheInvocationParameter;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Set;

/**
 * 缓存调用参数实现类，基于反射
 */
public class ReflectiveCacheInvocationParameter implements CacheInvocationParameter, Serializable {

    /**
     * 参数类
     */
    private final Class<?> parameterType;

    /**
     * 参数值
     */
    private final Object parameterValue;

    /**
     * 参数注解集合
     */
    private final Set<Annotation> parameterAnnotations;

    /**
     * 所属下标
     */
    private final int parameterIndex;

    /**
     * 构造
     *
     * @param parameter      参数对象
     * @param parameterIndex 下标
     * @param parameterValue 参数值
     */
    public ReflectiveCacheInvocationParameter(Parameter parameter, int parameterIndex, Object parameterValue) {
        this.parameterType = parameter.getType();
        this.parameterValue = parameterValue;
        this.parameterAnnotations = ReflectiveCacheMethodDetails.getAnnotations(parameter.getAnnotations());
        this.parameterIndex = parameterIndex;
    }

    @Override
    public Class<?> getRawType() {
        return parameterType;
    }

    @Override
    public Object getValue() {
        return parameterValue;
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return parameterAnnotations;
    }

    @Override
    public int getParameterPosition() {
        return parameterIndex;
    }

    @Override
    public String toString() {
        return "ReflectiveCacheInvocationParameter{" +
                "parameterType=" + parameterType +
                ", parameterValue=" + parameterValue +
                ", parameterAnnotations=" + parameterAnnotations +
                ", parameterIndex=" + parameterIndex +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReflectiveCacheInvocationParameter that = (ReflectiveCacheInvocationParameter) o;
        return parameterIndex == that.parameterIndex
                && Objects.equals(parameterType, that.parameterType)
                && Objects.equals(parameterValue, that.parameterValue)
                && Objects.equals(parameterAnnotations, that.parameterAnnotations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameterType, parameterValue, parameterAnnotations, parameterIndex);
    }
}
