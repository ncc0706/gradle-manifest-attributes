package io.github.ncc0706.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by IntelliJ IDEA. <br/>
 *
 * @author NiuYuxian <br/>
 * @version 1.0
 * @since 2025-08-22 11:41:40 <br/>
 */
class ManifestAttributesPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        project.task("hello-plugin"){

            doLast {
                println("Hello world!")
            }

        }

    }
}