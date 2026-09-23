package io.github.ncc0706.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import javax.inject.Inject

/**
 * 通用 Platform / BOM 扩展。
 * <p>支持挂载多个 BOM（Spring / MyBatis / Hutool 等），并让指定配置继承其版本约束。</p>
 *
 * @author NiuYuxian
 * @version 1.0
 * @since 2026-03-23
 */
open class PlatformsExtension @Inject constructor(
    private val project: Project
) {

    private val platformsConfiguration: Configuration =
        project.configurations.create(CONFIGURATION_NAME) {
            isCanBeConsumed = false
            isCanBeResolved = false
            isVisible = false
            description = "Holds platform/BOM constraints for compileOnly, annotationProcessor, etc."
        }

    /** 需要继承 BOM 约束的配置名 */
    private val targetConfigurationNames: MutableSet<String> = linkedSetOf(
        "implementation",
        "compileOnly",
        "annotationProcessor",
        "testImplementation",
        "testCompileOnly",
        "testAnnotationProcessor"
    )

    init {
        // java / java-library 应用后再接线，避免配置尚未创建
        project.pluginManager.withPlugin("java") {
            wireTargetConfigurations()
        }
        project.afterEvaluate {
            wireTargetConfigurations()
        }
    }

    /**
     * 添加一个 Platform / BOM。
     * <p>可传入坐标字符串，或 Version Catalog 中的库（如 {@code libs.spring.boot.dependencies.get()}）。</p>
     *
     * @param notation BOM 坐标或 catalog 依赖对象；若为 {@link org.gradle.api.provider.Provider} 会自动解包
     */
    fun from(notation: Any) {
        val resolved = when (notation) {
            is org.gradle.api.provider.Provider<*> -> notation.get()
                ?: error("platforms.from(...) 收到的 Provider 值为 null")
            else -> notation
        }
        project.dependencies.add(
            platformsConfiguration.name,
            project.dependencies.platform(resolved)
        )
    }

    /**
     * 覆盖默认的目标配置列表。
     *
     * @param names 需要继承 BOM 约束的配置名
     */
    fun applyTo(vararg names: String) {
        targetConfigurationNames.clear()
        targetConfigurationNames.addAll(names)
        wireTargetConfigurations()
    }

    /**
     * 在默认列表上追加目标配置。
     *
     * @param names 额外配置名
     */
    fun applyToAlso(vararg names: String) {
        targetConfigurationNames.addAll(names)
        wireTargetConfigurations()
    }

    /**
     * 将内部 platforms 配置挂到各目标配置上。
     */
    private fun wireTargetConfigurations() {
        targetConfigurationNames.forEach { name ->
            val target = project.configurations.findByName(name) ?: return@forEach
            if (!target.extendsFrom.contains(platformsConfiguration)) {
                target.extendsFrom(platformsConfiguration)
            }
        }
    }

    companion object {
        const val CONFIGURATION_NAME: String = "dependencyPlatforms"
        const val EXTENSION_NAME: String = "platforms"
    }
}
