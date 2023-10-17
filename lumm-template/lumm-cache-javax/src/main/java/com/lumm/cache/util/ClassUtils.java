package com.lumm.cache.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.*;

/**
 * Class工具
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
public abstract class ClassUtils {

    private static final Map<String, Class<?>> PRIMITIVE_TYPE_NAME_MAP;

    static {
        Map<String, Class<?>> typeNamesMap = new HashMap<>(16);
        List<Class<?>> primitiveTypeNames = new ArrayList<>(16);
        primitiveTypeNames.addAll(asList(boolean.class, byte.class, char.class, double.class,
                float.class, int.class, long.class, short.class));
        primitiveTypeNames.addAll(asList(boolean[].class, byte[].class, char[].class, double[].class,
                float[].class, int[].class, long[].class, short[].class));
        for (Class<?> primitiveTypeName : primitiveTypeNames) {
            typeNamesMap.put(primitiveTypeName.getName(), primitiveTypeName);
        }
        PRIMITIVE_TYPE_NAME_MAP = unmodifiableMap(typeNamesMap);
    }

    /**
     * 根据指定类 type 获取其继承层次中的所有继承类。如果提供了过滤器 typeFilters，它可以用于筛选结果集合中的类。
     *
     * @param type         指定类
     * @param includedSelf 是否包含自身
     * @param classFilters 过滤器
     * @return non-null read-only {@link Set}
     */
    public static Set<Class<?>> getAllClasses(Class<?> type, boolean includedSelf, Predicate<Class<?>>... classFilters) {
        // 空或基础类型返回空集合
        if (type == null || type.isPrimitive()) {
            return emptySet();
        }

        List<Class<?>> allClasses = new LinkedList<>();

        // 循环查找父类
        Class<?> superClass = type.getSuperclass();
        while (superClass != null) {
            // add current super class
            allClasses.add(superClass);
            superClass = superClass.getSuperclass();
        }

        // FIFO -> FILO
        Collections.reverse(allClasses);

        if (includedSelf) {
            allClasses.add(type);
        }

        Set<Class<?>> re;

        // Keep the same order from List
        if (ArrayUtil.isNotEmpty(classFilters)) {
            Predicate<Class<?>> filter = classFilters[0];
            for (int i = 1; i < classFilters.length; i++) {
                filter = filter.and(classFilters[i]);
            }
            re = allClasses.stream().filter(filter).collect(Collectors.toSet());
        } else {
            re = new LinkedHashSet<>(allClasses);
        }
        return Collections.unmodifiableSet(re);
    }

    /**
     * 根据指定类 type 获取所有的接口。如果提供了过滤器 typeFilters，它可以用于筛选结果集合中的类。
     *
     * @param type             指定类
     * @param interfaceFilters 类型匹配过滤器
     * @return 非null只读 {@link Set}
     */
    public static List<Class<?>> getAllInterfaces(Class<?> type, Predicate<Class<?>>... interfaceFilters) {
        if (type == null || type.isPrimitive()) {
            return Collections.emptyList();
        }

        List<Class<?>> allInterfaces = new LinkedList<>();
        Set<Class<?>> resolved = new LinkedHashSet<>();
        Queue<Class<?>> waitResolve = new LinkedList<>();

        resolved.add(type);
        Class<?> clazz = type;
        while (clazz != null) {

            Class<?>[] interfaces = clazz.getInterfaces();

            if (ArrayUtil.isNotEmpty(interfaces)) {
                // add current interfaces
                Arrays.stream(interfaces)
                        .filter(resolved::add)
                        .forEach(cls -> {
                            allInterfaces.add(cls);
                            waitResolve.add(cls);
                        });
            }

            // add all super classes to waitResolve
            getAllSuperClasses(clazz)
                    .stream()
                    .filter(resolved::add)
                    .forEach(waitResolve::add);

            clazz = waitResolve.poll();
        }

        // FIFO -> FILO
        Collections.reverse(allInterfaces);


        List<Class<?>> reList = allInterfaces;
        if (ArrayUtil.isNotEmpty(interfaceFilters)) {
            Predicate<Class<?>> predicate = interfaceFilters[0];
            for (int i = 1; i < interfaceFilters.length; i++) {
                predicate = predicate.and(interfaceFilters[i]);
            }
            reList = allInterfaces.stream().filter(predicate).collect(Collectors.toList());
        }

        return Collections.unmodifiableList(reList);
    }

    /**
     * 根据指定类 type 获取其继承层次中的所有继承类和接口。如果提供了过滤器 typeFilters，它可以用于筛选结果集合中的类。
     *
     * @param type        指定类
     * @param typeFilters 类型匹配过滤器
     * @return 非null只读 {@link Set}
     */
    public static Set<Class<?>> getAllInheritedTypes(Class<?> type, Predicate<Class<?>>... typeFilters) {
        // 获取所有的父类
        Set<Class<?>> types = new LinkedHashSet<>(getAllSuperClasses(type, typeFilters));
        // 获取所有的接口
        types.addAll(getAllInterfaces(type, typeFilters));
        return unmodifiableSet(types);
    }

    /**
     * 根据指定类 type 获取所有的父类。如果提供了过滤器 typeFilters，它可以用于筛选结果集合中的类。
     *
     * @param type         指定类
     * @param classFilters 类型匹配过滤器
     * @return 非null只读 {@link Set}
     */
    public static Set<Class<?>> getAllSuperClasses(Class<?> type, Predicate<Class<?>>... classFilters) {
        return getAllClasses(type, false, classFilters);
    }

    public static Class<?> resolveClass(String className, ClassLoader classLoader) {
        Class<?> targetClass = null;
        try {
            targetClass = forName(className, classLoader);
        } catch (Throwable ignored) { // Ignored
        }
        return targetClass;
    }

    public static boolean isPresent(String className, ClassLoader classLoader) {
        try {
            forName(className, classLoader);
        } catch (Throwable ignored) { // Ignored
            return false;
        }
        return true;
    }

    public static Class<?> forName(String className, ClassLoader classLoader) throws ClassNotFoundException {
        Class<?> result = resolvePrimitiveClassName(className);
        if (result != null) {
            return result;
        }

        if (className.endsWith("[]")) {
            String elementClassName = className.substring(0, className.length() - 2);
            Class<?> elementClass = forName(elementClassName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        int internalArrayIndex = className.indexOf("[L");
        if (internalArrayIndex != -1 && className.endsWith(";")) {
            String elementClassName = null;
            if (internalArrayIndex == 0) {
                elementClassName = className.substring(0, className.length() - 1);
            } else if (className.startsWith("[")) {
                elementClassName.substring(1);
            }
            Class<?> elementClass = forName(elementClassName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        ClassLoader cl = classLoader;
        if (cl == null) {
            cl = Thread.currentThread().getContextClassLoader();
        }
        return cl.loadClass(className);
    }

    public static Class<?> resolvePrimitiveClassName(String className) {
        Class<?> result = null;
        if (StrUtil.isNotBlank(className)) {
            result = PRIMITIVE_TYPE_NAME_MAP.get(className);
        }
        return result;
    }


}
