# Gradle manifest attributes

## Overview

The **Manifest Attributes Plugin** is a Gradle plugin designed to automate and standardize the population of the `MANIFEST.MF`file within JAR artifacts. It injects a comprehensive set of build-time and project-related attributes into the manifest, enhancing traceability, auditing, and runtime diagnostics for your Java applications. Furthermore, it provides a flexible extension mechanism for adding custom attributes beyond the default set.

## Features

- **Automatic Attribute Injection**: Automatically configures the manifest for all `Jar`tasks within the project.
- **Comprehensive Build Context**: Captures essential build environment details such as JDK version, Gradle version, operating system, and build timestamp.
- **Project Metadata**: Includes fundamental project information like name, group, and version directly from the Gradle project model.
- **Extensibility**: Supports the addition of arbitrary custom manifest attributes through a dedicated configuration block.

## Usage

### Applying the Plugin

To integrate the plugin into your project, apply it within your build script using the plugins block.

**Kotlin DSL:**

```kotlin
plugins {
     // ...
    id("io.github.ncc0706.gradle-manifest-attributes") version "1.0.0"
}
```

**Groovy DSL:**

```groovy
plugins {
    // ...
    id "io.github.ncc0706.gradle-manifest-attributes" version "1.0.0"
}
```

### Configuration

The plugin provides an extension named `manifestAttributes`for optional customization.

**Adding Custom Attributes:**

You can define additional key-value pairs to be included in the manifest via the `customAttributes`map.

**Kotlin DSL:**

```kotlin
manifestAttributes {
    attribute("Custom1", "custom1")
    attributes(
        "Custom2" to project.name,
        "Custom3" to project.version,
    )
}
```

## Default Attributes

The plugin automatically adds the following attributes to the `MANIFEST.MF`file of all JAR tasks:

| Attribute Key            | Description                                                  | Example Value                  |
| :----------------------- | :----------------------------------------------------------- | :----------------------------- |
| `Built-By`               | The tool used to build the project.                          | `Gradle 8.8`                   |
| `Build-Jdk`              | Version of the JDK used for compilation.                     | `17.0.10`                      |
| `JDK-Vendor`             | Vendor of the JDK.                                           | `Oracle Corporation`           |
| `JDK-Version`            | Detailed JDK version information.                            | `17.0.10 (Oracle GraalVM ...)` |
| `Build-OS`               | Operating system and architecture where the build was executed. | `Windows 11 10.0 amd64`        |
| `Implementation-Title`   | The name of the project.                                     | `my-awesome-library`           |
| `Implementation-Version` | The version of the project.                                  | `1.2.3`                        |
| `Build-Date`             | The date of the build (ISO-8601 date).                       | `2024-05-15`                   |
| `Build-Time`             | The precise time of the build (ISO-8601 date-time).          | `2024-05-15T16:12:45.123Z`     |
| `Project-Name`           | The name of the project (redundant with Implementation-Title). | `my-awesome-library`           |
| `Project-Group`          | The group identifier of the project.                         | `com.example`                  |
| `Project-Version`        | The version of the project (redundant with Implementation-Version). | `1.2.3`                        |
| `Plugin-Name`            | The name of this plugin.                                     | `Gradle manifest plugin`       |

## Example Output

A snippet of the generated `MANIFEST.MF`file will resemble the following:

```
Manifest-Version: 1.0
Built-By: Gradle 8.8
Build-Jdk: 11.0.25
JDK-Vendor: Eclipse Adoptium
JDK-Version: 11.0.25 (Temurin-11.0.25+9)
Build-OS: Windows 10 10.0 amd64
Implementation-Title: gradle-manifest-attributes
Implementation-Version: 1.0.0
Build-Date: 2025-08-28
Build-Time: 2025-08-28T14:54:51.0469411
Project-Name: gradle-manifest-attributes
Project-Group: io.github.ncc0706
Project-Version: 1.0.0
Plugin-Name: Gradle manifest plugin
Custom1: custom1
Custom2: gradle-manifest-attributes
Custom3: 1.0.0

```

**Note:** This plugin is designed to work with the standard Gradle `Jar`task. It will configure the manifest for all tasks of this type within the project where the plugin is applied.