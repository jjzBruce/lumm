package com.lumm.java.type;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * TypeVariable
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangjun</a>
 * @since 1.0.0
 */
public class ParameterizedTypeDemo {

    interface A<K extends String, V extends Object> extends Map<K, V> {
        abstract class AEntity<K extends String, V extends Object> implements Map.Entry<K, V> {}
    }

    abstract class ChildA<K extends String, V extends Object> implements A<K, V>, Map<K, V> {}

    public static void main(String[] args) {
        List<ParameterizedType> parameterizedTypes = Arrays.stream(A.class.getGenericInterfaces())
                .filter(type -> type instanceof ParameterizedType)
                .map(ParameterizedType.class::cast)
                .collect(Collectors.toList());
        // parameterizedTypes --> [Map]
        assert 1 == parameterizedTypes.size();
        Type ownerType0 = parameterizedTypes.get(0).getOwnerType();
        assert ownerType0 == null;
        Type rawType0 = parameterizedTypes.get(0).getRawType();
        assert Map.class.equals(rawType0);
        Type[] actualTypeArguments0 = parameterizedTypes.get(0).getActualTypeArguments();
        assert "K".equals(actualTypeArguments0[0].getTypeName());
        assert "V".equals(actualTypeArguments0[1].getTypeName());

        List<ParameterizedType> parameterizedTypes1 = Arrays.stream(A.AEntity.class.getGenericInterfaces())
                .filter(type -> type instanceof ParameterizedType)
                .map(ParameterizedType.class::cast)
                .collect(Collectors.toList());
        // parameterizedTypes --> [Map.Entry]
        assert 1 == parameterizedTypes1.size();
        Type ownerType1 = parameterizedTypes1.get(0).getOwnerType();
        // 内部类所在类是 Map
        assert Map.class.equals(ownerType1);
        Type rawType1 = parameterizedTypes1.get(0).getRawType();
        assert Map.Entry.class.equals(rawType1);
        Type[] actualTypeArguments1 = parameterizedTypes1.get(0).getActualTypeArguments();
        assert "K".equals(actualTypeArguments1[0].getTypeName());
        assert "V".equals(actualTypeArguments1[1].getTypeName());

        List<ParameterizedType> parameterizedTypes2 = Arrays.stream(ChildA.class.getGenericInterfaces())
                .filter(type -> type instanceof ParameterizedType)
                .map(ParameterizedType.class::cast)
                .collect(Collectors.toList());
        // parameterizedTypes --> [A, Map]
        assert 2 == parameterizedTypes2.size();
        assert A.class.equals(parameterizedTypes2.get(0).getOwnerType());
        assert Map.class.equals(parameterizedTypes2.get(1).getOwnerType());
    }

}
