package com.lumm.securitydata.mp.service;

import java.util.Collections;
import java.util.List;

/**
 * 数据权限用户服务
 *
 * @author zhangj
 * @since 1.0.0
 */
public class MockDataPermUserServiceImpl implements DataPermUserService {

    /**
     * 获取用户的角色
     *
     * @return Collection
     * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
     * @since 1.0.0
     */
    @Override
    public List<String> currentUserRoleTypes() {
        return Collections.emptyList();
    }

    /**
     * 根据用户id查询部门编码集合
     *
     * @param userId
     * @return java.util.List<java.lang.String>
     * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
     * @since 1.0.0
     */
    @Override
    public List<String> listDeptCodes(Long userId) {
        return Collections.emptyList();
    }

}
