/* (C)2025 */
package io.github.abhipdgupta.tryutil;

import java.util.function.Consumer;
import java.util.function.Function;

final class Failure<T> extends Try<T> {
    private final Throwable cause;

    Failure(Throwable cause) {
        this.cause = cause;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public boolean isFailure() {
        return true;
    }

    @Override
    public T get() throws TryException {
        throw new TryException(cause);
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    @Override
    public <U> Try<U> map(Function<? super T, ? extends U> mapper) {
        return new Failure<>(cause);
    }

    @Override
    public <U> Try<U> flatMap(Function<? super T, Try<U>> mapper) {
        return new Failure<>(cause);
    }

    @Override
    public T getOrElse(T other) {
        return other;
    }

    @Override
    public Try<T> recover(Function<Throwable, ? extends T> recoverFunc) {
        try {
            return new Success<>(recoverFunc.apply(cause));
        } catch (Throwable t) {
            return new Failure<>(t);
        }
    }

    @Override
    public <E extends Throwable> Try<T> recover(
            Class<E> exClass, Function<? super E, ? extends T> recoverFunc) {
        if (exClass.isInstance(cause)) {
            try {
                return new Success<>(recoverFunc.apply(exClass.cast(cause)));
            } catch (Throwable t) {
                return new Failure<>(t);
            }
        }
        return this;
    }

    @Override
    public Try<T> onSuccess(Consumer<? super T> action) {
        return this;
    }

    @Override
    public Try<T> onFailure(Consumer<? super Throwable> action) {
        action.accept(cause);
        return this;
    }

    @Override
    public String toString() {
        return "Failure(" + cause + ")";
    }
}
