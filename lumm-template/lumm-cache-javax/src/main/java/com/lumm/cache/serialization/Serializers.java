package com.lumm.cache.serialization;


import cn.hutool.core.util.TypeUtil;
import com.lumm.cache.priority.PriorityComparator;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.ServiceLoader.load;

/**
 * 序列化组合器
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
public class Serializers {

    /**
     * 类型映射序列化
     */
    private final Map<Class<?>, List<Serializer>> typedSerializers = new HashMap<>();

    /**
     * 类加载器
     */
    private final ClassLoader classLoader;

    /**
     * 构造
     *
     * @param classLoader 类加载器
     */
    public Serializers(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * 无参构造
     */
    public Serializers() {
        this(Thread.currentThread().getContextClassLoader());
    }

    /**
     * SPI加载实现累
     */
    public void loadSPI() {
        for (Serializer serializer : load(Serializer.class)) {
            Type typeArgument = TypeUtil.getTypeArgument(serializer.getClass());
            Class<?> targetClass = typeArgument == null ? Object.class : TypeUtil.getClass(typeArgument);
            List<Serializer> serializers = typedSerializers.computeIfAbsent(targetClass, k -> new LinkedList());
            serializers.add(serializer);
            serializers.sort(PriorityComparator.INSTANCE);
        }
    }

    /**
     * 获取最接近的序列化
     *
     * @param serializedType 反序列化资源类型
     * @return Serializer
     */
    public Serializer<?> getMostCompatible(Class<?> serializedType) {
        Serializer<?> serializer = getHighestPriority(serializedType);
        if (serializer == null) {
            serializer = getLowestPriority(Object.class);
        }
        return serializer;
    }

    /**
     * 获取优先级最高的序列化实现
     *
     * @param serializedType 序列化资源类型
     * @param <S>            序列化资源类型范型
     * @return Serializer
     */
    public <S> Serializer<S> getHighestPriority(Class<S> serializedType) {
        List<Serializer<S>> serializers = get(serializedType);
        return serializers.isEmpty() ? null : serializers.get(0);
    }

    /**
     * 获取优先级最低的序列化实现
     *
     * @param serializedType 序列化资源类型
     * @param <S>            序列化资源类型范型
     * @return Serializer
     */
    public <S> Serializer<S> getLowestPriority(Class<S> serializedType) {
        List<Serializer<S>> serializers = get(serializedType);
        return serializers.isEmpty() ? null : serializers.get(serializers.size() - 1);
    }

    /**
     * 获取类型对应的所有的序列化实现列表
     *
     * @param serializedType 序列化资源类型
     * @param <S>            序列化资源类型范型
     * @return List
     */
    public <S> List<Serializer<S>> get(Class<S> serializedType) {
        return (List) typedSerializers.getOrDefault(serializedType, emptyList());
    }
}
