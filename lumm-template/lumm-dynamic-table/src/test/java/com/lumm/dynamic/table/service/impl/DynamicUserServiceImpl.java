package com.lumm.dynamic.table.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lumm.dynamic.table.DynamicUser;
import com.lumm.dynamic.table.mapper.DynamicUserMapper;
import com.lumm.dynamic.table.service.DynamicUserService;
import org.springframework.stereotype.Service;

@Service
public class DynamicUserServiceImpl extends ServiceImpl<DynamicUserMapper, DynamicUser>
        implements DynamicUserService {
}
