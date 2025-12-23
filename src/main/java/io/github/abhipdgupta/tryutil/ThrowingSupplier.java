/* (C)2025 */
package io.github.abhipdgupta.tryutil;

@FunctionalInterface
public interface ThrowingSupplier<T> {
    T get() throws Throwable;
}
