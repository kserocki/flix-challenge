package com.kserocki.flix.buildlogic

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Applies `com.android.library` and `flix.quality`, and sets the namespace, SDK levels and Java
 * 17 compatibility shared by every plain Android library module. AGP 9's built-in Kotlin support
 * compiles Kotlin sources without `org.jetbrains.kotlin.android`; its `jvmTarget` defaults from
 * `compileOptions.targetCompatibility`, so no separate `kotlin { }` block is needed here.
 */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.library")
            pluginManager.apply("flix.quality")

            val moduleNamespace = flixNamespace()
            val minSdkVersion = androidMinSdk
            val compileSdkVersion = androidCompileSdk

            extensions.configure<LibraryExtension> {
                namespace = moduleNamespace
                compileSdk = compileSdkVersion

                defaultConfig {
                    minSdk = minSdkVersion
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
            }

            dependencies.add("testImplementation", libs.findLibrary("kotlin-test").get())
            // AGP 9's built-in Kotlin doesn't infer a JUnit binding for kotlin-test the way KGP does.
            dependencies.add("testImplementation", libs.findLibrary("kotlin-test-junit").get())
        }
    }
}
