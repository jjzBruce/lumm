package com.lumm.securitydata.mp.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

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
    Set<String> currentUserRoleTypes();

    /**
     * 根据用户id查询部门编码集合
     *
     * @param userId
     * @return java.util.List<java.lang.String>
     * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
     * @since  1.0.0
     */
    List<String> listDeptCodes(Long userId);

}
