package com.lumm.cache.annotation;

import javax.cache.annotation.CacheInvocationParameter;
import javax.cache.annotation.CacheKeyInvocationContext;
import javax.cache.annotation.GeneratedCacheKey;
import java.util.Arrays;
import java.util.Objects;

/**
 * 默认的缓存键接口实现类
 */
class DefaultGeneratedCacheKey implements GeneratedCacheKey {

    /**
     * 参数数组
     */
    private final Object[] parameters;

    /**
     * 构造
     *
     * @param context 缓存键调用上下文
     */
    DefaultGeneratedCacheKey(CacheKeyInvocationContext context) {
        this.parameters = getParameters(context.getKeyParameters());
    }

    /**
     * 获取缓存键参数
     *
     * @param keyParameters 缓存键参数
     * @return
     */
    private Object[] getParameters(CacheInvocationParameter[] keyParameters) {
        int size = keyParameters.length;
        Object[] parameters = new Object[keyParameters.length];
        for (int i = 0; i < size; i++) {
            CacheInvocationParameter keyParameter = keyParameters[i];
            parameters[i] = keyParameter.getValue();
        }
        return parameters;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(parameters);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof DefaultGeneratedCacheKey) {
            return Arrays.deepEquals(this.parameters, ((DefaultGeneratedCacheKey) other).parameters);
        } else {
            return Objects.deepEquals(this.parameters, other);
        }
    }

    @Override
    public String toString() {
        return "DefaultGeneratedCacheKey - " + Arrays.toString(parameters);
    }
}
