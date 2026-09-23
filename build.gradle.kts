plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.3.1"
}

group = "io.github.ncc0706"
version = "1.0.0"

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
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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

tasks.withType<Test> {
    useJUnitPlatform()
}