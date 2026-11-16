plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.compiler.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
    compileOnly(libs.koin.compiler.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("kmpLibrary") {
            id = "flix.kmp.library"
            implementationClass = "com.kserocki.flix.buildlogic.KmpLibraryConventionPlugin"
        }
        register("androidLibrary") {
            id = "flix.android.library"
            implementationClass = "com.kserocki.flix.buildlogic.AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "flix.android.feature"
            implementationClass = "com.kserocki.flix.buildlogic.AndroidFeatureConventionPlugin"
        }
        register("androidApplication") {
            id = "flix.android.application"
            implementationClass = "com.kserocki.flix.buildlogic.AndroidApplicationConventionPlugin"
        }
        register("compose") {
            id = "flix.compose"
            implementationClass = "com.kserocki.flix.buildlogic.ComposeConventionPlugin"
        }
        register("koin") {
            id = "flix.koin"
            implementationClass = "com.kserocki.flix.buildlogic.KoinConventionPlugin"
        }
        register("quality") {
            id = "flix.quality"
            implementationClass = "com.kserocki.flix.buildlogic.QualityConventionPlugin"
        }
    }
}
