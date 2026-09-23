//import kotlin.reflect.full.superclasses

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.3.1"
    id("io.github.ncc0706.gradle-manifest-attributes") version "1.0.0"
}

group = "io.github.ncc0706"
version = "1.0.1"

//repositories {
//    mavenCentral()
//}


java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

dependencies {
//    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
}


manifestAttributes {
    attribute("Custom1", "custom1")
    attributes(
        "Custom2" to project.name,
        "Custom3" to project.version,
    )
}

gradlePlugin {
    website.set("https://github.com/ncc0706/gradle-manifest-attributes")
    vcsUrl.set("https://github.com/ncc0706/gradle-manifest-attributes")
    plugins {
        create("manifestAttributesPlugin") {
            version = "1.0.1-SNAPSHOT"
            id = "io.github.ncc0706.gradle-manifest-attributes"
            implementationClass = "io.github.ncc0706.gradle.plugin.ManifestAttributesPlugin"
            displayName = "Manifest Attributes Plugin"
            description = "Adds standardized attributes to JAR manifests"
            tags.set(listOf("Manifest", "Attributes"))
        }
        create("platformsPlugin") {
            version = "1.0.1-SNAPSHOT"
            id = "io.github.ncc0706.gradle-platforms"
            implementationClass = "io.github.ncc0706.gradle.plugin.PlatformsPlugin"
            displayName = "Dependency Platforms Plugin"
            description = "Applies one or more BOMs/platforms to compileOnly, annotationProcessor and related configurations"
            tags.set(listOf("BOM", "Platform", "Dependencies"))
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

//Throwable().printStackTrace()
// [class org.gradle.kotlin.dsl.support.CompiledKotlinBuildScript]
//println(this::class.superclasses)