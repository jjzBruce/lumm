package com.lumm.cache.priority;

import java.util.Comparator;

import static java.lang.Integer.compare;

/**
 * 优先级接口，继承了比较接口
 *
 * @since 1.0.0
 */
public interface Prioritized extends Comparable<Prioritized> {

    Comparator<Object> COMPARATOR = (one, two) -> {
        boolean b1 = one instanceof Prioritized;
        boolean b2 = two instanceof Prioritized;
        if (b1 && !b2) {        // one is Prioritized, two is not
            return -1;
        } else if (b2 && !b1) { // two is Prioritized, one is not
            return 1;
        } else if (b1 && b2) {  //  one and two both are Prioritized
            return ((Prioritized) one).compareTo((Prioritized) two);
        } else {                // Try to use @Priority Comparator
            return PriorityComparator.INSTANCE.compare(one, two);
        }
    };

    /**
     * 最大优先级
     */
    int MAX_PRIORITY = Integer.MIN_VALUE;

    /**
     * 最小育先机
     */
    int MIN_PRIORITY = Integer.MAX_VALUE;

    /**
     * 正常优先级
     */
    int NORMAL_PRIORITY = 0;

    /**
     * 获取优先级
     */
    default int getPriority() {
        return NORMAL_PRIORITY;
    }

    @Override
    default int compareTo(Prioritized that) {
        return compare(this.getPriority(), that.getPriority());
    }
}
