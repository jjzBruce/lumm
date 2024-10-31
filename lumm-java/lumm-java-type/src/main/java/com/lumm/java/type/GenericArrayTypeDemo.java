package com.lumm.java.type;

import java.lang.reflect.*;
import java.util.Arrays;

/**
 * GenericArrayType
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangjun</a>
 * @since 1.0.0
 */
public class GenericArrayTypeDemo {

    public <T> T[] demo() {
        return null;
    }

    public static void main(String[] args) {
        Method[] declaredMethods = GenericArrayTypeDemo.class.getDeclaredMethods();
        Method demo = Arrays.stream(declaredMethods).filter(x -> x.getName().equals("demo")).findFirst().orElse(null);
        Type returnType = demo.getGenericReturnType();
        assert GenericArrayType.class.equals(returnType.getClass());
        GenericArrayType aClass = (GenericArrayType) returnType;
        Type type = aClass.getGenericComponentType();
        assert "T".equals(type.getTypeName());
    }

}
