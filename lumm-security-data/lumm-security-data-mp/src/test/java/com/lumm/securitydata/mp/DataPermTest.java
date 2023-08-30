package com.lumm.securitydata.mp;

import com.lumm.securitydata.mp.entity.User;
import com.lumm.securitydata.mp.mapper.TaskMapper;
import com.lumm.securitydata.mp.mapper.UserMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * MpTest
 *
 * @author zhangj
 * @since 1.0.0
 */
@SpringBootTest
public class DataPermTest {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private MockSession mockSession;


    /**
     * 无登录用户下的查询
     */
    @Test
    public void test() {
        // 无登录信息下查不到任何数据
        List<User> userList = userMapper.selectList(null);
        Assert.assertEquals(5, userList.size());


        /*
        业务数据：
            dev:
                Jone:
                    task-1
                Jack:
                    task-2
            product:
                Tom:
                    task-3
                Sandy:
                    task-4
                    task-5

         用户的数据权限：
            Jone(DEPT: dev):        task-1, task-2
            Jack(MYSELF: dev):      task-2
            Tom(DEPT: product):     task-3, task-4, task-5
            Sandy(MYSELF: product): task-4, task-5
            Billie(ALL: product):   task-1, task-2, task-3, task-4, task-5

         */

        // Jone 登录
        mockSession.setCurrentUsername("Jone");
        Assert.assertEquals(2, taskMapper.selectList(null).size());
        // Jack 登录
        mockSession.setCurrentUsername("Jack");
        Assert.assertEquals(1, taskMapper.selectList(null).size());
        // Tom 登录
        mockSession.setCurrentUsername("Tom");
        Assert.assertEquals(3, taskMapper.selectList(null).size());
        // Sandy 登录
        mockSession.setCurrentUsername("Sandy");
        Assert.assertEquals(2, taskMapper.selectList(null).size());
        // Billie 登录
        mockSession.setCurrentUsername("Billie");
        Assert.assertEquals(5, taskMapper.selectList(null).size());


    }


}
