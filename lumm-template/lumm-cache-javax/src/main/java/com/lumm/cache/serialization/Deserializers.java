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
 * 反序列化组合器
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
public class Deserializers {

    /**
     * 类型映射反序列化
     */
    private final Map<Class<?>, List<Deserializer>> typedDeserializers = new HashMap<>();

    /**
     * 类加载器
     */
    private final ClassLoader classLoader;

    /**
     * 构造
     *
     * @param classLoader 类加载器
     */
    public Deserializers(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * 无参构造
     */
    public Deserializers() {
        this(Thread.currentThread().getContextClassLoader());
    }

    /**
     * SPI加载实现累
     */
    public void loadSPI() {
        for (Deserializer deserializer : load(Deserializer.class)) {
            Type typeArgument = TypeUtil.getTypeArgument(deserializer.getClass());
            Class<?> targetClass = typeArgument == null ? Object.class : TypeUtil.getClass(typeArgument);
            List<Deserializer> deserializers = typedDeserializers.computeIfAbsent(targetClass, k -> new LinkedList());
            deserializers.add(deserializer);
            deserializers.sort(PriorityComparator.INSTANCE);
        }
    }

    /**
     * 获取最接近的反序列化
     *
     * @param deserializedType 反序列化资源类型
     * @return Deserializer
     */
    public Deserializer<?> getMostCompatible(Class<?> deserializedType) {
        Deserializer<?> deserializer = getHighestPriority(deserializedType);
        if (deserializer == null) {
            deserializer = getLowestPriority(Object.class);
        }
        return deserializer;
    }

    /**
     * 获取优先级最高的反序列化实现
     *
     * @param deserializedType 反序列化资源类型
     * @param <T>              反序列化资源类型范型
     * @return Deserializer
     */
    public <T> Deserializer<T> getHighestPriority(Class<?> deserializedType) {
        List<Deserializer<T>> serializers = get(deserializedType);
        return serializers.isEmpty() ? null : serializers.get(0);
    }

    /**
     * 获取优先级最低的反序列化实现
     *
     * @param deserializedType 反序列化资源类型
     * @param <T>              反序列化资源类型范型
     * @return Deserializer
     */
    public <T> Deserializer<T> getLowestPriority(Class<?> deserializedType) {
        List<Deserializer<T>> serializers = get(deserializedType);
        return serializers.isEmpty() ? null : serializers.get(0);
    }

    /**
     * 获取类型对应的所有的反序列化实现列表
     *
     * @param deserializedType 反序列化资源类型
     * @param <T>              反序列化资源类型范型
     * @return List
     */
    public <T> List<Deserializer<T>> get(Class<?> deserializedType) {
        return (List) typedDeserializers.getOrDefault(deserializedType, emptyList());
    }
}
