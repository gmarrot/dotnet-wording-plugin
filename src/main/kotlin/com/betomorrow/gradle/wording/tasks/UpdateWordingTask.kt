package com.betomorrow.gradle.wording.tasks

import com.betomorrow.gradle.wording.domain.Column
import com.betomorrow.gradle.wording.domain.MissingKeyException
import com.betomorrow.gradle.wording.domain.resx.ResxUpdater
import com.betomorrow.gradle.wording.domain.wording.Language
import com.betomorrow.gradle.wording.domain.wording.WordingExtractor
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import java.io.File

open class UpdateWordingTask : DefaultTask() {

    @InputFile
    lateinit var source: File

    @OutputFile
    lateinit var outputFile: File

    @Input
    lateinit var languageName: String

    @Input
    lateinit var keysColumn: String

    @Input
    lateinit var valuesColumn: String

    @Input
    @Optional
    var commentsColumn: String? = null

    @Input
    var skipHeaders: Boolean = true

    @Input
    @Optional
    var sheetNames = emptyList<String>()

    @Input
    var failOnMissingKeys = false

    @Input
    var addMissingKeys = false

    @Input
    var removeNonExistingKeys = false

    @Input
    var sortWording = false

    @TaskAction
    fun update() {
        val extractor = WordingExtractor(
            source.absolutePath,
            Column(keysColumn),
            skipHeaders
        )
        val updater = ResxUpdater(outputFile.absolutePath)

        val language = Language(languageName, valuesColumn, commentsColumn)

        logger.info("Updating wording for $languageName to $outputFile.")

        val wording = extractor.extract(language, sheetNames)
        val updatedKeys = updater.update(wording, addMissingKeys, removeNonExistingKeys, sortWording)

        val missingKeys = wording.keys - updatedKeys
        if (missingKeys.isNotEmpty() && failOnMissingKeys) {
            throw MissingKeyException(missingKeys, outputFile.relativeTo(project.rootDir))
        }
    }

}