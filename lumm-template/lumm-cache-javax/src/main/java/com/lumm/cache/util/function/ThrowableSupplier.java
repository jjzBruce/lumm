package com.lumm.cache.util.function;

import com.lumm.cache.util.ExceptionUtils;

/**
 * 可以抛异常的函数
 *
 * @param <T>
 */
@FunctionalInterface
public interface ThrowableSupplier<T> {

    /**
     * Applies this function to the given argument.
     *
     * @return the supplied result
     * @throws Throwable if met with any error
     */
    T get() throws Throwable;

    /**
     * Executes {@link ThrowableSupplier}
     *
     * @param supplier {@link ThrowableSupplier}
     * @param <T>      the supplied type
     * @return the result after execution
     * @throws RuntimeException
     */
    static <T> T execute(ThrowableSupplier<T> supplier) throws RuntimeException {
        return execute(supplier, RuntimeException.class);
    }

    static <T, E extends Throwable> T execute(ThrowableSupplier<T> supplier, Class<E> errorType) throws E {
        T result = null;
        try {
            result = supplier.get();
        } catch (Throwable e) {
            throw ExceptionUtils.wrapThrowable(e, errorType);
        }
        return result;
    }
}

