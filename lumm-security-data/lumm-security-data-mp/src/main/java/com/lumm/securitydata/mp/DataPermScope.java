package com.lumm.securitydata.mp;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据权限范围
 *
 * @author zhangj
 * @since 1.0.0
 */
@AllArgsConstructor
@Getter
public enum DataPermScope {

    /**
     * 全部
     */
    ALL("ALL"),

    /**
     * 部门
     */
    DEPT("DEPT"),

    /**
     * 自己
     */
    MYSELF("MYSELF");

    private String name;
}
