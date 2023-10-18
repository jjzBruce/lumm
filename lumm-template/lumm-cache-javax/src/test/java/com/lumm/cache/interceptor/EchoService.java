package com.lumm.cache.interceptor;

import javax.annotation.PostConstruct;

/**
 * EchoService
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
public class EchoService {

    @Logging
    public String echo(String message) {
        return "回复: " + message;
    }

    @PostConstruct
    public void init() {
        System.out.println("init...");
    }

}
