package com.lumm.toolbox.cert;

import lombok.Data;

/**
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since
 */
@Data
public class Extension {

    private String oid;

    private boolean critical;

    private byte[] value;

}
