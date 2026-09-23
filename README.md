# Gradle Plugins by ncc0706

本仓库发布 **两个** Gradle 插件（同版本号、一次发布）：

| Plugin ID | 职责 |
|-----------|------|
| `io.github.ncc0706.gradle-manifest-attributes` | 为 JAR 写入标准化 `MANIFEST.MF` 属性 |
| `io.github.ncc0706.gradle-platforms` | 将一个或多个 BOM/Platform 应用到 `compileOnly`、`annotationProcessor` 等配置 |

可按需只引用其中一个，也可两个一起用。

---

## 1. Manifest Attributes Plugin

### 功能

- 自动为所有 `Jar` 任务注入构建与项目元数据
- 支持通过扩展追加自定义属性

### 应用

**Kotlin DSL：**

```kotlin
plugins {
    id("io.github.ncc0706.gradle-manifest-attributes") version "1.0.1"
}
```

**Groovy DSL：**

```groovy
plugins {
    id "io.github.ncc0706.gradle-manifest-attributes" version "1.0.1"
}
```

### 配置自定义属性

```kotlin
manifestAttributes {
    attribute("Custom1", "custom1")
    attributes(
        "Custom2" to project.name,
        "Custom3" to project.version,
    )
}
```

### 默认属性

| Attribute Key | Description | Example |
|---------------|-------------|---------|
| `Built-By` | 构建工具 | `Gradle 8.8` |
| `Build-Jdk` | JDK 版本 | `17.0.10` |
| `JDK-Vendor` | JDK 厂商 | `Eclipse Adoptium` |
| `JDK-Version` | JDK 详细版本 | `17.0.10 (...)` |
| `Build-OS` | 操作系统 | `Windows 11 10.0 amd64` |
| `Implementation-Title` | 项目名 | `my-library` |
| `Implementation-Version` | 项目版本 | `1.2.3` |
| `Build-Date` | 构建日期 | `2024-05-15` |
| `Build-Time` | 构建时间 | `2024-05-15T16:12:45.123Z` |
| `Project-Name` | 项目名 | `my-library` |
| `Project-Group` | group | `com.example` |
| `Project-Version` | version | `1.2.3` |
| `Plugin-Name` | 本插件标识 | `Gradle manifest plugin` |

---

## 2. Platforms Plugin

### 解决什么问题

Gradle 的 `platform(...)` / BOM **按 configuration 生效**。只写：

```kotlin
implementation(platform("org.springframework.boot:spring-boot-dependencies:x.y.z"))
```

时，`annotationProcessor` / `compileOnly` 上的无版本依赖（如 Lombok）**往往解析失败**。

本插件创建一个内部配置 `dependencyPlatforms`，并让常用配置继承它，从而一次声明、多处生效。

### 应用

```kotlin
plugins {
    `java-library`
    id("io.github.ncc0706.gradle-platforms") version "1.0.1"
}
```

### `from` 用法

#### 坐标字符串

```kotlin
platforms {
    from("org.springframework.boot:spring-boot-dependencies:3.4.7")
    from("cn.hutool:hutool-bom:5.8.35")
    // from("org.mybatis:mybatis-bom:3.5.19")
}
```

#### Version Catalog（推荐，无需 `.get()`）

`gradle/libs.versions.toml`：

```toml
[versions]
spring-boot = "3.4.7"

[libraries]
spring-boot-dependencies = { module = "org.springframework.boot:spring-boot-dependencies", version.ref = "spring-boot" }
```

业务工程：

```kotlin
platforms {
    from(libs.spring.boot.dependencies)
}

dependencies {
    // 不必写入 libs.versions.toml；坐标无版本，由 BOM 约束
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}
```

若仍想用 catalog 别名，也可以只声明模块名、不写版本：

```toml
lombok = { module = "org.projectlombok:lombok" }
```

```kotlin
compileOnly(libs.lombok)
```

两种都行；**推荐前者**（catalog 不维护 lombok）。
#### 多模块根工程

```kotlin
subprojects {
    apply(plugin = "io.github.ncc0706.gradle-platforms")

    extensions.configure<io.github.ncc0706.gradle.plugin.PlatformsExtension>("platforms") {
        from(rootProject.libs.spring.boot.dependencies)
    }
}
```

### 默认作用的配置

- `implementation`
- `compileOnly`
- `annotationProcessor`
- `testImplementation`
- `testCompileOnly`
- `testAnnotationProcessor`

### 调整作用范围

```kotlin
platforms {
    from(libs.spring.boot.dependencies)
    // 覆盖默认列表
    applyTo("implementation", "compileOnly", "annotationProcessor")
    // 或在默认基础上追加
    applyToAlso("api", "runtimeOnly")
}
```

### 本地 SNAPSHOT 联调

1. 插件工程：`./gradlew publishToMavenLocal`
2. 业务工程 `settings.gradle.kts` 最前面：

```kotlin
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}
```

3. 若使用了会 `clear()` 插件仓库的全局 `init.gradle(.kts)`，务必把 `mavenLocal()` 加回去。

---

## 发布说明

- 同仓双插件，版本号一致（如 `1.0.1`）
- **Gradle Plugin Portal 不支持 SNAPSHOT**；本地 / 私服可用 `x.y.z-SNAPSHOT`
- 首次发布 `gradle-platforms` 到 Portal 时，需为新 Plugin ID 完成认领

## 开发与测试

```bash
./gradlew test
./gradlew publishToMavenLocal
```

使用 Gradle TestKit 覆盖 Manifest / Platforms 的基本功能场景。
