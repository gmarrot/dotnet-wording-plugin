package com.betomorrow.gradle.wording.domain.wording

interface Wording {
    val language: Language

    val keys: Set<String>

    fun containsKey(key: String): Boolean
    fun getValue(key: String): String?
    fun getComment(key: String): String?
}
