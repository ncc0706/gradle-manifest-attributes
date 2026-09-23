package io.github.ncc0706.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.provider.Provider
import javax.inject.Inject

/**
 * 通用 Platform / BOM 扩展。
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
        // 配置创建时自动挂接，避免 afterEvaluate
        project.configurations.configureEach {
            maybeExtendFromPlatforms(this)
        }
        project.pluginManager.withPlugin("java") {
            wireTargetConfigurations()
        }
    }

    /**
     * 添加一个 Platform / BOM（坐标字符串或已解析的依赖记号）。
     *
     * @param notation 例如 {@code "org.springframework.boot:spring-boot-dependencies:3.4.7"}
     */
    fun from(notation: Any) {
        when (notation) {
            is Provider<*> -> addPlatformProvider(notation)
            else -> project.dependencies.add(
                platformsConfiguration.name,
                project.dependencies.platform(notation)
            )
        }
    }

    /**
     * 添加一个 Platform / BOM（支持 Version Catalog 的 Provider，无需手动 {@code .get()}）。
     * <p>推荐写法：{@code from(libs.spring.boot.dependencies)}</p>
     *
     * @param notation Provider 形式的依赖记号
     */
    fun from(notation: Provider<*>) {
        addPlatformProvider(notation)
    }

    /**
     * 将 Provider 形式的 BOM 以惰性方式加入 platforms 配置。
     *
     * @param notation BOM Provider
     */
    private fun addPlatformProvider(notation: Provider<*>) {
        project.dependencies.addProvider(
            platformsConfiguration.name,
            notation.map { value ->
                requireNotNull(value) { "platforms.from(...) 收到的 Provider 值为 null" }
                project.dependencies.platform(value)
            }
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
            project.configurations.findByName(name)?.let { maybeExtendFromPlatforms(it) }
        }
    }

    /**
     * 若配置名在目标列表中，则继承 platforms 配置。
     *
     * @param configuration 待检查的配置
     */
    private fun maybeExtendFromPlatforms(configuration: Configuration) {
        if (configuration.name !in targetConfigurationNames) {
            return
        }
        if (configuration === platformsConfiguration) {
            return
        }
        if (!configuration.extendsFrom.contains(platformsConfiguration)) {
            configuration.extendsFrom(platformsConfiguration)
        }
    }

    companion object {
        const val CONFIGURATION_NAME: String = "dependencyPlatforms"
        const val EXTENSION_NAME: String = "platforms"
    }
}
