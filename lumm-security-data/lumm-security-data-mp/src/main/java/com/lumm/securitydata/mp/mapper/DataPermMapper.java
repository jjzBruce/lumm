package com.lumm.securitydata.mp.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lumm.securitydata.mp.DataPerm;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 用户权限控制Mapper
 *
 * @author zhangj
 * @since 1.0.0
 */
public interface DataPermMapper<T> extends BaseMapper<T> {

    @Override
    @DataPerm
    T selectById(Serializable id);

    @Override
    @DataPerm
    List<T> selectBatchIds(Collection<? extends Serializable> idList);

    @Override
    @DataPerm
    default List<T> selectByMap(Map<String, Object> columnMap) {
        return BaseMapper.super.selectByMap(columnMap);
    }

    @Override
    @DataPerm
    default T selectOne(Wrapper<T> queryWrapper) {
        return BaseMapper.super.selectOne(queryWrapper);
    }

    @Override
    @DataPerm
    default T selectOne(Wrapper<T> queryWrapper, boolean throwEx) {
        return BaseMapper.super.selectOne(queryWrapper, throwEx);
    }

    @Override
    @DataPerm
    Long selectCount(Wrapper<T> queryWrapper);

    @Override
    @DataPerm
    List<T> selectList(Wrapper<T> queryWrapper);

    @Override
    @DataPerm
    List<T> selectList(IPage<T> page, Wrapper<T> queryWrapper);

    @Override
    @DataPerm
    List<Map<String, Object>> selectMaps(Wrapper<T> queryWrapper);

    @Override
    @DataPerm
    List<Map<String, Object>> selectMaps(IPage<? extends Map<String, Object>> page, Wrapper<T> queryWrapper);

    @Override
    @DataPerm
    List<Object> selectObjs(Wrapper<T> queryWrapper);

    @Override
    @DataPerm
    default <P extends IPage<T>> P selectPage(P page, Wrapper<T> queryWrapper) {
        return BaseMapper.super.selectPage(page, queryWrapper);
    }

    @Override
    @DataPerm
    default <P extends IPage<Map<String, Object>>> P selectMapsPage(P page, Wrapper<T> queryWrapper) {
        return BaseMapper.super.selectMapsPage(page, queryWrapper);
    }
}
