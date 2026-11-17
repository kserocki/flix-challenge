package com.kserocki.flix.buildlogic

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * A `feature:x:impl` module: a plain Android library with Compose and Koin, depending on its own
 * `feature:x:api` (derived from this module's path) plus the core modules every feature is
 * allowed to see (AGENTS.md §3: never `core:data`, never another `impl`).
 *
 * JVM unit tests can host Compose UI with Robolectric: `isIncludeAndroidResources` makes AGP
 * generate the resources/manifest Robolectric needs, and `ui-test-junit4`/`ui-test-manifest` give
 * `createComposeRule()` a host activity, so a semantics assertion doesn't need an emulator.
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

            extensions.configure<LibraryExtension> {
                testOptions.unitTests.isIncludeAndroidResources = true
            }

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
            dependencies.add("testImplementation", dependencies.platform(libs.findLibrary("compose-bom").get()))
            dependencies.add("testImplementation", libs.findLibrary("compose-ui-test-junit4").get())
            dependencies.add("testImplementation", libs.findLibrary("compose-ui-test-manifest").get())
            dependencies.add("testImplementation", libs.findLibrary("robolectric").get())
        }
    }
}
