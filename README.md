# Try Utility Library (io.github.abhipdgupta.tryutil)

The Try utility is a functional-style container designed to manage and compose operations that may result in exceptions (Throwable). It promotes declarative, type-safe error handling by eliminating verbose try-catch blocks.

Inspired by Vavr, a Try is either a **Success** (containing a result value of type T) or a **Failure** (containing a Throwable cause).

---

## 1. Core Classes and State

| Class         | Description                                                       |
|---------------|-------------------------------------------------------------------|
| `Try<T>`      | The abstract base container (Success or Failure).                 |
| `Success<T>`  | Holds the result value.                                           |
| `Failure<T>`  | Holds the Throwable cause.                                        |
| `TryException`| A RuntimeException thrown by `get()` to wrap the underlying cause. |

---

## 2. Creation

The primary entry point is wrapping a Supplier that might throw an exception.

### of (Supplier)

Wraps a computation, yielding a Success on normal completion or a Failure if any Throwable is caught.

```java
// Creates Success("10")
Try<String> success = Try.of(() -> String.valueOf(10));

// Creates Failure(ArithmeticException)
Try<Integer> failure = Try.of(() -> 10 / 0);
```

---

## 3. Transformations and Composition

These methods enable safe chaining. They only execute on Success; if a Failure is encountered, the chain skips the transformation methods and preserves the Failure. Any exception thrown inside map or flatMap results in a new Failure.

### map (Function)

Transforms the successful value T into a new value U.

### flatMap (Function)

Transforms the successful value T into a new Try\<U\>, used for sequencing Try-returning operations (monadic bind).

```java
Try<Integer> result = Try.of(() -> "100")
        .map(Integer::parseInt)
        .flatMap(i -> Try.of(() -> String.format("%d squared is %d", i, i * i)));
```


## 4. Error Handling and Recovery

### getOrElse (T other)

Returns the value if Success, otherwise returns a default value.

### recover (Function<Throwable, T>)

Recovers from **any** Failure by applying the function to the Throwable, yielding a new Success with the recovery value.

### recover (Class<E>, Function<E, T>)

Recovers selectively from a **specific Exception type**. Non-matching Failures are passed through.

```java
// 1. Fallback value
int value = Try.of(() -> 1 / 0).getOrElse(-1); // -1

// 2. General recovery
Try<String> recovered = Try.of(() -> apiCall())
.recover(t -> "Fallback Data"); // Success("Fallback Data")

// 3. Selective recovery
Try<String> result = Try.of(() -> { throw new TimeoutException(); })
.recover(IOException.class, e -> "IO Error Fallback") // Skipped
.recover(TimeoutException.class, e -> "Timeout Fallback"); // Success("Timeout Fallback")
```

---

## 5. Side Effects and Unwrapping

### onSuccess (Consumer<T>)

Performs a side-effect action on the value if Success. Returns this.

### onFailure (Consumer<Throwable>)

Performs a side-effect action on the Throwable if Failure. Returns this.

### getOrElseThrow ()

Returns the value if Success, otherwise throws the **original Throwable**.

### getOrElseThrow (Function<Throwable, X>)

Returns the value if Success, otherwise throws a custom Exception (X) mapped from the original Throwable.

```java
// Log and then throw if failed
String data = Try.of(() -> executeTask())
.onFailure(t -> System.err.println("Task failed: " + t.getMessage()))
.getOrElseThrow(cause -> new TaskException("Critical failure", cause));

// Simple unwrap (must handle the checked exception)
String content = Try.of(() -> readFile()).getOrElseThrow();
```