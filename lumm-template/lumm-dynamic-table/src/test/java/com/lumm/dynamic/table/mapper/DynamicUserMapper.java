package com.lumm.dynamic.table.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lumm.dynamic.table.DynamicUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface DynamicUserMapper extends BaseMapper<DynamicUser> {
}
