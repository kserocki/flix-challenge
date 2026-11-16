package com.kserocki.flix.buildlogic.quality

import com.kserocki.flix.buildlogic.libs
import dev.detekt.gradle.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.io.File

/**
 * Detekt without type resolution: `source` is set explicitly to every `src/<sourceSet>/kotlin`
 * directory this module has, so coverage does not depend on how the alpha discovers source sets
 * under the AGP 9 KMP library plugin or AGP 9 built-in Kotlin. No baseline.
 */
internal fun Project.configureDetekt() {
    val detektConfigFile = rootProject.layout.projectDirectory.file("config/detekt/detekt.yml")
    val repoRoot = rootProject.layout.projectDirectory
    val moduleSourceDirectories = kotlinSourceDirectories()

    extensions.configure<DetektExtension> {
        buildUponDefaultConfig.set(true)
        allRules.set(false)
        parallel.set(true)
        ignoreFailures.set(false)
        autoCorrect.set(false)
        disableDefaultRuleSets.set(false)
        config.setFrom(detektConfigFile)
        basePath.set(repoRoot)
        source.setFrom(moduleSourceDirectories)
    }

    dependencies.add("detektPlugins", libs.findLibrary("compose-rules-detekt").get())
}

/** Every `src/<sourceSet>/kotlin` directory this module has, whatever the source-set names are. */
private fun Project.kotlinSourceDirectories(): List<File> =
    layout.projectDirectory
        .dir("src")
        .asFile
        .listFiles { file -> file.isDirectory }
        ?.map { sourceSetDir -> sourceSetDir.resolve("kotlin") }
        ?.filter { it.exists() }
        .orEmpty()
