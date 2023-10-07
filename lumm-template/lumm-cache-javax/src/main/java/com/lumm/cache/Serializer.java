package com.lumm.cache;

import java.io.IOException;

/**
 * 序列化接口
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
public interface Serializer<S> {

    /**
     * 序列化操作
     *
     * @param source
     * @return
     * @throws IOException
     */
    byte[] serialize(S source) throws IOException;
}
