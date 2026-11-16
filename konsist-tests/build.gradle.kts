plugins {
    alias(libs.plugins.kotlin.jvm)
    id("flix.quality")
}

dependencies {
    testImplementation(libs.konsist)
    testImplementation(libs.kotlin.test)
}

tasks.test {
    useJUnitPlatform()

    // Konsist scans the whole repository, so this task must rerun whenever any Kotlin file
    // changes anywhere in the project, not just when this module's own sources change.
    inputs.files(
        fileTree(rootProject.layout.projectDirectory) {
            include("**/*.kt", "**/*.kts")
            exclude("**/build/**", "**/.gradle/**", "**/.kotlin/**")
        },
    )
}
