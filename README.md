# Missing Link - A Gradle Dependency Problem Finder

**Missing Link** is a plugin for Gradle that helps identify classpath conflicts 
and dependency problems in your project. Originally developed for Maven, this 
plugin has been ported to Gradle to provide similar functionality.

## Installation

Add the plugin to your `build.gradle` (Groovy DSL):

```groovy
plugins {
    id 'com.mridang.gradle.missinglink' version '0.2.1'
}
```

## Usage

Once the plugin is installed, it runs as part of Gradle's verification tasks, 
ensuring that dependency conflicts are checked alongside other verification 
processes.

Once the plugin is installed, you can run it with:

```sh
gradle missinglinkCheck
```

This will analyze your dependencies and report any conflicts that might cause 
issues at runtime.

## Configuration

You can configure the plugin in your `build.gradle` file:

```groovy
missinglink {
    failOnConflicts = true
    ignoreSourcePackages = ["groovy.lang"]
    ignoreDestinationPackages = ["com.foo"]
}
```

### Exclude Some Dependencies from Analysis

Specific dependencies can be excluded from analysis if you know that all 
conflicts within that dependency are "false" or irrelevant to your project.

For example, to exclude `jackson-databind` from analysis:

```groovy
missingLink {
    failOnConflicts = true
    excludeDependencies = ['com.fasterxml.jackson.core:jackson-databind']
}
```

This prevents the plugin from analyzing `jackson-databind` for conflicts, 
reducing false positives.

### Configuring the Reports

Missing Link supports Gradle’s reporting infrastructure. You can configure
output reports in different formats, such as HTML, XML, and SARIF.

```groovy
missingLink {
    failOnConflicts = true
    excludeDependencies = ['com.fasterxml.jackson.core:jackson-databind']
    reports {
        html {
            required = false
        }
        xml {
            required = false
        }
        sarif {
            required = false
        }
    }
}
```

The reports will be generated in Gradle’s default reporting directory unless 
configured otherwise.

### Specifying Output Location

The plugin supports customizing the output directory using Gradle's `reporting` 
extension:

```groovy
reporting {
    baseDirectory = layout.buildDirectory.dir("our-reports")
}
```

This allows you to control where reports are stored, aligning with Gradle’s s
tandard reporting behavior.

## Caveats

### Reflection

The plugin cannot detect issues arising from reflective class loading. If your 
code relies on reflection, some conflicts may go undetected.

### Dependency Injection Containers

Frameworks that use dynamic dependency injection, such as Guice or Spring, 
may introduce dependencies at runtime that Missing Link cannot analyze.

### Dead Code

Missing Link analyzes all method calls in your bytecode, even if some methods 
are never executed at runtime. This may lead to false positives.

### Optional Dependencies

Some libraries check for optional dependencies using `Class.forName()`. The 
plugin might report conflicts even if they do not affect runtime behavior.

## Contributing

Contributions are welcome! If you find a bug or have suggestions for improvement, 
please open an issue or submit a pull request.

## License

Apache License 2.0 © 2024 Mridang Agarwalla

