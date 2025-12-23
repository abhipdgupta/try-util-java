/* (C)2025 */
package io.github.abhipdgupta.tryutil;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A functional-style container for computations that may succeed or fail.
 * <p>
 * This class encapsulates operations that may throw exceptions and allows handling
 * success and failure in a composable and declarative manner.
 * <p>
 * Inspired by {@code io.vavr.control.Try}, it provides:
 * <ul>
 *   <li>Encapsulation of success and failure</li>
 *   <li>Transformation via {@code map} and {@code flatMap}</li>
 *   <li>Safe defaults with {@code getOrElse} and {@code recover}</li>
 *   <li>Side-effect handling via {@code onSuccess} and {@code onFailure}</li>
 * </ul>
 *
 * @param <T> the type of the successful result
 * @author Abhishek pd. gupta
 */
public abstract class Try<T> {
    /**
     * Wraps a computation that may throw an exception into a {@code Try}.
     *
     * @param supplier the computation
     * @param <T>      the type of the result
     * @return a Success with the value or Failure with the exception
     */
    public static <T> Try<T> of(Supplier<T> supplier) {
        try {
            return new Success<>(supplier.get());
        } catch (Throwable t) {
            return new Failure<>(t);
        }
    }

    public static <T> Try<T> ofChecked(ThrowingSupplier<T> supplier) {
        try {
            return new Success<>(supplier.get());
        } catch (Throwable t) {
            return new Failure<>(t);
        }
    }

    /**
     * Utility to rethrow a checked exception without wrapping.
     */
    @SuppressWarnings("unchecked")
    static <E extends Throwable> void sneakyThrow(Throwable t) throws E {
        throw (E) t;
    }

    /**
     * Returns {@code true} if this {@code Try} represents a failed computation.
     *
     * @return true if failure
     */
    public abstract boolean isFailure();

    /**
     * Transforms the successful value using the given function.
     *
     * @param mapper the function to transform the value
     * @param <U>    the type of the transformed value
     * @return a new Try with the transformed value or the original failure
     */
    public abstract <U> Try<U> map(Function<? super T, ? extends U> mapper);

    /**
     * Flat-maps the successful value using a function returning a {@code Try}.
     *
     * @param mapper the function to transform the value
     * @param <U>    the type of the result
     * @return a new Try or the original failure
     */
    public abstract <U> Try<U> flatMap(Function<? super T, Try<U>> mapper);

    /**
     * Returns the successful value or a default if failure.
     *
     * @param other default value
     * @return the value or default
     */
    public abstract T getOrElse(T other);

    /**
     * Recovers from a failure by providing a fallback value.
     *
     * @param recoverFunc the function to provide fallback
     * @return a new Try with fallback or original success
     */
    public abstract Try<T> recover(Function<Throwable, ? extends T> recoverFunc);

    /**
     * Recovers from a failure selectively for a specific exception type.
     *
     * @param exClass     the exception type to recover from
     * @param recoverFunc the function providing fallback
     * @param <E>         type of exception
     * @return a new Try with fallback or original failure
     */
    public abstract <E extends Throwable> Try<T> recover(
            Class<E> exClass, Function<? super E, ? extends T> recoverFunc);

    /**
     * Performs a side-effect action if this is success.
     *
     * @param action the consumer to accept the value
     * @return this Try
     */
    public abstract Try<T> onSuccess(Consumer<? super T> action);

    /**
     * Performs a side-effect action if this is failure.
     *
     * @param action the consumer to accept the exception
     * @return this Try
     */
    public abstract Try<T> onFailure(Consumer<? super Throwable> action);

    /**
     * Returns the value if success, otherwise throws the original exception.
     *
     * @return the value
     */
    public T getOrElseThrow() {
        if (isSuccess()) return get();
        sneakyThrow(getCause());
        return null; // unreachable
    }

    /**
     * Returns {@code true} if this {@code Try} represents a successful computation.
     *
     * @return true if success
     */
    public abstract boolean isSuccess();

    /**
     * Returns the value if success, otherwise throws the original exception.
     *
     * @return the successful value
     * @throws TryException the original exception if this is a failure
     */
    public abstract T get() throws TryException;

    /**
     * Returns the cause of failure if this {@code Try} is a failure.
     *
     * @return the exception cause
     */
    public abstract Throwable getCause();

    /**
     * Returns the value if success, otherwise throws a mapped exception.
     *
     * @param exceptionMapper maps the cause to a new exception
     * @param <X>             type of exception to throw
     * @return the value
     * @throws X mapped exception
     */
    public <X extends Throwable> T getOrElseThrow(Function<Throwable, X> exceptionMapper) throws X {
        if (isSuccess()) {
            try {
                return get();
            } catch (TryException t) {
                throw exceptionMapper.apply(t.getCause());
            }
        }
        throw exceptionMapper.apply(getCause());
    }
}
