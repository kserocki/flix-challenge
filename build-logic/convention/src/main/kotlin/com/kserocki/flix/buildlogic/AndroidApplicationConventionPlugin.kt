package com.kserocki.flix.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Applies `com.android.application` and `flix.quality`, and sets the SDK levels and Java 17
 * compatibility shared by the application module. AGP 9's built-in Kotlin support compiles
 * Kotlin sources without `org.jetbrains.kotlin.android`; its `jvmTarget` defaults from
 * `compileOptions.targetCompatibility`, so no separate `kotlin { }` block is needed here.
 */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.application")
            pluginManager.apply("flix.quality")

            val minSdkVersion = androidMinSdk
            val compileSdkVersion = androidCompileSdk
            val targetSdkVersion = androidTargetSdk

            extensions.configure<ApplicationExtension> {
                compileSdk = compileSdkVersion

                defaultConfig {
                    minSdk = minSdkVersion
                    targetSdk = targetSdkVersion
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
            }
        }
    }
}
