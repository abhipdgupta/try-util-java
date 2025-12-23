/* (C)2025 */
package io.github.abhipdgupta.tryutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

class TryTest {

    @Test
    void testTryOfSuccess() {
        Try<Integer> success = Try.of(() -> 10);
        assertTrue(success.isSuccess());
        assertFalse(success.isFailure());
        assertEquals(10, success.get());
    }

    @Test
    void testTryOfFailure() {
        ArithmeticException exception = new ArithmeticException("Division by zero");
        Try<Integer> failure =
                Try.of(
                        () -> {
                            throw exception;
                        });
        assertFalse(failure.isSuccess());
        assertTrue(failure.isFailure());
        assertEquals(exception, failure.getCause());
    }

    @Test
    void testMapOnSuccess() {
        Try<Integer> success = Try.of(() -> 10);
        Try<String> mapped = success.map(Object::toString);
        assertTrue(mapped.isSuccess());
        assertEquals("10", mapped.get());
    }

    @Test
    void testMapOnFailure() {
        ArithmeticException exception = new ArithmeticException("Division by zero");
        Try<Integer> failure =
                Try.of(
                        () -> {
                            throw exception;
                        });
        Try<String> mapped = failure.map(Object::toString);
        assertTrue(mapped.isFailure());
        assertEquals(exception, mapped.getCause());
    }

    @Test
    void testFlatMapOnSuccess() {
        Try<Integer> success = Try.of(() -> 10);
        Try<String> mapped = success.flatMap(i -> Try.of(() -> "Value is " + i));
        assertTrue(mapped.isSuccess());
        assertEquals("Value is 10", mapped.get());
    }

    @Test
    void testFlatMapOnFailure() {
        ArithmeticException exception = new ArithmeticException("Division by zero");
        Try<Integer> failure =
                Try.of(
                        () -> {
                            throw exception;
                        });
        Try<String> mapped = failure.flatMap(i -> Try.of(() -> "Value is " + i));
        assertTrue(mapped.isFailure());
        assertEquals(exception, mapped.getCause());
    }

    @Test
    void testGetOrElseOnSuccess() {
        Try<Integer> success = Try.of(() -> 10);
        assertEquals(10, success.getOrElse(-1));
    }

    @Test
    void testGetOrElseOnFailure() {
        Try<Integer> failure = Try.of(() -> 1 / 0);
        assertEquals(-1, failure.getOrElse(-1));
    }

    @Test
    void testRecoverOnFailure() {
        Try<Integer> failure = Try.of(() -> 1 / 0);
        Try<Integer> recovered = failure.recover(t -> 0);
        assertTrue(recovered.isSuccess());
        assertEquals(0, recovered.get());
    }

    @Test
    void testRecoverOnSuccess() {
        Try<Integer> success = Try.of(() -> 10);
        Try<Integer> recovered = success.recover(t -> 0);
        assertTrue(recovered.isSuccess());
        assertEquals(10, recovered.get());
    }

    @Test
    void testRecoverWithSpecificException() {
        Try<Integer> failure =
                Try.ofChecked(
                        () -> {
                            throw new IOException();
                        });
        Try<Integer> recovered = failure.recover(IOException.class, e -> 0);
        assertTrue(recovered.isSuccess());
        assertEquals(0, recovered.get());

        Try<Integer> notRecovered = failure.recover(ArithmeticException.class, e -> -1);
        assertTrue(notRecovered.isFailure());
    }

    @Test
    void testOnSuccessOnSuccess() {
        AtomicBoolean called = new AtomicBoolean(false);
        Try<Integer> success = Try.of(() -> 10);
        success.onSuccess(i -> called.set(true));
        assertTrue(called.get());
    }

    @Test
    void testOnSuccessOnFailure() {
        AtomicBoolean called = new AtomicBoolean(false);
        Try<Integer> failure = Try.of(() -> 1 / 0);
        failure.onSuccess(i -> called.set(true));
        assertFalse(called.get());
    }

    @Test
    void testOnFailureOnFailure() {
        AtomicBoolean called = new AtomicBoolean(false);
        Try<Integer> failure = Try.of(() -> 1 / 0);
        failure.onFailure(t -> called.set(true));
        assertTrue(called.get());
    }

    @Test
    void testOnFailureOnSuccess() {
        AtomicBoolean called = new AtomicBoolean(false);
        Try<Integer> success = Try.of(() -> 10);
        success.onFailure(t -> called.set(true));
        assertFalse(called.get());
    }

    @Test
    void testGetOnSuccess() {
        Try<Integer> success = Try.of(() -> 10);
        assertEquals(10, success.get());
    }

    @Test
    void testGetOnFailure() {
        Try<Integer> failure = Try.of(() -> 1 / 0);
        TryException exception = assertThrows(TryException.class, failure::get);

        assertNotNull(exception.getCause());
        assertEquals(ArithmeticException.class, exception.getCause().getClass());
    }

    @Test
    void testGetOrElseThrowOnSuccess() {
        Try<Integer> success = Try.of(() -> 10);
        try {
            assertEquals(10, success.getOrElseThrow());
        } catch (Throwable t) {
            fail("Should not throw exception");
        }
    }

    @Test
    void testGetOrElseThrowOnFailure() {
        Try<Integer> failure = Try.of(() -> 1 / 0);
        ArithmeticException exception =
                assertThrows(ArithmeticException.class, failure::getOrElseThrow);
        assertNotNull(exception);
    }

    @Test
    void testIsSuccessAndIsFailure() {
        Try<Integer> success = Try.of(() -> 10);
        assertTrue(success.isSuccess());
        assertFalse(success.isFailure());

        Try<Integer> failure = Try.of(() -> 1 / 0);
        assertFalse(failure.isSuccess());
        assertTrue(failure.isFailure());
    }
}
