package com.lumm.cache.interceptor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 包装形成链式 {@link InvocationContext}
 */
public class ChainableInvocationContext implements InvocationContext {

    /**
     * 委托实现
     */
    private final InvocationContext delegateContext;

    private final List<Object> interceptors; // @Interceptor class instances

    private final int size;

    private final InterceptorManager interceptorManager;

    private int pos; // position

    /**
     * 构造
     *
     * @param delegateContext     委托实现
     * @param defaultInterceptors 默认拦截
     */
    public ChainableInvocationContext(InvocationContext delegateContext, Object... defaultInterceptors) {
        this.delegateContext = delegateContext;
        this.interceptorManager = InterceptorManager.getInstance(resolveClassLoader(defaultInterceptors));
        this.interceptors = resolveInterceptors(defaultInterceptors);
        this.size = this.interceptors.size();
        this.pos = 0;
    }

    /**
     * 返回目标实例。
     *
     * @return Object
     */
    @Override
    public Object getTarget() {
        return delegateContext.getTarget();
    }

    /**
     * 获取目标对象执行方法的计时器对象
     *
     * @return Object
     */
    @Override
    public Object getTimer() {
        return delegateContext.getTimer();
    }

    /**
     * 获取目标对象执行方法
     *
     * @return Method
     */
    @Override
    public Method getMethod() {
        return delegateContext.getMethod();
    }

    /**
     * 获取目标对象的构造方法
     *
     * @return Constructor<?>
     */
    @Override
    public Constructor<?> getConstructor() {
        return delegateContext.getConstructor();
    }

    /**
     * 获取目标方法的执行参数
     *
     * @return Object[]
     */
    @Override 
    public Object[] getParameters() {
        return delegateContext.getParameters();
    }

    @Override
    public void setParameters(Object[] params) {
        delegateContext.setParameters(params);
    }

    @Override
    public Map<String, Object> getContextData() {
        return delegateContext.getContextData();
    }

    @Override
    public Object proceed() throws Exception {
        if (pos < size) {
            int currentPos = pos++;
            Object interceptor = interceptors.get(currentPos);
            Collection<Method> interceptionMethods = resolveInterceptionMethods(interceptor);
            Object result = null;
            for (Method interceptionMethod : interceptionMethods) {
                result = interceptionMethod.invoke(interceptor, this);
            }
            return result;
        } else {
            return delegateContext.proceed();
        }
    }

    private ClassLoader resolveClassLoader(Object[] interceptors) {
        Object target = interceptors.length > 0 ? interceptors[0] : this;
        return target.getClass().getClassLoader();
    }

    /**
     * 解析拦截器
     *
     * @param defaultInterceptors
     * @return
     */
    private List<Object> resolveInterceptors(Object[] defaultInterceptors) {
        Method method = getMethod();
        if (method != null) {
            return interceptorManager.resolveInterceptors(method, defaultInterceptors);
        }
        return interceptorManager.resolveInterceptors(getConstructor(), defaultInterceptors);
    }


    private Collection<Method> resolveInterceptionMethods(Object interceptor) {
        InterceptorInfo interceptorInfo = interceptorManager.getInterceptorInfo(interceptor.getClass());

        if (interceptorInfo == null) { // interceptor may be a default(external) Interceptor
            interceptorInfo = new InterceptorInfo(interceptor.getClass());
        }

        final Collection<Method> interceptionMethods;  // nerver null

        if (getTimer() != null) { // If the "Timer" is present
            interceptionMethods = interceptorInfo.getAroundTimeoutMethods();
        } else if (getConstructor() != null) { // If the "Constructor" should be intercepted
            interceptionMethods = interceptorInfo.getAroundConstructMethods();
        } else {
            Method method = getMethod();
            if (method.isAnnotationPresent(PostConstruct.class)) {
                interceptionMethods = interceptorInfo.getPostConstructMethods();
            } else if (method.isAnnotationPresent(PreDestroy.class)) {
                interceptionMethods = interceptorInfo.getPreDestroyMethods();
            } else {
                interceptionMethods = interceptorInfo.getAroundInvokeMethods();
            }
        }

        return interceptionMethods;
    }
}
