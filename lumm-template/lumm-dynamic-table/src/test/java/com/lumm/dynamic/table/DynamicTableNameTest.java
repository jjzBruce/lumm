package com.lumm.dynamic.table;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.lumm.dynamic.table.config.OrmMpConfiguration;
import com.lumm.dynamic.table.config.TestConfig;
import com.lumm.dynamic.table.service.DynamicUserService;
import com.lumm.dynamic.table.utils.IOUtils;
import com.lumm.dynamic.table.utils.SessionUser;
import com.lumm.dynamic.table.utils.UserContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

/**
 * Service 分页测试
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
@Slf4j
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-dynamic.properties")
@ContextConfiguration(classes = {
        // 数据源自动装配
        DataSourceAutoConfiguration.class,
        // Mybatis 相关自动装配
        MybatisPlusAutoConfiguration.class,
        OrmMpConfiguration.class,
        // 测试配置
        TestConfig.class,
})
@SpringBootTest(classes = {DynamicTableNameTest.class})
public class DynamicTableNameTest {

    @Autowired
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    protected DynamicUserService dynamicUserService;

    private void readAndExecSql(String path) {
        InputStream inputStream = null;
        try {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            byte[] bytes = IOUtils.readInputStream(inputStream);
            String ddlStatements = new String(bytes);
            // 逐行读取
            BufferedReader reader = new BufferedReader(new StringReader(ddlStatements));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                sb.append(line).append("\n");
                if (line.endsWith(";")) {
                    log.info("sql exec: {}", sb);
                    jdbcTemplate.execute(sb.toString());
                    sb.setLength(0);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeSilently(inputStream);
        }
    }

    @Before
    public void init() {
        readAndExecSql("db/dynamic/h2.create.sql");
    }

    @After
    public void destroy() {
        readAndExecSql("db/dynamic/h2.drop.sql");
    }

    @Data
    class MockSessionUser implements SessionUser {
        private Long id;
        private Long tenantId;
    }

    @Test
    public void testDynamicTableName() {
        MockSessionUser mockSessionUser = new MockSessionUser();
        mockSessionUser.setId(1L);
        mockSessionUser.setTenantId(1L);
        UserContext.setUser(mockSessionUser);

        List<DynamicUser> list = dynamicUserService.list();
        Assert.assertEquals(2, list.size());


        MockSessionUser mockSessionUser2 = new MockSessionUser();
        mockSessionUser2.setId(2L);
        mockSessionUser2.setTenantId(37L);
        UserContext.setUser(mockSessionUser2);

        List<DynamicUser> list2 = dynamicUserService.list();
        Assert.assertEquals(1, list2.size());
    }

}
