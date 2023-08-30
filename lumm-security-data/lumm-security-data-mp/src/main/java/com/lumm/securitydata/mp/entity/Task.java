package com.lumm.securitydata.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author zhangj
 * @since 1.0.0
 */
@Data
@TableName("`task`")
public class Task {
    private Long id;
    private String code;
    private String deptCode;
    private String createCode;
}
