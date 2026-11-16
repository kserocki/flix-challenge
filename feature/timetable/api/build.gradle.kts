plugins {
    id("flix.android.library")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    api(libs.navigation3.runtime)
    implementation(libs.kotlinx.serialization.core)
}
