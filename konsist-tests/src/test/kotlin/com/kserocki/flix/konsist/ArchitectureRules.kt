package com.kserocki.flix.konsist

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertFalse
import com.lemonappdev.konsist.api.verify.assertTrue

// AGENTS.md §3, rules 1-5 (ADR-0005). Each rule is a KoScope extension so it runs against both
// the real project scope and a fixture scope that proves it fails.

private val domainForbiddenImportPrefixes =
    listOf("io.ktor.", "android.", "androidx.", "org.jetbrains.compose.", "org.koin.")
private val featurePackageRegex = Regex("""^com\.kserocki\.flix\.feature\.([^.]+)\.impl(\..*)?$""")

/** Rule 1: `core:domain` imports nothing from Ktor, Compose, Android, AndroidX or Koin. */
fun KoScope.assertDomainHasNoFrameworkImports() {
    files
        .withPackage("com.kserocki.flix.core.domain..")
        .assertFalse { file ->
            file.hasImport { import -> domainForbiddenImportPrefixes.any { import.hasNameStartingWith(it) } }
        }
}

/** Rule 2: no `feature:*:impl` imports another feature's `impl` package. */
fun KoScope.assertFeatureImplDoesNotImportAnotherFeatureImpl() {
    files
        .withPackage("com.kserocki.flix.feature..impl..")
        .assertFalse { file ->
            val ownFeature = featurePackageRegex.find(file.packagee?.name.orEmpty())?.groupValues?.get(1)
            file.hasImport { import ->
                val importedFeature = featurePackageRegex.find(import.name)?.groupValues?.get(1)
                importedFeature != null && importedFeature != ownFeature
            }
        }
}

/** Rule 3: no `feature:*:impl` imports `core:data`. */
fun KoScope.assertFeatureImplDoesNotImportCoreData() {
    files
        .withPackage("com.kserocki.flix.feature..impl..")
        .assertFalse { file -> file.hasImport { it.hasNameStartingWith("com.kserocki.flix.core.data") } }
}

/** Rule 4: classes ending in `Dto` live only in `core:data`. */
fun KoScope.assertDtoClassesResideOnlyInCoreData() {
    classes()
        .withNameEndingWith("Dto")
        .assertTrue { it.resideInPackage("com.kserocki.flix.core.data..") }
}

/** Rule 5a: ViewModels end with `ViewModel`, sit in a `presentation` package and have `@KoinViewModel`. */
fun KoScope.assertViewModelsAreNamedScopedAndAnnotated() {
    classes()
        .filter { it.hasParentWithName("ViewModel") }
        .assertTrue {
            it.hasNameEndingWith("ViewModel") &&
                it.resideInPackage("..presentation..") &&
                it.hasAnnotationWithName("KoinViewModel")
        }
}

/** Rule 5b: use cases end with `UseCase` and expose exactly one public function, `operator fun invoke`. */
fun KoScope.assertUseCasesHaveSingleInvokeOperator() {
    classes()
        .withNameEndingWith("UseCase")
        .assertTrue { useCase ->
            val publicFunctions = useCase.functions(includeNested = false, includeLocal = false)
                .filter { it.hasPublicOrDefaultModifier }
            publicFunctions.size == 1 && publicFunctions.single().let { it.hasName("invoke") && it.hasOperatorModifier }
        }
}
