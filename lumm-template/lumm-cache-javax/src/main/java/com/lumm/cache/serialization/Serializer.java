package com.lumm.cache.serialization;

import java.io.IOException;

/**
 * 序列化
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
public interface Serializer<S> {

    /**
     * 序列化
     *
     * @param source 资源
     */
    byte[] serialize(S source) throws IOException;
}
