package com.kserocki.flix.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.core.exception.KoAssertionFailedException
import kotlin.test.Test
import kotlin.test.assertFailsWith

/**
 * AGENTS.md §3 / ADR-0005: the 5 architecture rules (6 checks, rule 5 splits into ViewModels and
 * use cases). Each rule has one test on the real project and one on a fixture that proves it fails.
 */
class ArchitectureRulesTest {

    // Excludes this module's own tests and fixtures (which intentionally violate every rule) and
    // build-logic's convention plugins (a separate included build, not application source).
    private val projectScope: KoScope =
        Konsist.scopeFromProject() -
            Konsist.scopeFromModule("konsist-tests") -
            Konsist.scopeFromModule("build-logic/convention")

    private fun fixtureScope(id: String): KoScope = Konsist.scopeFromDirectory("konsist-tests/src/test/fixtures/$id")

    @Test
    fun `domain imports no framework packages`() {
        projectScope.assertDomainHasNoFrameworkImports()
    }

    @Test
    fun `domain rule fails on a ktor import`() {
        assertFailsWith<KoAssertionFailedException> {
            fixtureScope("rule1").assertDomainHasNoFrameworkImports()
        }
    }

    @Test
    fun `feature impl does not import another feature impl`() {
        projectScope.assertFeatureImplDoesNotImportAnotherFeatureImpl()
    }

    @Test
    fun `feature impl rule fails on a cross-feature import`() {
        assertFailsWith<KoAssertionFailedException> {
            fixtureScope("rule2").assertFeatureImplDoesNotImportAnotherFeatureImpl()
        }
    }

    @Test
    fun `feature impl does not import core data`() {
        projectScope.assertFeatureImplDoesNotImportCoreData()
    }

    @Test
    fun `feature impl rule fails on a core data import`() {
        assertFailsWith<KoAssertionFailedException> {
            fixtureScope("rule3").assertFeatureImplDoesNotImportCoreData()
        }
    }

    @Test
    fun `dto classes reside only in core data`() {
        projectScope.assertDtoClassesResideOnlyInCoreData()
    }

    @Test
    fun `dto rule fails on a dto outside core data`() {
        assertFailsWith<KoAssertionFailedException> {
            fixtureScope("rule4").assertDtoClassesResideOnlyInCoreData()
        }
    }

    @Test
    fun `view models are named scoped and annotated`() {
        projectScope.assertViewModelsAreNamedScopedAndAnnotated()
    }

    @Test
    fun `view model rule fails without the koin annotation`() {
        assertFailsWith<KoAssertionFailedException> {
            fixtureScope("rule5a").assertViewModelsAreNamedScopedAndAnnotated()
        }
    }

    @Test
    fun `use cases expose a single invoke operator`() {
        projectScope.assertUseCasesHaveSingleInvokeOperator()
    }

    @Test
    fun `use case rule fails with an extra public function`() {
        assertFailsWith<KoAssertionFailedException> {
            fixtureScope("rule5b").assertUseCasesHaveSingleInvokeOperator()
        }
    }
}
