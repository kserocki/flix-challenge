plugins {
    id("flix.android.application")
    id("flix.compose")
    id("flix.koin")
}

android {
    namespace = "com.kserocki.flix.app"

    defaultConfig {
        applicationId = "com.kserocki.flix"
        versionCode = 1
        versionName = "0.1.0"
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation(projects.core.designsystem)
    implementation(projects.feature.timetable.api)
    implementation(projects.feature.timetable.impl)
    implementation(libs.androidx.activity.compose)
    implementation(libs.navigation3.ui)
    implementation(libs.lifecycle.viewmodel.navigation3)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
}
