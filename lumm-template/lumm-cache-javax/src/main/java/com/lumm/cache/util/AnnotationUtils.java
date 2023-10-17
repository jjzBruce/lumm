package com.lumm.cache.util;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

/**
 * 注解工具类
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
public abstract class AnnotationUtils {

    public final static List<Class<? extends Annotation>> NATIVE_ANNOTATION_TYPES = unmodifiableList(asList
            (Target.class, Retention.class, Documented.class, Inherited.class, Native.class, Repeatable.class));

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

}
