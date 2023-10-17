package com.lumm.cache.util;

import cn.hutool.core.collection.CollUtil;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.Set;

/**
 * 方法工具
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
public abstract class MethodUtils {

    public final static Set<Method> OBJECT_METHODS = CollUtil.newHashSet(Object.class.getMethods());

    public static boolean isInheritedObjectMethod(Method attributeMethod) {
        boolean inherited = false;

        for (Method method : OBJECT_METHODS) {
            if (overrides(attributeMethod, method)) {
                inherited = true;
                break;
            }
        }

        return inherited;
    }

    public static boolean isStatic(Member member) {
        return member != null && Modifier.isStatic(member.getModifiers());
    }

    public static boolean isPrivate(Member member) {
        return member != null && Modifier.isPrivate(member.getModifiers());
    }

    public static boolean overrides(Method overrider, Method overridden) {

        if (overrider == null || overridden == null) {
            return false;
        }

        // equality comparison: If two methods are same
        if (Objects.equals(overrider, overridden)) {
            return false;
        }

        // Modifiers comparison: Any method must be non-static method
        if (isStatic(overrider) || isStatic(overridden)) { //
            return false;
        }

        // Modifiers comparison: the accessibility of any method must not be private
        if (isPrivate(overrider) || isPrivate(overridden)) {
            return false;
        }

        // Inheritance comparison: The declaring class of overrider must be inherit from the overridden's
        if (!overridden.getDeclaringClass().isAssignableFrom(overrider.getDeclaringClass())) {
            return false;
        }

        // Method comparison: must not be "default" method
        if (overrider.isDefault()) {
            return false;
        }

        // Method comparison: The method name must be equal
        if (!Objects.equals(overrider.getName(), overridden.getName())) {
            return false;
        }

        // Method comparison: The count of method parameters must be equal
        if (!Objects.equals(overrider.getParameterCount(), overridden.getParameterCount())) {
            return false;
        }

        // Method comparison: Any parameter type of overrider must equal the overridden's
        for (int i = 0; i < overrider.getParameterCount(); i++) {
            if (!Objects.equals(overridden.getParameterTypes()[i], overrider.getParameterTypes()[i])) {
                return false;
            }
        }

        // Method comparison: The return type of overrider must be inherit from the overridden's
        if (!overridden.getReturnType().isAssignableFrom(overrider.getReturnType())) {
            return false;
        }

        // Throwable comparison: "throws" Throwable list will be ignored, trust the compiler verify

        return true;
    }


}
