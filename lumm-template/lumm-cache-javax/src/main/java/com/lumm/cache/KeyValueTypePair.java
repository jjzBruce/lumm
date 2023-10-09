package com.lumm.cache;


import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.TypeUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * 键值类型对
 */
@Slf4j
public class KeyValueTypePair {

    /**
     * 键的类型
     */
    @Getter
    private final Class<?> keyType;

    /**
     * 值的类型
     */
    @Getter
    private final Class<?> valueType;

    /**
     * 构造
     */
    public KeyValueTypePair(Class<?> keyType, Class<?> valueType) {
        this.keyType = keyType;
        this.valueType = valueType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        KeyValueTypePair that = (KeyValueTypePair) o;

        if (!Objects.equals(keyType, that.keyType)) {
            return false;
        }
        return Objects.equals(valueType, that.valueType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyType, valueType);
    }

    /**
     * 解析缓存实现
     * @param targetClass
     * @return
     */
    public static KeyValueTypePair resolve(Class<?> targetClass) {
        // 断言：必须是实现类
        assertCache(targetClass);
        // 查找Class的参数类型
        Type[] typeArguments = TypeUtil.getTypeArguments(targetClass);
        if (ArrayUtil.length(typeArguments) == 2) {
            return new KeyValueTypePair(TypeUtil.getClass(typeArguments[0]), TypeUtil.getClass(typeArguments[1]));
        } else {
            log.error("无法在类[{}]中找到键值对应的类信息", targetClass);
            return null;
        }
    }

    /**
     * 验证缓存类是否争取
     *
     * @param cacheClass
     */
    public static void assertCache(Class<?> cacheClass) {
        if (cacheClass.isInterface()) {
            throw new IllegalArgumentException("实现类不能是接口！");
        }
        if (Modifier.isAbstract(cacheClass.getModifiers())) {
            throw new IllegalArgumentException("实现类不能是抽象类！");
        }
    }

}
