package io.github.ncc0706.gradle.plugin

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.jar.JarFile

/**
 * Manifest Attributes 插件的 Gradle TestKit 功能测试。
 *
 * @author NiuYuxian
 * @version 1.0
 * @since 2026-03-23
 */
class ManifestAttributesPluginFunctionalTest {

    @TempDir
    lateinit var testProjectDir: File

    private lateinit var settingsFile: File
    private lateinit var buildFile: File

    /**
     * 初始化临时工程文件。
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
            rootProject.name = "manifest-test"
        """.trimIndent())
    }

    /**
     * 验证：jar 任务生成的 MANIFEST.MF 包含默认与自定义属性。
     */
    @Test
    fun `writes default and custom attributes into jar manifest`() {
        File(testProjectDir, "src/main/java").mkdirs()
        File(testProjectDir, "src/main/java/Dummy.java").writeText(
            "public class Dummy {}"
        )

        buildFile.writeText("""
            plugins {
                `java-library`
                id("io.github.ncc0706.gradle-manifest-attributes")
            }

            group = "io.github.ncc0706"
            version = "1.2.3"

            manifestAttributes {
                attribute("Custom1", "custom1")
            }
        """.trimIndent())

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withPluginClasspath()
            .withArguments("jar", "--quiet")
            .forwardOutput()
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":jar")?.outcome)

        val jarFile = testProjectDir.resolve("build/libs").listFiles()
            ?.firstOrNull { it.name.endsWith(".jar") && !it.name.contains("sources") }
            ?: error("未找到 jar 产物")

        JarFile(jarFile).use { jar ->
            val attrs = jar.manifest.mainAttributes
            assertEquals("manifest-test", attrs.getValue("Implementation-Title"))
            assertEquals("1.2.3", attrs.getValue("Implementation-Version"))
            assertEquals("io.github.ncc0706", attrs.getValue("Project-Group"))
            assertEquals("custom1", attrs.getValue("Custom1"))
            assertTrue(!attrs.getValue("Built-By").isNullOrBlank())
            assertTrue(!attrs.getValue("Build-Jdk").isNullOrBlank())
        }
    }
}
