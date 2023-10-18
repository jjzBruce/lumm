package com.lumm.cache.interceptor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * MonitoringInterceptor
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
@Monitored
@Interceptor
public class MonitoringInterceptor {

    @AroundInvoke
    public Object monitorInvocation(InvocationContext context) throws Exception {
        return context.proceed();
    }

}
