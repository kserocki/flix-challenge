plugins {
    id("flix.android.library")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    api(libs.navigation3.runtime)
    api(libs.kotlinx.serialization.core)
}
