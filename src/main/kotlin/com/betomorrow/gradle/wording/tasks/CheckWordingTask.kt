package com.betomorrow.gradle.wording.tasks

import com.betomorrow.gradle.wording.domain.Column
import com.betomorrow.gradle.wording.domain.wording.state.InvalidWordingException
import com.betomorrow.gradle.wording.domain.wording.state.WordingState
import com.betomorrow.gradle.wording.domain.wording.state.WordingStateExtractor
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.io.File

open class CheckWordingTask : DefaultTask() {
    @InputFile
    lateinit var source: File

    @Input
    lateinit var languageName: String

    @Input
    lateinit var keysColumn: String

    @Input
    lateinit var statesColumn: String

    @Input
    lateinit var validStates: List<String>

    @Input
    var skipHeaders: Boolean = true

    @Input
    @Optional
    var sheetNames = emptyList<String>()

    @TaskAction
    fun check() {
        val extractor = WordingStateExtractor(source.absolutePath, Column(keysColumn), skipHeaders)

        logger.info("Checking wording for $languageName")

        val wordingStates = extractor.extract(Column(statesColumn), sheetNames)

        val invalidWordingStates = wordingStates.filterNot { validStates.contains(it.state) }
        if (invalidWordingStates.isNotEmpty()) {
            invalidWordingStates.forEach { logInvalidWording(it) }
            throw InvalidWordingException(invalidWordingStates)
        }
    }

    private fun logInvalidWording(wordingState: WordingState) {
        logger.error("Invalid state for key '${wordingState.key}': '${wordingState.state}'")
    }
}
