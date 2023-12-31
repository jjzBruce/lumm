package com.lumm.cache.interceptor;

import org.junit.Test;

import java.lang.reflect.Method;

/**
 * 测试链式调用
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
public class ChainableInvocationContextTest {

    @Test
    public void test() throws Exception {
        EchoService echoService = new EchoService();
        Method method = EchoService.class.getMethod("echo", String.class);
        ReflectiveMethodInvocationContext delegateContext = 
                new ReflectiveMethodInvocationContext(echoService, method, "Hello,World");

        ChainableInvocationContext context = new ChainableInvocationContext(delegateContext, Interceptor.loadInterceptors());

        Object result = context.proceed();
        System.out.println(result);

    }
    

}
