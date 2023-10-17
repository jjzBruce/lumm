package com.lumm.cache.util;

import cn.hutool.core.util.ArrayUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Comparator;

/**
 * 异常工具
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
public abstract class ExceptionUtils {

    public static <T extends Throwable> T wrapThrowable(Throwable throwable, Class<T> exceptionType) {
        String message = throwable.getMessage();
        Throwable cause = throwable.getCause();

        Constructor<?>[] constructors = exceptionType.getConstructors();
        if (ArrayUtil.isEmpty(constructors)) {
            throw new IllegalArgumentException("包装的异常类不能没有构造函数");
        }

        // 按照构造参数从小到达排序
        Arrays.sort(constructors, Comparator.comparingInt(Constructor::getParameterCount));

        // 用排在第一位的构造来构造目标异常
        Constructor constructor = constructors[0];
        Parameter[] parameters = constructor.getParameters();
        Object[] params = new Object[parameters.length];
        // 只关注 cause 和 message 参数
        for (int i = 0; i < parameters.length; i++) {
            if(String.class.isAssignableFrom(parameters[i].getClass())) {
                params[i] = message;
            } else if(Throwable.class.isAssignableFrom(parameters[i].getClass())) {
                params[i] = cause;
            }
        }
        try {
            return (T) constructor.newInstance(parameters);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }


}
