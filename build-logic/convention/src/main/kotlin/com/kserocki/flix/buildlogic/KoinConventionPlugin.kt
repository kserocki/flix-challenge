package com.kserocki.flix.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Koin compiler plugin (`@Single`/`@Factory`/`@KoinViewModel`/`@Module`) plus the `koin-core` and
 * `koin-annotations` runtime. Adds the dependencies to `commonMain` for a KMP module
 * (`flix.kmp.library`), or as plain `implementation` for a plain Android module.
 */
class KoinConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("io.insert-koin.compiler.plugin")

            if (pluginManager.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
                extensions.getByType<KotlinMultiplatformExtension>().sourceSets.named("commonMain") {
                    dependencies {
                        implementation(libs.findLibrary("koin-core").get())
                        implementation(libs.findLibrary("koin-annotations").get())
                    }
                }
            } else {
                dependencies.add("implementation", libs.findLibrary("koin-core").get())
                dependencies.add("implementation", libs.findLibrary("koin-annotations").get())
            }
        }
    }
}
