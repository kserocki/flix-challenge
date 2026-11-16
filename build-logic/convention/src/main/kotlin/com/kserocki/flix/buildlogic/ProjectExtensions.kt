package com.kserocki.flix.buildlogic

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/** The `libs` version catalog, shared by every convention plugin. */
internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

/**
 * Derives the Android namespace / Kotlin root package from the Gradle project path:
 * `:core:domain` -> `com.kserocki.flix.core.domain`, `:feature:timetable:impl` ->
 * `com.kserocki.flix.feature.timetable.impl`, `:app` -> `com.kserocki.flix.app`.
 */
internal fun Project.flixNamespace(): String = (BASE_PACKAGE_SEGMENTS + pathSegments()).joinToString(".")

private fun Project.pathSegments(): List<String> = path.removePrefix(":").split(":").filter { it.isNotEmpty() }

private val BASE_PACKAGE_SEGMENTS = listOf("com", "kserocki", "flix")

/** Minimum Android API level, from `android-minSdk` in the version catalog. */
internal val Project.androidMinSdk: Int
    get() = libs.findVersion("android-minSdk").get().requiredVersion.toInt()

/** Compile-time Android API level, from `android-compileSdk` in the version catalog. */
internal val Project.androidCompileSdk: Int
    get() = libs.findVersion("android-compileSdk").get().requiredVersion.toInt()

/** Target Android API level, from `android-targetSdk` in the version catalog. Application modules only. */
internal val Project.androidTargetSdk: Int
    get() = libs.findVersion("android-targetSdk").get().requiredVersion.toInt()
