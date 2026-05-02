# CodeWeaver

A Gradle plugin for conditional compilation in Java. Wrap code in `#if`/`#endif` blocks and control whether it's included or commented out at build time based on flags you define.

## Why

Sometimes you need code that should only exist in certain builds, for example: debug utilities, offline mode connectors, maintenance endpoints. But you don't want that code compiled into your production jar at all. CodeWeaver comments it out before compilation so it never makes it into the bytecode.

## Setup

```groovy
plugins {
    id 'io.github.szypkoo.codeweaver' version '0.4.1'
}

codeWeaver {
    flag 'TEST_FLAG', false
}
```

Kotlin DSL:

```kotlin
plugins {
    id("io.github.szypkoo.codeweaver") version "0.4.1"
}

codeWeaver {
    flag("TEST_FLAG", false)
}
```

## Usage

Wrap any code you want to conditionally include:

```java
public class MyService {
    public void handleRequestIfPossible() {
        // #if MAINTENANCE_MODE
        // return new MaintenanceResponse();
        // #endif
    
        return handleRequest();
    }
}
```

When `MAINTENANCE_MODE` is `true`, the code is uncommented and compiled normally:

```java
    // #if MAINTENANCE_MODE
    return new MaintenanceResponse();
    // #endif
```

When `MAINTENANCE_MODE` is `false`, the code is commented out and never reaches the compiler:

```java
    // #if MAINTENANCE_MODE
    // return new MaintenanceResponse();
    // #endif
```

## How it works

CodeWeaver registers a `processConditionalsMainSources` task that runs automatically before `compileJava`. It scans all `.java` files in your source set and rewrites `#if`/`#endif` blocks in place based on your configured flags. Unknown flags are treated as `false`.

## Rules

- The `// #if` and `// #endif` markers are always preserved as-is
- Commented code is indented to match the `// #if` line, not the original code
- Empty lines inside blocks are left untouched
- Running the task multiple times is safe, already commented lines won't get double-commented

## License

MIT
