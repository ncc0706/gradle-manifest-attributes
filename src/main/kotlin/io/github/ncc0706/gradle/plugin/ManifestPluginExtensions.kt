package io.github.ncc0706.gradle.plugin

import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

/**
 * Created by IntelliJ IDEA. <br/>
 *
 * @author NiuYuxian <br/>
 * @version 1.0
 * @since 2025-10-15 14:40:33 <br/>
 */
/**
 * 定义简写函数
 */
fun PluginDependenciesSpec.manifestAttrs(): PluginDependencySpec {
    return id("io.github.ncc0706.gradle-manifest-attributes")
}

fun PluginDependenciesSpec.manifestAttrs(version: String): PluginDependencySpec {
    return id("io.github.ncc0706.gradle-manifest-attributes").version(version)
}

/**
 * @return 插件依赖规格
 */
fun PluginDependenciesSpec.platforms(): PluginDependencySpec {
    return id("io.github.ncc0706.gradle-platforms")
}

/**
 * @param version 插件版本
 * @return 插件依赖规格
 */
fun PluginDependenciesSpec.platforms(version: String): PluginDependencySpec {
    return id("io.github.ncc0706.gradle-platforms").version(version)
}
