package com.lumm.cache.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

/**
 * Stream工具
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
public abstract class StreamUtils {

    public static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public static <T, I extends Iterable<T>> Stream<T> filterStream(I values, Predicate<? super T> predicate) {
        return stream(values).filter(predicate);
    }

    public static <E, L extends List<E>> List<E> filter(L values, Predicate<? super E> predicate) {
        final L result;
        if (predicate == null) {
            result = values;
        } else {
            result = (L) filterStream(values, predicate).collect(toList());
        }
        return unmodifiableList(result);
    }

    public static <E, L extends List<E>> List<E> filter(L values, Predicate<? super E>... predicates) {
        Predicate filter = null;
        if (ArrayUtil.isNotEmpty(predicates)) {
            filter = predicates[0];
            for (int i = 1; i < predicates.length; i++) {
                filter = filter.and(predicates[i]);
            }
        }

        return filter(values, filter);
    }

    public static <E, S extends Set<E>> Set<E> filter(S values, Predicate<? super E> predicate) {
        final S result;
        if (predicate == null) {
            result = values;
        } else {
            result = (S) filterStream(values, predicate).collect(Collectors.toSet());
        }
        return Collections.unmodifiableSet(result);
    }

}
