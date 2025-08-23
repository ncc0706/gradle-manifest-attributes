package io.github.ncc0706.gradle.plugin

/**
 * Created by IntelliJ IDEA. <br/>
 *
 * @author NiuYuxian <br/>
 * @version 1.0
 * @since 2025-08-22 11:41:27 <br/>
 */
open class ManifestAttributesExtension {

    val customAttributes: MutableMap<String, Any> = mutableMapOf()

    fun attribute(key: String, value: Any) {
        customAttributes[key] = value
    }

    fun attributes(vararg pairs: Pair<String, Any>) {
        pairs.forEach { (key, value) ->
            customAttributes[key] = value
        }
    }

    fun attributes(map: Map<String, Any>) {
        customAttributes.putAll(map)
    }
}