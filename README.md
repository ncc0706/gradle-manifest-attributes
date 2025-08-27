# Gradle manifest attributes

**setting.gradle.kts**

```groovy
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}
```

**build.gradle.kts**
```groovy

plugins {
    // ...
    id("io.github.ncc0706.gradle-manifest-attributes") version "1.0.0"
}

subprojects {
    // ...
    apply(plugin = "io.github.ncc0706.gradle-manifest-attributes")
    
}

manifestAttributes {
    attribute("Custom1", "custom1")
    attributes(
            "Custom2" to project.name,
            "Custom3" to project.version,
    )
}
```