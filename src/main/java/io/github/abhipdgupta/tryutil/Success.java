/* (C)2025 */
package io.github.abhipdgupta.tryutil;

import java.util.function.Consumer;
import java.util.function.Function;

final class Success<T> extends Try<T> {
    private final T value;

    Success(T value) {
        this.value = value;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public boolean isFailure() {
        return false;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public Throwable getCause() {
        throw new IllegalStateException("Success has no cause");
    }

    @Override
    public <U> Try<U> map(Function<? super T, ? extends U> mapper) {
        try {
            return new Success<>(mapper.apply(value));
        } catch (Exception t) {
            return new Failure<>(t);
        }
    }

    @Override
    public <U> Try<U> flatMap(Function<? super T, Try<U>> mapper) {
        try {
            return mapper.apply(value);
        } catch (Throwable t) {
            return new Failure<>(t);
        }
    }

    @Override
    public T getOrElse(T other) {
        return value;
    }

    @Override
    public Try<T> recover(Function<Throwable, ? extends T> recoverFunc) {
        return this;
    }

    @Override
    public <E extends Throwable> Try<T> recover(
            Class<E> exClass, Function<? super E, ? extends T> recoverFunc) {
        return this;
    }

    @Override
    public Try<T> onSuccess(Consumer<? super T> action) {
        action.accept(value);
        return this;
    }

    @Override
    public Try<T> onFailure(Consumer<? super Throwable> action) {
        return this;
    }

    @Override
    public String toString() {
        return "Success(" + value + ")";
    }
}
