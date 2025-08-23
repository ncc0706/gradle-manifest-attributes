import kotlin.reflect.full.superclasses

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
//    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
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
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<Wrapper> {
    distributionUrl = "https://mirrors.huaweicloud.com/gradle/gradle-8.8-all.zip"
}

//Throwable().printStackTrace()
// [class org.gradle.kotlin.dsl.support.CompiledKotlinBuildScript]
println(this::class.superclasses)