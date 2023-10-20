package com.lumm.cache.interceptor;


import cn.hutool.core.util.TypeUtil;
import com.lumm.cache.interceptor.util.InterceptorUtils;
import com.lumm.cache.priority.Prioritized;
import com.lumm.cache.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.interceptor.*;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import static com.lumm.cache.interceptor.util.InterceptorUtils.INTERCEPTOR_ANNOTATION_TYPE;
import static java.lang.String.format;

/**
 * 拦截器接口 {@link javax.interceptor.Interceptor @Interceptor} 的抽象类
 */
@Slf4j
public abstract class AnnotatedInterceptor<A extends Annotation> implements Interceptor, Prioritized {

    private final InterceptorManager interceptorManager;

    private final Class<A> interceptorBindingType;

    private int priority = Prioritized.NORMAL_PRIORITY;

    /**
     * @throws IllegalArgumentException If the implementation does not annotate {@link Interceptor @Interceptor} or
     *                                  the generic parameter type does not be specified.
     */
    public AnnotatedInterceptor() throws IllegalArgumentException {
        Class<?> interceptorClass = getClass();
        this.interceptorManager = InterceptorManager.getInstance(interceptorClass.getClassLoader());
        this.interceptorManager.registerInterceptorClass(interceptorClass);
        this.interceptorBindingType = resolveInterceptorBindingType(interceptorClass);
        this.interceptorManager.registerInterceptor(this);
    }

    @Override
    public final int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * 拦截操作
     *
     * @param context
     * @return
     * @throws Throwable
     */
    @AroundInvoke
    public Object intercept(InvocationContext context) throws Throwable {
        // 在执行上下文对象的执行方法中找到对应的拦截注解 
        A interceptorBinding = findInterceptorBinding(context.getMethod());
        // 找到对应的拦截注解，执行拦截方法；否则不走拦截
        return interceptorBinding == null ? context.proceed() : intercept(context, interceptorBinding);
    }

    /**
     * Timeout methods are currently specific to Enterprise JavaBeans, although Timer Service functionality
     * may be extended to other specifications in the future, and extension specifications may define events
     * that may be interposed on by around-timeout methods.
     *
     * @param context
     * @return
     * @throws Throwable
     */
    @AroundTimeout
    public Object interceptTimeout(InvocationContext context) throws Throwable {
        A interceptorBinding = findInterceptorBinding(context.getMethod());
        return interceptorBinding == null ? context.proceed() : interceptTimeout(context, interceptorBinding);
    }

    @AroundConstruct
    public void interceptConstruct(InvocationContext context) throws Throwable {
        A interceptorBinding = findInterceptorBinding(context.getConstructor());
        if (interceptorBinding == null) {
            context.proceed();
        } else {
            interceptConstruct(context, interceptorBinding);
        }
    }

    @PostConstruct
    public void interceptPostConstruct(InvocationContext context) throws Throwable {
        beforePostConstruct(context.getTarget(), context.getMethod());
        context.proceed();
        afterPostConstruct(context.getTarget(), context.getMethod());
    }

    @PreDestroy
    public void interceptPreDestroy(InvocationContext context) throws Throwable {
        beforePreDestroy(context.getTarget(), context.getMethod());
        context.proceed();
        afterPreDestroy(context.getTarget(), context.getMethod());
    }

    /**
     * 执行拦截方法，需要子类实现
     *
     * @param context            执行上下文对象
     * @param interceptorBinding 绑定的注解
     * @return the result of {@link InvocationContext#proceed()} method
     * @throws Throwable any exception if occurs
     */
    protected abstract Object intercept(InvocationContext context, A interceptorBinding) throws Throwable;

    /**
     * Executes {@link AroundTimeout @AroundTimeout} method
     *
     * @param context            {@link InvocationContext}
     * @param interceptorBinding the instance of {@link Annotation} annotated by {@link InterceptorBindings}
     * @return the result of {@link InvocationContext#proceed()} method
     * @throws Throwable any exception if occurs
     */
    protected Object interceptTimeout(InvocationContext context, A interceptorBinding) throws Throwable {
        return context.proceed();
    }

    /**
     * Executes {@link AroundConstruct @AroundConstruct} method
     *
     * @param context            {@link InvocationContext}
     * @param interceptorBinding the instance of {@link Annotation} annotated by {@link InterceptorBindings}
     * @throws Throwable any exception if occurs
     */
    protected void interceptConstruct(InvocationContext context, A interceptorBinding) throws Throwable {
        context.proceed();
    }

