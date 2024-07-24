package com.lumm.dynamic.table.utils;



/**
 * Session中的User信息接口
 */
public interface SessionUser {

    /**
     * 用户Id
     */
    Long getId();

    /**
     * 用户名
     */
    default String getUsername() {
        return null;
    }

    /**
     * 获取租户id，默认不存在为null
     */
    default Long getTenantId() {
        return null;
    }

}
