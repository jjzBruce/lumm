package com.lumm.java.type;

import java.lang.reflect.*;
import java.util.Arrays;

/**
 * WildcardType
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangjun</a>
 * @since 1.0.0
 */
public class WildcardTypeDemo {

    public Class<? extends Object> demo() {
        return null;
    }

    public static void main(String[] args) {
        Method[] declaredMethods = WildcardTypeDemo.class.getDeclaredMethods();
        Method demo = Arrays.stream(declaredMethods).filter(x -> x.getName().equals("demo")).findFirst().orElse(null);

        Type returnType = demo.getGenericReturnType();
        ParameterizedType parameterizedType = (ParameterizedType) returnType;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        Type returnArgType0 = actualTypeArguments[0];
        WildcardType returnArgWildcardType0 = (WildcardType) returnArgType0;
        // upperBounds --> Object
        Type[] upperBounds = returnArgWildcardType0.getUpperBounds();
        assert Object.class.equals(upperBounds[0]);
    }


}
