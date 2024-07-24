package com.lumm.dynamic.table;


import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.lumm.dynamic.table.anno.DynamicTableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 动态用户表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@DynamicTableName(dynamicExpression = "biz_#{#tenant}_#{#tableName}")
public class DynamicUser extends Model<DynamicUser> {

    private Long id;
    private String position;
    private String dept;

}
