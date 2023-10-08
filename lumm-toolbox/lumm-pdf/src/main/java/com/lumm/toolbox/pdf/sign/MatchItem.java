package com.lumm.toolbox.pdf.sign;

import lombok.Data;

/**
 * Pdf关键字匹配类
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
@Data
public class MatchItem {

    /**
     * 内容
     */
    private String content;

    /**
     * 所在页
     */
    private int pageNum;

    /**
     * 坐标x
     */
    private float x;

    /**
     * 坐标y
     */
    private float y;

}
