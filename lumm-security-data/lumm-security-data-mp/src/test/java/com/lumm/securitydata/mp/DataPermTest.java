package com.lumm.securitydata.mp;

import com.lumm.securitydata.mp.entity.User;
import com.lumm.securitydata.mp.mapper.TaskMapper;
import com.lumm.securitydata.mp.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

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
        org.junit.Assert.assertTrue(0 == userList.size());
        userList.forEach(System.out::println);


        /*

        INSERT INTO `task` (id, code, dept_code, create_code) VALUES
                    (1, 'task-1', 'dev', 'Jone'),
                    (2, 'task-2', 'dev', 'Jack'),
                    (3, 'task-3', 'product', 'Tom'),
                    (4, 'task-4', 'product', 'Sandy'),
                    (5, 'task-5', 'product', 'Sandy');

        INSERT INTO `user` (id, code, name, age, email, dept_code, perm_scope) VALUES
                    (1, 'Jone', 'Jone', 18, 'test1@baomidou.com', 'dev', 'DEPT'),
                    (2, 'Jack', 'Jack', 20, 'test2@baomidou.com', 'dev', 'MYSELF'),
                    (3, 'Tom', 'Tom', 28, 'test3@baomidou.com', 'product', 'DEPT'),
                    (4, 'Sandy', 'Sandy', 21, 'test4@baomidou.com', 'product', 'MYSELF'),
                    (5, 'Billie', 'Billie', 24, 'test5@baomidou.com', 'product', 'ALL');


            业务数据：
            ------------------------------------------------------
            -

            Jone - dev - DEPT ->

         */

        // Jone 登录
        mockSession.setCurrentUser();

    }




}
