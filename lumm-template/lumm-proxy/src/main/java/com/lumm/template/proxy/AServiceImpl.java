package com.lumm.template.proxy;

/**
 * 服务实现
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangjun</a>
 * @since 1.0.0
 */
public class AServiceImpl implements IService{

    @Override
    public void hello() {
        System.out.println("Hello A");
    }

}
