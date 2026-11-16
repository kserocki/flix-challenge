package com.kserocki.flix.core.domain.fixture

import org.koin.core.annotation.Factory

/** Violates rule 1: with the domain Koin-free (ADR-0003 option D), no org.koin import is allowed. */
@Factory
class AnnotatedDomainUseCase
