package com.lumm.securitydata.mp.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.lumm.securitydata.mp.interept.UserDataPermHandler;
import com.lumm.securitydata.mp.interept.UserDataPermInterceptor;
import com.lumm.securitydata.mp.service.DataPermRoleService;
import com.lumm.securitydata.mp.service.DataPermUserService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 数据权限配置类
 *
 * @author zhangj
 * @since 1.0.0
 */
@Configuration
@MapperScan("com.lumm.securitydata.mp.mapper")
public class DataPermConfiguration {

    /**
     * 数据权限用户的角色服务
     */
    @Bean
    @ConditionalOnMissingBean(DataPermRoleService.class)
    public DataPermRoleService dataPermRoleService() {
        return null;
    }

    /**
     * 数据权限用户服务
     */
    @Bean
    @ConditionalOnMissingBean(DataPermUserService.class)
    public DataPermUserService dataPermUserService() {
        return null;
    }


    /**
     * MP拦截器配置
     *
     * @return com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor
     * @author <a href="mailto:brucezhang_jjz@163.com">zhangjun</a>
     * @since 1.0.0
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(DataPermRoleService dataPermRoleService, DataPermUserService dataPermUserService) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加数据权限插件
        UserDataPermInterceptor dataPermissionInterceptor = new UserDataPermInterceptor(
                new UserDataPermHandler(dataPermRoleService, dataPermUserService));
        interceptor.addInnerInterceptor(dataPermissionInterceptor);
        //todo 这边应该是动态的
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

}
