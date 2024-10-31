package com.lumm.template.proxy;

/**
 * 静态代理
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangjun</a>
 * @since 1.0.0
 */
public class JavaStaticProxy implements IService {

    private IService service;

    public JavaStaticProxy(IService service) {
        this.service = service;
    }

    @Override
    public void hello() {
        System.out.println("JavaStaticProxy start");
        service.hello();
        System.out.println("JavaStaticProxy end");
    }

    public static void main(String[] args) {
        AServiceImpl service = new AServiceImpl();
        IService proxy = new JavaStaticProxy(service);
        proxy.hello();
    }
}
