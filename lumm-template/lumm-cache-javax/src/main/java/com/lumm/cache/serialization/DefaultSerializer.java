package com.lumm.cache.serialization;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 默认的序列化实现
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
public class DefaultSerializer implements Serializer<Object> {

    @Override
    public byte[] serialize(Object source) throws IOException {
        byte[] bytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            // Key -> byte[]
            objectOutputStream.writeObject(source);
            bytes = outputStream.toByteArray();
        }
        return bytes;
    }
}

