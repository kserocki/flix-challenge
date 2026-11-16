package com.kserocki.flix.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * A `feature:x:impl` module: a plain Android library with Compose and Koin, depending on its own
 * `feature:x:api` (derived from this module's path) plus the core modules every feature is
 * allowed to see (AGENTS.md §3: never `core:data`, never another `impl`).
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("flix.android.library")
            pluginManager.apply("flix.compose")
            pluginManager.apply("flix.koin")

            check(path.endsWith(":impl")) {
                "flix.android.feature expects a module path ending in `:impl`, got `$path`."
            }
            val apiPath = path.removeSuffix(":impl") + ":api"

            dependencies.add("implementation", dependencies.project(apiPath))
            dependencies.add("implementation", dependencies.project(":core:domain"))
            dependencies.add("implementation", dependencies.project(":core:common"))
            dependencies.add("implementation", dependencies.project(":core:designsystem"))
            dependencies.add("implementation", libs.findLibrary("lifecycle-runtime-compose").get())
            dependencies.add("implementation", libs.findLibrary("lifecycle-viewmodel-compose").get())
            dependencies.add("implementation", libs.findLibrary("koin-androidx-compose").get())
            dependencies.add("implementation", libs.findLibrary("navigation3-runtime").get())
            dependencies.add("implementation", libs.findLibrary("kotlinx-collections-immutable").get())

            dependencies.add("testImplementation", dependencies.project(":core:testing"))
            dependencies.add("testImplementation", libs.findLibrary("kotlinx-coroutines-test").get())
            dependencies.add("testImplementation", libs.findLibrary("turbine").get())
        }
    }
}
