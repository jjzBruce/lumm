package com.lumm.cache.util;

import java.util.function.Predicate;

/**
 * Predicate工具
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
public abstract class PredicateUtils {

    public static <T> Predicate<T> alwaysTrue() {
        return e -> true;
    }

    public static <T> Predicate<T> and(Predicate<? super T>... predicates) {
        Predicate<T> andPredicate = alwaysTrue();
        for (Predicate<? super T> p : predicates) {
            andPredicate = andPredicate.and(p);
        }
        return andPredicate;
    }


}
