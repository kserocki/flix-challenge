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

    // AGP 9 has buildConfig off by default; LogcatDataLogger reads BuildConfig.DEBUG (plan #3).
    buildFeatures {
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        // Makes KOIN-W008 (an unverified Koin graph) fail the build instead of just warning.
        allWarningsAsErrors = true
    }
}

koinCompiler {
    // Demotes the "Kotlin 2.4.21 newer than verified" plugin note: not a real defect (plan #3).
    versionCheckSeverity = "info"
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

    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlin.test.junit)
}
