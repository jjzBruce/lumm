package com.lumm.securitydata.mp;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

/**
 * 数据权限下角色枚举
 *
 * @author zhangj
 * @since 1.0.0
 */
@AllArgsConstructor
@Getter
public enum DataPermRole {

    DATA_MANAGER("数据管理员", "DATA_MANAGER", DataPermScope.ALL),
    DATA_AUDITOR("数据审核员", "DATA_AUDITOR", DataPermScope.DEPT),
    DATA_OPERATOR("数据业务员", "DATA_OPERATOR", DataPermScope.MYSELF);

    private String name;
    private String code;
    private DataPermScope scope;


    public static String getName(String code) {
        for (DataPermRole type : DataPermRole.values()) {
            if (type.getCode().equals(code)) {
                return type.getName();
            }
        }
        return null;
    }

    public static String getCode(String name) {
        for (DataPermRole type : DataPermRole.values()) {
            if (type.getName().equals(name)) {
                return type.getCode();
            }
        }
        return null;
    }

    public static DataPermScope getScope(Collection<String> code) {
        for (DataPermRole type : DataPermRole.values()) {
            for (String v : code) {
                if (type.getCode().equals(v)) {
                    return type.getScope();
                }
            }
        }
        return DataPermScope.MYSELF;
    }
}