    /**
     * Before {@link PostConstruct @PostConstruct} interception
     *
     * @param target the intercepted target object
     * @param method the intercepted {@link Method}
     * @throws Throwable any exception if occurs
     */
    protected void beforePostConstruct(Object target, Method method) throws Throwable {
    }

    /**
     * After {@link PostConstruct @PostConstruct} interception
     *
     * @param target the intercepted target object
     * @param method the intercepted {@link Method}
     * @throws Throwable any exception if occurs
     */
    protected void afterPostConstruct(Object target, Method method) throws Throwable {
    }

    /**
     * Before {@link PreDestroy @PreDestroy} interception
     *
     * @param target the intercepted target object
     * @param method the intercepted {@link Method}
     * @throws Throwable any exception if occurs
     */
    protected void beforePreDestroy(Object target, Method method) throws Throwable {
    }

    /**
     * After {@link PreDestroy @PreDestroy} interception
     *
     * @param target the intercepted target object
     * @param method the intercepted {@link Method}
     * @throws Throwable any exception if occurs
     */
    protected void afterPreDestroy(Object target, Method method) throws Throwable {
    }

    /**
     * Get the type of {@link Annotation} annotated by {@link InterceptorBindings}
     *
     * @param interceptorClass
     * @return non-null
     */
    protected Class<A> resolveInterceptorBindingType(Class<?> interceptorClass) {

        Class<A> interceptorBindingType = null;

        TypeUtil.getTypeArguments(interceptorClass);


        for (Class<?> typeArgument : TypeUtils.resolveTypeArguments(interceptorClass)) {
            if (typeArgument.isAnnotation()) {
                Class<A> annotationType = (Class<A>) typeArgument;
                if (isInterceptorBindingType(annotationType)) {
                    interceptorBindingType = annotationType;
                    break;
                } else if (shouldRegisterSyntheticInterceptorBindingType()) {
                    registerSyntheticInterceptorBindingType(annotationType);
                    interceptorBindingType = annotationType;
                    break;
                } else {
                    log.error(format("The annotationType[%s] should annotate %s",
                            typeArgument.getName(),
                            InterceptorBindings.class.getName()));
                }
            }
        }

        validateInterceptorBindingType(interceptorBindingType);

        return interceptorBindingType;
    }

    private boolean isInterceptorBindingType(Class<? extends Annotation> annotationType) {
        return interceptorManager.isInterceptorBindingType(annotationType);
    }

    private void registerSyntheticInterceptorBindingType(Class<A> annotationType) {
        interceptorManager.registerInterceptorBindingType(annotationType);
    }

    protected boolean shouldRegisterSyntheticInterceptorBindingType() {
        return false;
    }

    protected void validateInterceptorBindingType(Class<A> annotationType) {
        if (annotationType == null) {
            String message = format("The interceptor binding annotation is invalid in the Interceptor class[%s]! " +
                            "Please check it that should annotate @%s or be registered as a synthetic interceptor " +
                            "binding type by InterceptorRegistry#registerInterceptorBindingType method!",
                    getClass().getName(), INTERCEPTOR_ANNOTATION_TYPE.getName());
            throw new IllegalArgumentException(message);
        }

        Target target = annotationType.getAnnotation(Target.class);
        ElementType[] elementTypes = target.value();
        if (Arrays.stream(elementTypes).anyMatch(ElementType.TYPE::equals)) {
            if (!getClass().isAnnotationPresent(annotationType)) {
                throw new IllegalArgumentException(format("The @%s must be annotated on the type[%s]!",
                        annotationType.getName(), getClass().getName()));
            }
        }
    }

    /**
     * 在拦截方法中寻找 interceptorBindingType
     *
     * @param method
     * @return
     */
    protected A findInterceptorBinding(Method method) {
        return InterceptorUtils.resolveInterceptorBinding(method, interceptorBindingType);
    }

    protected A findInterceptorBinding(Constructor<?> constructor) {
        return InterceptorUtils.resolveInterceptorBinding(constructor, interceptorBindingType);
    }

    protected Throwable getFailure(Throwable e) {
        Throwable failure = e instanceof InvocationTargetException ? e.getCause() : e;
        while (failure instanceof InvocationTargetException) {
            failure = getFailure(failure);
        }
        return failure;
    }

    public InterceptorManager getInterceptorRegistry() {
        return interceptorManager;
    }

    private boolean excludeInterceptorAnnotation(Annotation annotation) {
        return !INTERCEPTOR_ANNOTATION_TYPE.equals(annotation.annotationType());
    }
}
