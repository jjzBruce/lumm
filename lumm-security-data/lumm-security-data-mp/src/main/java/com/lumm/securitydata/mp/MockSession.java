package com.lumm.securitydata.mp;

import com.lumm.securitydata.mp.entity.User;
import lombok.Data;

/**
 * 模拟当前登录信息
 *
 * @author zhangj
 * @since
 */
@Data
public class MockSession {

    /**
     * 当前登录的用户名
     */
    private User currentUser;

}
