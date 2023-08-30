package com.lumm.securitydata.mp.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.lumm.securitydata.mp.MockSession;
import com.lumm.securitydata.mp.interept.DataPermHandler;
import com.lumm.securitydata.mp.interept.DataPermInterceptor;
import com.lumm.securitydata.mp.mapper.UserMapper;
import com.lumm.securitydata.mp.service.DataPermUserService;
import com.lumm.securitydata.mp.service.MockDataPermUserServiceImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * 数据权限配置类
 *
 * @author zhangj
 * @since 1.0.0
 */
@Configuration
@ComponentScan("com.lumm.securitydata.mp")
@MapperScan("com.lumm.securitydata.mp.mapper")
public class DataPermConfiguration {

    /**
     * 模拟的当前登录用户
     */
    @Bean
    public MockSession mockSession() {
        return new MockSession();
    }

    /**
     * 数据权限用户服务
     */
    @Bean
    @ConditionalOnMissingBean(DataPermUserService.class)
    public DataPermUserService dataPermUserService(MockSession mockSession, @Lazy UserMapper userMapper) {
        return new MockDataPermUserServiceImpl(mockSession, userMapper);
    }


    /**
     * MP拦截器配置
     *
     * @return com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor
     * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
     * @since 1.0.0
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(DataPermUserService dataPermUserService, MockSession mockSession) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加数据权限插件
        DataPermInterceptor dataPermissionInterceptor = new DataPermInterceptor(
                new DataPermHandler(dataPermUserService, mockSession));
        interceptor.addInnerInterceptor(dataPermissionInterceptor);
        //todo 这边应该是动态的
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

}
