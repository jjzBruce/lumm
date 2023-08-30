package com.lumm.securitydata.mp;

import com.lumm.securitydata.mp.entity.User;
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

    @Test
    public void testSelectWihDataPerm() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userMapper.selectList(null);
        org.junit.Assert.assertTrue(0 == userList.size());
        userList.forEach(System.out::println);
    }

}
