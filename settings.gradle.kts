import org.gradle.api.artifacts.dsl.RepositoryHandler

/** Google's Maven repo, scoped to the groups it actually hosts (speeds up dependency resolution). */
fun RepositoryHandler.googleFiltered() {
    google {
        content {
            includeGroupByRegex("com\\.android.*")
            includeGroupByRegex("com\\.google.*")
            includeGroupByRegex("androidx.*")
        }
    }
}

pluginManagement {
    includeBuild("build-logic")

    // The pluginManagement block is compiled before top-level declarations in this file are
    // visible, so `googleFiltered()` can't be called here; only `dependencyResolutionManagement`
    // below reuses it.
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        googleFiltered()
        mavenCentral()
    }
}

// Incubating feature: must stay enabled for `implementation(projects.core.domain)` style accessors.
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "flix-timetable"

include(":app")

include(":core:common")
include(":core:domain")
include(":core:data")
include(":core:designsystem")
include(":core:testing")

include(":feature:timetable:api")
include(":feature:timetable:impl")

include(":konsist-tests")
