package com.betomorrow.gradle.wording.domain

class UnknownLanguageException(language: String) : Exception("Language $language is unknown")