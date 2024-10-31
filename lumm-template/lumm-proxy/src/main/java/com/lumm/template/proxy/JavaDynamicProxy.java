package com.lumm.template.proxy;

import javax.security.auth.Subject;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 静态代理
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangjun</a>
 * @since 1.0.0
 */
public class JavaDynamicProxy implements InvocationHandler {
    private IService impl;

    public JavaDynamicProxy(IService impl) {
        this.impl = impl;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("JavaDynamicProxy start");
        Object result = method.invoke(impl, args);
        System.out.println("JavaDynamicProxy end");
        return result;
    }

    public static void main(String[] args) {
        AServiceImpl service = new AServiceImpl();
        IService proxy = (IService) Proxy.newProxyInstance(
                AServiceImpl.class.getClassLoader(),
                new Class[]{IService.class},
                new JavaDynamicProxy(service)
        );
        proxy.hello();
    }
}
