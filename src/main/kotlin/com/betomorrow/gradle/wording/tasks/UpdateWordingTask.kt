package com.betomorrow.gradle.wording.tasks

import com.betomorrow.gradle.wording.domain.*
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

open class UpdateWordingTask : DefaultTask() {

    @InputFile
    lateinit var source: File

    @OutputFile
    lateinit var outputFile: File

    lateinit var languageName: String
    var skipHeaders: Boolean = true
    lateinit var keysColumn: String
    lateinit var valuesColumn: String
    var commentsColumn: String? = null

    var sheetNames = emptyList<String>()
    var failOnMissingKeys = false
    var addMissingKeys = false

    @TaskAction
    fun update() {
        val extractor = XlsxExtractor(source.absolutePath, Column(keysColumn), skipHeaders)
        val updater = ResxUpdater(outputFile.absolutePath)

        val language = Language(languageName, valuesColumn, commentsColumn)

        logger.info("Updating wording for $languageName to $outputFile.")

        val wording = extractor.extract(language, sheetNames)
        val updatedKeys = updater.update(wording, addMissingKeys)

        val missingKeys = wording.keys - updatedKeys
        if (missingKeys.isNotEmpty() && failOnMissingKeys) {
            throw MissingKeyException(missingKeys, outputFile.relativeTo(project.rootDir))
        }
    }

}