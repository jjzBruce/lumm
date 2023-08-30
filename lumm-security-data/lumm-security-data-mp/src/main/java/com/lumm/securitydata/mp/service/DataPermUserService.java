package com.lumm.securitydata.mp.service;

import java.util.List;

/**
 * 数据权限用户服务
 *
 * @author zhangj
 * @since 1.0.0
 */
public interface DataPermUserService {

    /**
     * 获取用户的角色
     *
     * @return Collection
     * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
     * @since 1.0.0
     */
    List<String> listCurrentUserPermScopes();

    /**
     * 查询当前用户的部门编码集合
     *
     * @return java.util.List<java.lang.String>
     * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
     * @since  1.0.0
     */
    List<String> listCurrentUserDeptCodes();

}
