package com.lumm.securitydata.mp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lumm.securitydata.mp.MockSession;
import com.lumm.securitydata.mp.entity.User;
import com.lumm.securitydata.mp.mapper.UserMapper;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 数据权限用户服务
 *
 * @author zhangj
 * @since 1.0.0
 */
@AllArgsConstructor
public class MockDataPermUserServiceImpl implements DataPermUserService {

    private MockSession mockSession;
    private UserMapper userMapper;

    /**
     * 获取用户的角色
     *
     * @return Collection
     * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
     * @since 1.0.0
     */
    @Override
    public List<String> listCurrentUserPermScopes() {
        String username = mockSession.getCurrentUsername();
        if (username == null) {
            return Collections.emptyList();
        } else {
            User user = userMapper.selectOne(new QueryWrapper<User>().eq("name", username), false);
            if (user == null) {
                return Collections.emptyList();
            } else {
                return Collections.singletonList(user.getPermScope());
            }
        }
    }

    /**
     * 根据用户id查询部门编码集合
     *
     * @return java.util.List<java.lang.String>
     * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
     * @since 1.0.0
     */
    @Override
    public List<String> listCurrentUserDeptCodes() {
        String username = mockSession.getCurrentUsername();
        if (username == null) {
            return Collections.emptyList();
        } else {
            User user = userMapper.selectOne(new QueryWrapper<User>().eq("name", username), false);
            if (user == null) {
                return Collections.emptyList();
            } else {
                return Collections.singletonList(user.getDeptCode());
            }
        }
    }

}
