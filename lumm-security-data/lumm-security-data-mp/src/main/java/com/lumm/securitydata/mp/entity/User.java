package com.lumm.securitydata.mp.entity;

import lombok.Data;

/**
 * @author zhangj
 * @since 1.0.0
 */
@Data
public class User {
    private Long id;
    private String name;
    private Integer age;
    private String email;
}
