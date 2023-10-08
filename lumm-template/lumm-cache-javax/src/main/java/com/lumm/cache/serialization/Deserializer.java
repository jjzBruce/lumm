package com.lumm.cache.serialization;

import java.io.IOException;

/**
 * 反序列
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
public interface Deserializer<T> {

    /**
     * 反序列
     * @param bytes 二进制数据
     */
    T deserialize(byte[] bytes) throws IOException;
}
