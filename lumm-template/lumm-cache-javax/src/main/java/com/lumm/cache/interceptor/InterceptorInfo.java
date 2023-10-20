package com.lumm.cache.interceptor;


import com.lumm.cache.interceptor.util.InterceptorUtils;
import com.lumm.cache.util.AnnotationUtils;
import com.lumm.cache.util.ClassUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.interceptor.AroundConstruct;
import javax.interceptor.AroundInvoke;
import javax.interceptor.AroundTimeout;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.lang.String.format;

/**
 * 拦截器元信息类
 */
public class InterceptorInfo {

    /**
     * 拦截管理器
     */
    private final InterceptorManager interceptorManager;

    private final Class<?> interceptorClass;

    /**
     * If an interceptor class declared using interceptor bindings has superclasses,
     * interceptor methods declared in the interceptor class’s superclasses are
     * invoked before the interceptor method declared in the interceptor class itself,
     * most general superclass first.
     */
    private final Collection<Method> aroundInvokeMethods;

    private final Collection<Method> aroundTimeoutMethods;

    private final Collection<Method> aroundConstructMethods;

    private final Collection<Method> postConstructMethods;

    private final Collection<Method> preDestroyMethods;

    private final InterceptorBindings interceptorBindings;

    public InterceptorInfo(Class<?> interceptorClass) {
        this.interceptorManager = InterceptorManager.getInstance(interceptorClass.getClassLoader());
        this.interceptorClass = interceptorClass;
        this.aroundInvokeMethods = new LinkedList<>();
        this.aroundTimeoutMethods = new LinkedList<>();
        this.aroundConstructMethods = new LinkedList<>();
        this.postConstructMethods = new LinkedList<>();
        this.preDestroyMethods = new LinkedList<>();
        resolveInterceptionMethods();
        this.interceptorBindings = resolveInterceptorBindings();
    }

    /**
     * 解析拦截方法
     *
     * @throws IllegalStateException
     */
    private void resolveInterceptionMethods() throws IllegalStateException {
        // 获取所有的类，包含自身，不能是Object
        Set<Class<?>> allClasses = ClassUtils.getAllClasses(interceptorClass, true, t -> !Object.class.equals(t));

        for (Class<?> declaringClass : allClasses) {
            Map<Class<? extends Annotation>, Method> interceptionMethods = new HashMap<>();
            for (Method method : declaringClass.getDeclaredMethods()) {
                resolveInterceptionMethod(method, AroundInvoke.class, InterceptorUtils::isAroundInvokeMethod,
                        interceptionMethods, aroundInvokeMethods::add);

                resolveInterceptionMethod(method, AroundTimeout.class, InterceptorUtils::isAroundTimeoutMethod,
                        interceptionMethods, aroundTimeoutMethods::add);

                resolveInterceptionMethod(method, AroundConstruct.class, InterceptorUtils::isAroundConstructMethod,
                        interceptionMethods, aroundConstructMethods::add);

                resolveInterceptionMethod(method, PostConstruct.class, InterceptorUtils::isPostConstructMethod,
                        interceptionMethods, postConstructMethods::add);

                resolveInterceptionMethod(method, PreDestroy.class, InterceptorUtils::isPreDestroyMethod,
                        interceptionMethods, preDestroyMethods::add);
            }
            interceptionMethods.clear();
        }

    }

    private void resolveInterceptionMethod(Method method,
                                           Class<? extends Annotation> annotationType,
                                           Predicate<Method> isInterceptionMethod,
                                           Map<Class<? extends Annotation>, Method> interceptionMethods,
                                           Consumer<Method> interceptionMethodConsumer) {
        if (isInterceptionMethod.test(method)) {
            if (interceptionMethods.putIfAbsent(annotationType, method) == null) {
                interceptionMethodConsumer.accept(method);
            } else {
                throw interceptionMethodDefinitionException(method, annotationType);
            }
        }
    }

    private IllegalStateException interceptionMethodDefinitionException(Method method, Class<? extends Annotation> annotationType) {
        throw new IllegalStateException(format("There is only one @%s method[%s] is declared in the interceptor class[%s]",
                annotationType.getName(), method.toString(), method.getDeclaringClass().getName()));
    }

    private InterceptorBindings resolveInterceptorBindings() {

        return new InterceptorBindings(AnnotationUtils.getAllDeclaredAnnotations(interceptorClass, interceptorManager::isInterceptorBinding));
    }

    Collection<Method> getAroundInvokeMethods() {
        return aroundInvokeMethods;
    }

    Collection<Method> getAroundTimeoutMethods() {
        return aroundTimeoutMethods;
    }

    Collection<Method> getAroundConstructMethods() {
        return aroundConstructMethods;
    }

    Collection<Method> getPostConstructMethods() {
        return postConstructMethods;
    }

    Collection<Method> getPreDestroyMethods() {
        return preDestroyMethods;
    }

    public Class<?> getInterceptorClass() {
        return interceptorClass;
    }

    public boolean hasAroundInvokeMethod() {
        return !getAroundInvokeMethods().isEmpty();
    }

    public boolean hasAroundTimeoutMethod() {
        return !getAroundTimeoutMethods().isEmpty();
    }

    public boolean hasAroundConstructMethod() {
        return !getAroundConstructMethods().isEmpty();
    }

    public boolean hasPostConstructMethod() {
        return !getPostConstructMethods().isEmpty();
    }

    public boolean hasPreDestroyMethod() {
        return !getPreDestroyMethods().isEmpty();
    }


    public InterceptorBindings getInterceptorBindings() {
        return interceptorBindings;
    }

    public Set<Class<? extends Annotation>> getInterceptorBindingTypes() {
        return interceptorBindings.getInterceptorBindingTypes();
    }

    public InterceptorManager getInterceptorRegistry() {
        return interceptorManager;
    }

}
