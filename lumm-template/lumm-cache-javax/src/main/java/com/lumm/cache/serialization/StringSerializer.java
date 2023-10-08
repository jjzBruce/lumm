package com.lumm.cache.serialization;

import java.nio.charset.StandardCharsets;

/**
 * 字符串序列化
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
public class StringSerializer implements Serializer<String> {

    @Override
    public byte[] serialize(String source) {
        return source.getBytes(StandardCharsets.UTF_8);
    }
}
