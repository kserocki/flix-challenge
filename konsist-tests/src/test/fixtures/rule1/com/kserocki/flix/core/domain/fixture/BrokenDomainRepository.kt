package com.kserocki.flix.core.domain.fixture

import io.ktor.client.HttpClient

/** Violates rule 1: a domain-package file must not import Ktor. */
class BrokenDomainRepository(private val client: HttpClient)
