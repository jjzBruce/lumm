package com.lumm.dynamic.table.utils;



import com.alibaba.ttl.TransmittableThreadLocal;

import java.io.Serializable;

/**
 * 登录用户上下文
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
public abstract class UserContext implements Serializable {

    /**
     * 保存线程变量
     */
    public static ThreadLocal local = new TransmittableThreadLocal();

    /**
     * 获取当前用户
     */
    public static <T extends SessionUser> T getUser() {
        Object t = local.get();
        return t != null ? (T) t : null;
    }

    /**
     * 设置当前用户
     */
    public static <T extends SessionUser> void setUser(T user) {
        local.set(user);
    }

    /**
     * 清除当前用户
     */
    public static void clear() {
        local.remove();
    }

    /**
     * 获取用户Id
     */
    public static Long getUserId() {
        return getUser() == null ? null : getUser().getId();
    }

    /**
     * 获取租户Id
     */
    public static Long getTenantId() {
        return getUser() == null ? -1 : getUser().getTenantId();
    }

}
