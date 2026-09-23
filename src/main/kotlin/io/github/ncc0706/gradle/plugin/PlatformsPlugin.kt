package io.github.ncc0706.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author NiuYuxian
 * @version 1.0
 * @since 2026-03-23
 */
class PlatformsPlugin : Plugin<Project> {

    /**
     * 注册 {@code platforms} 扩展。
     *
     * @param project 当前 Gradle 工程
     */
    override fun apply(project: Project) {
        project.extensions.create(
            PlatformsExtension.EXTENSION_NAME,
            PlatformsExtension::class.java,
            project
        )
    }
}
