package com.lumm.cache.priority;

import cn.hutool.core.annotation.AnnotationUtil;

import javax.annotation.Priority;
import java.util.Comparator;
import java.util.Objects;


/**
 * 优先级比较器
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
public class PriorityComparator implements Comparator<Object> {

    private static final Class<Priority> PRIORITY_CLASS = Priority.class;

    public static final PriorityComparator INSTANCE = new PriorityComparator();

    @Override
    public int compare(Object o1, Object o2) {
        return compare(o1.getClass(), o2.getClass());
    }

    public static int compare(Class<?> type1, Class<?> type2) {
        if (Objects.equals(type1, type2)) {
            return 0;
        }

        Priority priority1 = AnnotationUtil.getAnnotation(type1, PRIORITY_CLASS);
        Priority priority2 = AnnotationUtil.getAnnotation(type2, PRIORITY_CLASS);

        if (priority1 != null && priority2 != null) {
            return Integer.compare(priority1.value(), priority2.value());
        } else if (priority1 != null && priority2 == null) {
            return -1;
        } else if (priority1 == null && priority2 != null) {
            return 1;
        }
        // else
        return 0;
    }

}
