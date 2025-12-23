/* (C)2025 */
package io.github.abhipdgupta.tryutil;

public class TryException extends RuntimeException {
    public TryException(String message) {
        super(message);
    }

    public TryException(String message, Throwable cause) {
        super(message, cause);
    }

    public TryException(Throwable cause) {
        super(cause);
    }
}
