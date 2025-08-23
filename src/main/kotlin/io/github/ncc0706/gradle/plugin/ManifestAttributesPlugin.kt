package io.github.ncc0706.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.java.archives.Manifest
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.attributes
import org.gradle.kotlin.dsl.withType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Created by IntelliJ IDEA. <br/>
 *
 * @author NiuYuxian <br/>
 * @version 1.0
 * @since 2025-08-22 11:41:40 <br/>
 */
class ManifestAttributesPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create("manifestAttributes", ManifestAttributesExtension::class.java)
        project.tasks.withType(Jar::class).configureEach {
            manifest {
                manifestAttributes(project)
                if(extension.customAttributes.isNotEmpty()) {
                    attributes(extension.customAttributes)
                }
            }
        }
    }

    fun Manifest.manifestAttributes(project: Project) = attributes(
        // 构建工具信息
        "Built-By" to "Gradle ${project.gradle.gradleVersion}",
        "Build-Jdk" to System.getProperty("java.version"),
        "JDK-Vendor" to System.getProperty("java.vendor"),
        "JDK-Version" to "${System.getProperty("java.version")} (${System.getProperty("java.vendor.version")})",
        "Build-OS" to "${System.getProperty("os.name")} ${System.getProperty("os.version")} ${System.getProperty("os.arch")}",


        // 版本信息
        "Implementation-Title" to project.name,
        "Implementation-Version" to project.version,

        // 时间戳
        "Build-Date" to LocalDate.now().toString(),
        "Build-Time" to LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),

        // 项目信息
        "Project-Name" to project.name,
        "Project-Group" to project.group,
        "Project-Version" to project.version,
        "Plugin-Name" to "Gradle manifest plugin",
    )
}