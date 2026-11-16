package com.kserocki.flix.buildlogic

import com.kserocki.flix.buildlogic.quality.configureDetekt
import org.gradle.api.Plugin
import org.gradle.api.Project

/** Detekt with the Compose rules (static analysis), part of `check`. */
class QualityConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("dev.detekt")

            configureDetekt()
        }
    }
}
