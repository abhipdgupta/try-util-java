# try-util

[![Java CI with Maven](https://github.com/abhipdgupta/tryutil-java/actions/workflows/maven.yml/badge.svg)](https://github.com/abhipdgupta/tryutil-java/actions/workflows/maven.yml)

A lightweight, zero-dependency Java library for functional-style error handling.

`try-util` provides a `Try` monad to encapsulate computations that may result in an exception, allowing you to write cleaner, more composable, and more robust code. Instead of using verbose `try-catch` blocks, you can chain operations, transform values, and handle failures in a declarative way.

## Features

- **Encapsulate Success or Failure:** A `Try` is either a `Success` holding a result or a `Failure` holding an exception.
- **Functional Composition:** Chain operations with `map` and `flatMap`.
- **Flexible Error Handling:** Recover from failures with `recover` or provide default values with `getOrElse`.
- **Side Effects:** Use `onSuccess` and `onFailure` for logging or other side effects.
- **Easy Unwrapping:** Get the value or throw an exception with `get` and `getOrElseThrow`.

## Getting Started

### Maven

Add the following dependency to your `pom.xml` file:

```xml
<dependency>
    <groupId>io.github.abhipdgupta</groupId>
    <artifactId>try-util</artifactId>
    <version>1.0.0</version>
</dependency>
```

And add the GitHub Packages repository to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github</id>
        <name>GitHub Packages</name>
        <url>https://maven.pkg.github.com/abhipdgupta/tryutil-java</url>
    </repository>
</repositories>
```

## Usage

### Creating a `Try`

Use `Try.of()` to wrap a computation that might throw an exception.

```java
// Success case
Try<Integer> age = Try.of(() -> 25); // Success(25)

// Failure case
Try<Integer> error = Try.of(() -> 1 / 0); // Failure(java.lang.ArithmeticException: / by zero)
```

### Transforming Values

Use `map` to transform the value inside a `Success`. If the `Try` is a `Failure`, `map` does nothing.

```java
Try<String> message = age.map(a -> "Age is " + a); // Success("Age is 25")
Try<String> errorMessage = error.map(a -> "Age is " + a); // Failure(java.lang.ArithmeticException: / by zero)
```

### Chaining Operations

Use `flatMap` to chain operations that return a `Try`.

```java
Try<Double> result = Try.of(() -> "123.45")
    .map(Double::parseDouble)
    .flatMap(d -> Try.of(() -> d * 2)); // Success(246.9)
```

### Handling Failures

Use `getOrElse` to provide a default value in case of a failure.

```java
Integer defaultAge = error.getOrElse(30); // 30
```

Use `recover` to handle an exception and return a `Success`.

```java
Try<Integer> recovered = error.recover(throwable -> {
    System.out.println("Recovered from: " + throwable.getMessage());
    return 0;
}); // Success(0)
```

You can also recover from specific exception types:

```java
Try<Integer> recoveredSpecific = Try.of(() -> {
        throw new java.io.IOException();
    })
    .recover(java.io.IOException.class, e -> 0)
    .recover(Exception.class, e -> -1); // Recovers with 0
```

### Getting the Value

Use `get()` to get the value. If the `Try` is a `Failure`, it throws a `TryException`.

```java
Integer myAge = age.get(); // 25
try {
    Integer errorAge = error.get();
} catch (TryException e) {
    System.out.println(e.getCause()); // java.lang.ArithmeticException: / by zero
}
```

Use `getOrElseThrow()` to re-throw the original exception.

```java
try {
    Integer value = error.getOrElseThrow();
} catch (ArithmeticException e) {
    // Handle the original exception
}
```

## Building the Project

To build the project and run the tests, you need to have Java 21 and Maven installed.

Clone the repository and run the following command:

```bash
mvn clean install
```

## Contributing

Contributions are welcome! Please open an issue or submit a pull request.

## License

This project is licensed under the MIT License.
