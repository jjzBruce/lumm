package com.lumm.cache.interceptor;

import com.lumm.cache.interceptor.util.InterceptorUtils;
import com.lumm.cache.util.AnnotationUtils;

import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;


/**
 * 拦截器绑定元信息
 */
public class InterceptorBindingInfo {

    /**
     * 指定的注解
     */
    private final Annotation declaredAnnotation;

    /**
     * 指定的注解类型
     */
    private final Class<? extends Annotation> declaredAnnotationType;

    /**
     * If <code>true</code>, the declared annotation does not annotate {@link InterceptorBinding}
     */
    private final boolean synthetic;

    /**
     * 属性
     */
    private final Map<String, Object> attributes;

    /**
     * 构造
     *
     * @param declaredAnnotation 指定的注解
     */
    public InterceptorBindingInfo(Annotation declaredAnnotation) {
        this.declaredAnnotation = declaredAnnotation;
        this.declaredAnnotationType = declaredAnnotation.annotationType();
        this.synthetic = !InterceptorUtils.isAnnotatedInterceptorBinding(declaredAnnotationType);
        this.attributes = AnnotationUtils.getAttributesMap(declaredAnnotation, InterceptorBindingAttributeFilter.filters());
    }

    public Annotation getDeclaredAnnotation() {
        return declaredAnnotation;
    }

    public Class<? extends Annotation> getDeclaredAnnotationType() {
        return declaredAnnotationType;
    }

    public boolean isSynthetic() {
        return synthetic;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InterceptorBindingInfo that = (InterceptorBindingInfo) o;
        return synthetic == that.synthetic
                && Objects.equals(declaredAnnotationType, that.declaredAnnotationType)
                && Objects.equals(attributes, that.attributes);
    }

    public boolean equals(Annotation declaredAnnotation) {
        if (declaredAnnotation == null) {
            return false;
        }
        return this.equals(ofMe(declaredAnnotation));
    }

    @Override
    public int hashCode() {
        return Objects.hash(declaredAnnotationType, synthetic, attributes);
    }

    /**
     * 快速创建
     *
     * @param interceptorBinding the instance of {@linkplain InterceptorBinding interceptor binding}
     * @return non-null
     */
    public static InterceptorBindingInfo ofMe(Annotation interceptorBinding) {
        return new InterceptorBindingInfo(interceptorBinding);
    }

}
