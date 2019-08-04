package com.betomorrow.gradle.wording.extensions

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import java.io.File

const val WORDING_EXTENSION_NAME = "wording"

open class WordingPluginExtension(val project: Project) {

    var credentials: String? = null
    var clientId: String? = null
    var clientSecret: String? = null

    lateinit var sheetId: String
    var sheetNames: List<String> = emptyList()

    var filename = "wording.xlsx"
    val wordingFile: File
        get() = project.rootDir.resolve(filename)

    var keysColumn = "A"
    var commentsColumn: String? = null

    var skipHeaders = true

    var addMissingKeys = false
    var removeNonExistingKeys = false

    var sortWording = false

    var validWordingStates: List<String> = emptyList()

    var languages: NamedDomainObjectContainer<WordingLanguageExtension> =
        project.container(WordingLanguageExtension::class.java) {
            WordingLanguageExtension(it, project)
        }

    fun languages(action: Action<NamedDomainObjectContainer<WordingLanguageExtension>>) {
        action.execute(languages)
    }

    fun languageValidWordingStates(languageName: String): List<String> {
        val validStates = languages.first { it.name == languageName }.validWordingStates
        return if (validStates.isNullOrEmpty()) {
            validWordingStates
        } else {
            validStates
        }
    }

}