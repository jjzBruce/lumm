package com.lumm.cache.serialization;

import java.nio.charset.StandardCharsets;

/**
 * 字符串反序列化
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
public class StringDeserializer implements Deserializer<String> {

    @Override
    public String deserialize(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
