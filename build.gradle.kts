import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.maven.publish)
}

group = "io.github.ncc0706"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

dependencies {
    testImplementation(gradleTestKit())
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

gradlePlugin {
    website.set("https://github.com/ncc0706/gradle-manifest-attributes")
    vcsUrl.set("https://github.com/ncc0706/gradle-manifest-attributes")
    plugins {
        create("manifestAttributesPlugin") {
            id = "io.github.ncc0706.gradle-manifest-attributes"
            implementationClass = "io.github.ncc0706.gradle.plugin.ManifestAttributesPlugin"
            displayName = "Manifest Attributes Plugin"
            description = "Adds standardized attributes to JAR manifests"
            tags.set(listOf("Manifest", "Attributes"))
        }
        create("platformsPlugin") {
            id = "io.github.ncc0706.gradle-platforms"
            implementationClass = "io.github.ncc0706.gradle.plugin.PlatformsPlugin"
            displayName = "Dependency Platforms Plugin"
            description = "Applies one or more BOMs/platforms to compileOnly, annotationProcessor and related configurations"
            tags.set(listOf("BOM", "Platform", "Dependencies"))
        }
    }
}

mavenPublishing {
    publishToMavenCentral(true)
    configure(GradlePlugin(JavadocJar.Empty(), true))
    signAllPublications()

    pom {
        name.set("gradle-manifest-attributes")
        description.set("Gradle plugins: JAR manifest attributes and multi-BOM platforms support")
        url.set("https://github.com/ncc0706/gradle-manifest-attributes")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("ncc0706")
                name.set("ncc0706")
                email.set("ncc0706@gmail.com")
            }
        }
        scm {
            connection.set("scm:git:https://github.com/ncc0706/gradle-manifest-attributes.git")
            developerConnection.set("scm:git:https://github.com/ncc0706/gradle-manifest-attributes.git")
            url.set("https://github.com/ncc0706/gradle-manifest-attributes")
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}