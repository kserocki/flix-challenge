plugins {
    id("flix.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.domain)
            api(projects.core.common)
            api(libs.kotlin.test)
            api(libs.kotlinx.coroutines.test)
        }
    }
}
