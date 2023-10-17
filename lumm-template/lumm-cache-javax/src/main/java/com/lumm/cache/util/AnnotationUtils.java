package com.lumm.cache.util;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.TypeUtil;
import com.lumm.cache.util.function.ThrowableSupplier;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.Collections.unmodifiableMap;

/**
 * 注解工具类
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
public abstract class AnnotationUtils {

    public final static List<Class<? extends Annotation>> NATIVE_ANNOTATION_TYPES = unmodifiableList(asList
            (Target.class, Retention.class, Documented.class, Inherited.class, Native.class, Repeatable.class));

    private static final Predicate<Method> INHERITED_OBJECT_METHOD_PREDICATE = MethodUtils::isInheritedObjectMethod;

    private static final Predicate<Method> NON_INHERITED_OBJECT_METHOD_PREDICATE = INHERITED_OBJECT_METHOD_PREDICATE.negate();

    private static final Predicate<Method> ANNOTATION_METHOD_PREDICATE = AnnotationUtils::isAnnotationMethod;

    private static final Predicate<Method> NON_ANNOTATION_METHOD_PREDICATE = ANNOTATION_METHOD_PREDICATE.negate();

    /**
     * 判断被注解的类 annotatedElement 是否注解了指定的注解集合 annotationTypes
     *
     * @param annotatedElement 被注解的类
     * @param annotationTypes  指定的注解集合
     * @return
     */
    public static boolean isAnnotationPresent(AnnotatedElement annotatedElement, Iterable<Class<? extends Annotation>> annotationTypes) {
        if (annotatedElement == null || annotationTypes == null) {
            return false;
        }

        boolean annotated = true;
        for (Class<? extends Annotation> annotationType : annotationTypes) {
            if (!isAnnotationPresent(annotatedElement, annotationType)) {
                annotated = false;
                break;
            }
        }
        return annotated;
    }

    /**
     * 判断被注解的类 annotatedElement 是否注解了指定的注解 annotationType
     *
     * @param annotatedElement 被注解的类
     * @param annotationType   指定的注解
     * @return
     */
    public static boolean isAnnotationPresent(AnnotatedElement annotatedElement, Class<? extends Annotation> annotationType) {
        if (annotatedElement == null || annotationType == null) {
            return false;
        }
        // 判断被注解的类 annotatedElement 是否注解了指定的注解 annotationType
        return annotatedElement.isAnnotationPresent(annotationType);
    }

    /**
     * 判断给定的注解 annotationType 是否是元注解。元注解是给定的注解集合 <br/>
     *
     * @param annotationType      给定的注解
     * @param metaAnnotationTypes 元注解类型集合
     * @return
     */
    public static boolean isMetaAnnotation(Class<? extends Annotation> annotationType,
                                           Class<? extends Annotation>... metaAnnotationTypes) {
        return isMetaAnnotation(annotationType, asList(metaAnnotationTypes));
    }

    /**
     * 判断给定的注解 annotation 是否是元注解。元注解是给定的注解集合 <br/>
     *
     * @param annotation          给定的注解
     * @param metaAnnotationTypes 元注解类型集合
     * @return
     */
    public static boolean isMetaAnnotation(Annotation annotation,
                                           Iterable<Class<? extends Annotation>> metaAnnotationTypes) {
        if (annotation == null) {
            return false;
        }
        return isMetaAnnotation(annotation.annotationType(), metaAnnotationTypes);
    }

    /**
     * 判断给定的注解 annotationType 是否是元注解。元注解是给定的注解集合 <br/>
     * <ul>
     *     <li>当给定注解 annotationType 是JDK自带注解不是元注解</li>
     * </ul>
     *
     * @param annotationType      给定的注解类
     * @param metaAnnotationTypes 元注解类型集合
     * @return
     */
    public static boolean isMetaAnnotation(Class<? extends Annotation> annotationType,
                                           Iterable<Class<? extends Annotation>> metaAnnotationTypes) {

        if (NATIVE_ANNOTATION_TYPES.contains(annotationType)) {
            return false;
        }
        // 检查是否有与 annotationType 直接标注的元注解存在。如果存在，就返回 true，表示 annotationType 是元注解。
        if (isAnnotationPresent(annotationType, metaAnnotationTypes)) {
            return true;
        }

        boolean annotated = false;
        for (Annotation annotation : annotationType.getDeclaredAnnotations()) {
            if (isMetaAnnotation(annotation, metaAnnotationTypes)) {
                annotated = true;
                break;
            }
        }

        return annotated;
    }

    /**
     * 在指定被注解了的元素 annotatedElement 中获取指定的注解
     *
     * @param annotatedElement    被注解了的元素
     * @param annotationsToFilter 指定的注解过滤
     * @return non-null read-only {@link List}
     */
    public static List<Annotation> getAllDeclaredAnnotations(AnnotatedElement annotatedElement,
                                                             Predicate<Annotation>... annotationsToFilter) {
        if (annotatedElement instanceof Class) {
            return getAllDeclaredAnnotations((Class) annotatedElement, annotationsToFilter);
        } else {
            return getDeclaredAnnotations(annotatedElement, annotationsToFilter);
        }
    }

    /**
     * 在指定被注解了的类 type 中获取指定的注解
     *
     * @param type                被注解了的类
     * @param annotationsToFilter 指定的注解过滤
     * @return non-null read-only {@link List}
     */
    public static List<Annotation> getAllDeclaredAnnotations(Class<?> type, Predicate<Annotation>... annotationsToFilter) {

        if (type == null) {
            return emptyList();
        }

        List<Annotation> allAnnotations = new LinkedList<>();

        // All types
        Set<Class<?>> allTypes = new LinkedHashSet<>();
        // Add current type
        allTypes.add(type);
        // Add all inherited types


        allTypes.addAll(ClassUtils.getAllInheritedTypes(type, t -> !Object.class.equals(t)));

        for (Class<?> t : allTypes) {
            allAnnotations.addAll(getDeclaredAnnotations(t));
        }

        return StreamUtils.filter(allAnnotations, annotationsToFilter);
    }

    /**
     * 获取一个元素（AnnotatedElement）上直接存在的注解（annotations），而不考虑继承而来的注解。
     *
     * @param annotatedElement    被注解的元素
     * @param annotationsToFilter 过滤器
     * @return non-null read-only {@link List}
     */
    public static List<Annotation> getDeclaredAnnotations(AnnotatedElement annotatedElement,
                                                          Predicate<Annotation>... annotationsToFilter) {
        if (annotatedElement == null) {
            return emptyList();
        }

        return StreamUtils.filter(asList(annotatedElement.getAnnotations()), annotationsToFilter);
    }

    public static Map<String, Object> getAttributesMap(Annotation annotation, Predicate<Method>... attributesToFilter) {
        Map<String, Object> attributesMap = new LinkedHashMap<>();
        getAttributeMethods(annotation, attributesToFilter)
                .forEach(method -> {
                    Object value = ThrowableSupplier.execute(() -> method.invoke(annotation));
                    attributesMap.put(method.getName(), value);
                });
        return attributesMap.isEmpty() ? emptyMap() : unmodifiableMap(attributesMap);
    }

    private static Stream<Method> getAttributeMethods(Annotation annotation, Predicate<Method>... attributesToFilter) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        return Stream.of(annotationType.getMethods())
                .filter(NON_INHERITED_OBJECT_METHOD_PREDICATE
                        .and(NON_ANNOTATION_METHOD_PREDICATE)
                        .and(PredicateUtils.and(attributesToFilter)));
    }

    public static boolean isAnnotationMethod(Method attributeMethod) {
        return attributeMethod != null && Objects.equals(Annotation.class, attributeMethod.getDeclaringClass());
    }


}
