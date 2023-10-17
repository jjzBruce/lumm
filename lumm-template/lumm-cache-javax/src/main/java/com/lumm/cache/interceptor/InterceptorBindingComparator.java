package com.lumm.cache.interceptor;

import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Annotation;
import java.util.Comparator;

/**
 * The {@link Comparator} of {@link InterceptorBinding Inteceptor Binding} annotation using the
 * {@link String} representing of {@link Annotation#toString() annotation}
 */
class InterceptorBindingComparator implements Comparator<Annotation> {

    final static Comparator<Annotation> INSTANCE = new InterceptorBindingComparator();

    private InterceptorBindingComparator() {
    }

    @Override
    public int compare(Annotation o1, Annotation o2) {
        return o1.toString().compareTo(o2.toString());
    }
}
