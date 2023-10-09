package com.lumm.cache.annotation;

import com.lumm.cache.annotation.util.CacheAnnotationUtil;

import javax.cache.annotation.CacheInvocationContext;
import javax.cache.annotation.CacheInvocationParameter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * 缓存调用上下文接口实现，基于反射
 */
public class ReflectiveCacheInvocationContext<A extends Annotation> extends ReflectiveCacheMethodDetails<A>
        implements CacheInvocationContext<A> {

    /**
     * 目标值
     */
    private final Object target;

    /**
     * 所有缓存调用入参数组
     */
    private final CacheInvocationParameter[] allParameters;

    /**
     * 构造
     *
     * @param target          目标值
     * @param method          缓存方法
     * @param parameterValues 参数值数组
     */
    public ReflectiveCacheInvocationContext(Object target, Method method, Object... parameterValues) {
        super(method);
        // 校验
        requireNonNull(target, "目标值不能为null");
        requireNonNull(parameterValues, "参数值数组不能为null");
        assertMethodParameterCounts(method, parameterValues);
        // 赋值
        this.target = target;
        this.allParameters = resolveAllParameters(method, parameterValues);
    }

    /**
     * 解析并返回缓存名
     *
     * @return 缓存名
     */
    protected String resolveCacheName() {
        return CacheAnnotationUtil.findCacheName(getCacheAnnotation(), getMethod(), getTarget());
    }

    /**
     * 断言方法参数数量是否与实际参数数量匹配
     *
     * @param method          方法
     * @param parameterValues 参数值数组
     */
    private void assertMethodParameterCounts(Method method, Object[] parameterValues) {
        if (method.getParameterCount() != parameterValues.length) {
            throw new IllegalArgumentException(format("缓存方法的参数数量[%d]与实际参数数量[%d]不匹配！", method.getParameterCount(), parameterValues.length));
        }
    }

    /**
     * 解析并返回所有的参数值数组
     *
     * @param method          缓存方法
     * @param parameterValues 参数值数组
     * @return CacheInvocationParameter[]
     */
    private CacheInvocationParameter[] resolveAllParameters(Method method, Object[] parameterValues) {
        int parameterCount = getMethod().getParameterCount();
        Parameter[] parameters = method.getParameters();
        CacheInvocationParameter[] allParameters = new CacheInvocationParameter[parameterCount];
        for (int index = 0; index < parameterCount; index++) {
            allParameters[index] = new ReflectiveCacheInvocationParameter(parameters[index], index, parameterValues[index]);
        }
        return allParameters;
    }

    @Override
    public Object getTarget() {
        return target;
    }

    @Override
    public CacheInvocationParameter[] getAllParameters() {
        return allParameters;
    }

    @Override
    public Object unwrap(Class cls) {
        Object instance = null;
        try {
            instance = cls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(format("The class[%s] must contain a public non-argument constructor"
                    , cls.getName()));
        }
        return instance;
    }

    @Override
    public String toString() {
        return "ReflectiveCacheInvocationContext{" +
                "target=" + target +
                ", allParameters=" + Arrays.toString(allParameters) +
                "} " + super.toString();
    }
}
