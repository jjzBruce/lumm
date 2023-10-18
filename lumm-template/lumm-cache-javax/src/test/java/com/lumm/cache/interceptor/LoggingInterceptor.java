package com.lumm.cache.interceptor;

import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * LoggingInterceptor
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
@Logging
@Interceptor
public class LoggingInterceptor extends AnnotatedInterceptor<Logging> {

    @Override
    protected Object intercept(InvocationContext context, Logging logging) throws Throwable {
        Logger logger = Logger.getLogger(logging.name());
        logger.info((String) context.getParameters()[0]);
        return context.proceed();
    }

    @Override
    protected void beforePostConstruct(Object target, Method method) throws Throwable {
        Logger logger = Logger.getLogger(getClass().getName());
        logger.info("target : " + target.getClass().getName());
        logger.info("method : " + method.getName());
    }
}
