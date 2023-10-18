package com.lumm.cache.interceptor;

import javax.annotation.PostConstruct;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.util.logging.Logger;

/**
 * ExtLoggingInterceptor
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
@Logging
@Interceptor
public class ExtLoggingInterceptor extends LoggingInterceptor {

    @PostConstruct
    public void postConstruct(InvocationContext context) throws Exception {
        Logger logger = Logger.getLogger(getClass().getName());
        logger.info("postConstruct");
    }

}
