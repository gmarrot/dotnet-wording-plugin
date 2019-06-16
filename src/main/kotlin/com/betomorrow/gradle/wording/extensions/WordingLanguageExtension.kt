package com.betomorrow.gradle.wording.extensions

import org.gradle.api.GradleException
import org.gradle.api.Project
import java.io.File

open class WordingLanguageExtension(val name: String, val project: Project) {

    lateinit var output: String

    lateinit var column: String

    val isDefault: Boolean
        get() = name == DEFAULT_NAME

    val outputFile: File
        get() {
            val file = project.projectDir.resolve(output)
            if (file.exists() && !file.isFile) {
                throw GradleException("Wrong output for language $name")
            }

            return file
        }

    companion object {
        const val DEFAULT_NAME = "default"
    }

}