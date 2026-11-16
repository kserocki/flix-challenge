package com.kserocki.flix.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Jetpack Compose for an Android module (library or application): the Compose compiler plugin,
 * `buildFeatures.compose = true` and the `compose-bom`-aligned runtime, foundation, material3 and
 * tooling-preview dependencies. Must be applied after `com.android.library`/`com.android.application`.
 */
class ComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            pluginManager.withPlugin("com.android.library") {
                extensions.configure<LibraryExtension> {
                    buildFeatures { compose = true }
                }
            }
            pluginManager.withPlugin("com.android.application") {
                extensions.configure<ApplicationExtension> {
                    buildFeatures { compose = true }
                }
            }

            val composeBom = libs.findLibrary("compose-bom").get()
            dependencies.add("implementation", dependencies.platform(composeBom))
            dependencies.add("implementation", libs.findLibrary("compose-ui").get())
            dependencies.add("implementation", libs.findLibrary("compose-foundation").get())
            dependencies.add("implementation", libs.findLibrary("compose-material3").get())
            dependencies.add("implementation", libs.findLibrary("compose-ui-tooling-preview").get())
            dependencies.add("debugImplementation", libs.findLibrary("compose-ui-tooling").get())
        }
    }
}
