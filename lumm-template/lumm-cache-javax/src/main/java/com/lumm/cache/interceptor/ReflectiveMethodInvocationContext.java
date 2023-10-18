package com.lumm.cache.interceptor;

import javax.interceptor.InvocationContext;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * {@link Method} {@link InvocationContext}
 *
 */
public class ReflectiveMethodInvocationContext implements InvocationContext {

    private final Object target;

    private final Method method;

    private Object[] parameters;

    private final Map<String, Object> contextData;

    public ReflectiveMethodInvocationContext(Object target, Method method, Object... parameters) {
        requireNonNull(target, "The target instance must not be null");
        requireNonNull(method, "The method must not be null");
        this.target = target;
        this.method = method;
        this.setParameters(parameters);
        this.contextData = new HashMap<>();
    }

    @Override
    public final Object getTarget() {
        return target;
    }

    @Override
    public final Object getTimer() {
        return null;
    }

    @Override
    public final Method getMethod() {
        return method;
    }

    @Override
    public final Constructor<?> getConstructor() {
        return null;
    }

    @Override
    public final Object[] getParameters() {
        return parameters;
    }

    @Override
    public final void setParameters(Object[] params) {
        this.parameters = params != null ? params : new Object[0];
    }

    @Override
    public final Map<String, Object> getContextData() {
        return contextData;
    }

    @Override
    public Object proceed() throws Exception {
        return method.invoke(getTarget(), getParameters());
    }
}
