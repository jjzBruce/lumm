package com.lumm.template.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * CglibProxy
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangjun</a>
 * @since 1.0.0
 */
public class CglibProxy {

    static class MyInterceptor implements MethodInterceptor {
        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            System.out.println("CglibProxy start");
            Object result = proxy.invokeSuper(obj, args);
            System.out.println("CglibProxy end");
            return result;
        }
    }

    public static void main(String[] args) {
        // 实例化一个增强器
        Enhancer enhancer = new Enhancer();
        // 设置目标类
        enhancer.setSuperclass(AServiceImpl.class);
        // 设置回调类（拦截器）
        enhancer.setCallback(new MyInterceptor());
        // 生成代理类
        IService target = (IService) enhancer.create();
        // 调用代理类的方法
        target.hello();
    }
}
