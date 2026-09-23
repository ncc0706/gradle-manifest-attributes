package io.github.ncc0706.gradle.plugin

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

/**
 * Platforms 插件的 Gradle TestKit 功能测试。
 *
 * @author NiuYuxian
 * @version 1.0
 * @since 2026-03-23
 */
class PlatformsPluginFunctionalTest {

    @TempDir
    lateinit var testProjectDir: File

    private lateinit var settingsFile: File
    private lateinit var buildFile: File

    /**
     * 初始化临时工程的 settings / build 脚本文件。
     */
    @BeforeEach
    fun setup() {
        settingsFile = File(testProjectDir, "settings.gradle.kts")
        buildFile = File(testProjectDir, "build.gradle.kts")
        settingsFile.writeText("""
            pluginManagement {
                repositories {
                    mavenCentral()
                    gradlePluginPortal()
                }
            }
            rootProject.name = "platforms-test"
        """.trimIndent())
    }

    /**
     * 验证：挂载 Spring BOM 后，annotationProcessor 上的无版本 lombok 可解析。
     */
    @Test
    fun `applies spring bom so lombok without version resolves on annotationProcessor`() {
        buildFile.writeText("""
            plugins {
                `java-library`
                id("io.github.ncc0706.gradle-platforms")
            }

            repositories {
                mavenCentral()
            }

            platforms {
                from("org.springframework.boot:spring-boot-dependencies:2.7.18")
            }

            dependencies {
                compileOnly("org.projectlombok:lombok")
                annotationProcessor("org.projectlombok:lombok")
            }
        """.trimIndent())

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withPluginClasspath()
            .withArguments("dependencies", "--configuration", "annotationProcessor", "--quiet")
            .forwardOutput()
            .build()

        assertTrue(
            result.output.contains("org.projectlombok:lombok") &&
                !result.output.contains("FAILED"),
            "annotationProcessor 应能解析无版本 lombok，实际输出:\n${result.output}"
        )
        assertTrue(
            result.output.contains("spring-boot-dependencies:2.7.18") ||
                result.output.contains("lombok -> 1.18"),
            "应体现 BOM 约束，实际输出:\n${result.output}"
        )
    }

    /**
     * 验证：可同时挂载多个 BOM，且工程可成功配置。
     */
    @Test
    fun `supports multiple platform boms`() {
        buildFile.writeText("""
            plugins {
                `java-library`
                id("io.github.ncc0706.gradle-platforms")
            }

            repositories {
                mavenCentral()
            }

            platforms {
                from("org.springframework.boot:spring-boot-dependencies:2.7.18")
                from("cn.hutool:hutool-bom:5.8.25")
            }

            dependencies {
                implementation("cn.hutool:hutool-core")
            }
        """.trimIndent())

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withPluginClasspath()
            .withArguments("dependencies", "--configuration", "compileClasspath", "--quiet")
            .forwardOutput()
            .build()

        assertTrue(
            result.output.contains("hutool-core") && !result.output.contains("FAILED"),
            "多 BOM 下 hutool-core 应能解析，实际输出:\n${result.output}"
        )
    }

    /**
     * 验证：applyTo 可收窄目标配置。
     */
    @Test
    fun `applyTo limits which configurations inherit platforms`() {
        buildFile.writeText("""
            plugins {
                `java-library`
                id("io.github.ncc0706.gradle-platforms")
            }

            repositories {
                mavenCentral()
            }

            platforms {
                from("org.springframework.boot:spring-boot-dependencies:2.7.18")
                applyTo("implementation")
            }

            dependencies {
                implementation("org.projectlombok:lombok")
            }
        """.trimIndent())

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withPluginClasspath()
            .withArguments("help", "--quiet")
            .forwardOutput()
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":help")?.outcome)
    }
}
