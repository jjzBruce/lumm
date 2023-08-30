package com.lumm.securitydata.mp;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.EnumUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据权限范围
 *
 * @author zhangj
 * @since 1.0.0
 */
@AllArgsConstructor
@Getter
public enum DataPermScope {

    /**
     * 全部
     */
    ALL("ALL"),

    /**
     * 部门
     */
    DEPT("DEPT"),

    /**
     * 自己
     */
    MYSELF("MYSELF");

    private String name;

    /**
     * 在一群范围编码找找到最大的范围
     *
     * @param scopeCodes
     * @return com.lumm.securitydata.mp.DataPermScope
     * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
     */
    public static DataPermScope getDataPermScopeFromScopeCodes(Collection<String> scopeCodes) {
        if (CollectionUtil.isEmpty(scopeCodes)) {
            return null;
        } else {
            List<DataPermScope> enumList = scopeCodes.stream().map(x -> EnumUtil.getBy(DataPermScope::getName, x)).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(enumList)) {
                return null;
            }
            if (enumList.stream().anyMatch(x -> x.equals(DataPermScope.ALL))) {
                return ALL;
            } else if (enumList.stream().anyMatch(x -> x.equals(DataPermScope.DEPT))) {
                return DEPT;
            } else {
                return MYSELF;
            }
        }
    }
}
