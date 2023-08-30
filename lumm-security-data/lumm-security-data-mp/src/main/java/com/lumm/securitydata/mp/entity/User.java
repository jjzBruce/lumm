package com.lumm.securitydata.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author zhangj
 * @since 1.0.0
 */
@Data
@TableName("`user`")
public class User {
    private Long id;
    private String code;
    private String name;
    private Integer age;
    private String email;
}
