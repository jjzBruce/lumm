package com.lumm.java.type;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

/**
 * TypeVariable
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangjun</a>
 * @since 1.0.0
 */
public class TypeVariableDemo {

    @Deprecated
    abstract class A<K extends String & Serializable, V extends Object & Serializable> implements Map<K, V> {}

    public static void main(String[] args) {
        TypeVariable<Class<A>>[] typeParameters = A.class.getTypeParameters();
        assert 2 == typeParameters.length;
        // name0 是 K，上界是：String 和 Serializable
        String name0 = typeParameters[0].getName();
        assert "K".equals(name0);
        Type[] bound0s = typeParameters[0].getBounds();
        assert "String".equals(bound0s[0].getTypeName());
        assert "Serializable".equals(bound0s[1].getTypeName());
        // name0 的载体(genericDeclaration)是 A.class
        assert TypeVariableDemo.A.class.equals(typeParameters[0].getGenericDeclaration());
        // name0 绑定注解是 @Deprecated
        assert Deprecated.class.equals(typeParameters[0].getBounds()[0]);

        // name1 是 V，上界是：Object 和 Serializable
        String name1 = typeParameters[0].getName();
        assert "V".equals(name1);
        Type[] bound1s = typeParameters[1].getBounds();
        assert "Object".equals(bound1s[0].getTypeName());
        assert "Serializable".equals(bound1s[1].getTypeName());
        // name1 的载体(genericDeclaration)是 A.class
        assert TypeVariableDemo.A.class.equals(typeParameters[1].getGenericDeclaration());
        // name1 绑定注解是 @Deprecated
        assert Deprecated.class.equals(typeParameters[1].getBounds()[0]);

    }

}
