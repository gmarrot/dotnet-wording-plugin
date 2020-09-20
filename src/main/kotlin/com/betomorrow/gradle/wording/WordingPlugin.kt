package com.betomorrow.gradle.wording

import com.betomorrow.gradle.wording.extensions.WORDING_EXTENSION_NAME
import com.betomorrow.gradle.wording.extensions.WordingLanguageExtension
import com.betomorrow.gradle.wording.extensions.WordingPluginExtension
import com.betomorrow.gradle.wording.tasks.CheckWordingTask
import com.betomorrow.gradle.wording.tasks.DownloadWordingTask
import com.betomorrow.gradle.wording.tasks.UpdateWordingTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("UNUSED")
class WordingPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.create(WORDING_EXTENSION_NAME, WordingPluginExtension::class.java, project)

            afterEvaluate { p ->
                val wordingExtension = extensions.getByType(WordingPluginExtension::class.java)

                val downloadWordingTask = tasks.register("downloadWording", DownloadWordingTask::class.java) { t ->
                    t.group = GROUP
                    t.description = "Download translation."

                    t.credentials = wordingExtension.credentials?.let { project.rootDir.resolve(it) }
                    t.clientId = wordingExtension.clientId
                    t.clientSecret = wordingExtension.clientSecret

                    t.fileId = wordingExtension.sheetId
                    t.output = wordingExtension.wordingFile

                    t.outputs.upToDateWhen { false }
                }.get()

                val updateWordingTasks = mutableListOf<UpdateWordingTask>()
                val checkWordingTasks = mutableListOf<CheckWordingTask>()

                wordingExtension.languages.forEach { language ->
                    val updateLanguageWordingTask = createUpdateWordingTask(p, wordingExtension, language)
                    updateLanguageWordingTask.mustRunAfter(downloadWordingTask)
                    updateWordingTasks.add(updateLanguageWordingTask)

                    val checkLanguageWordingTask = createCheckWordingTask(p, wordingExtension, language)
                    if (checkLanguageWordingTask != null) {
                        checkLanguageWordingTask.mustRunAfter(downloadWordingTask)
                        checkWordingTasks.add(checkLanguageWordingTask)
                    }
                }

                if (updateWordingTasks.isNotEmpty()) {
                    val updateWordingTask = tasks.register("updateWording") { t ->
                        t.group = GROUP
                        t.description = "Update all wording files."
                    }.get()
                    updateWordingTask.mustRunAfter(downloadWordingTask)

                    updateWordingTasks.forEach { updateWordingTask.dependsOn(it) }

                    tasks.register("upgradeWording") { t ->
                        t.group = GROUP
                        t.description = "Download and update all wording files."

                        t.dependsOn(downloadWordingTask, updateWordingTask)
                    }
                }

                if (checkWordingTasks.isNotEmpty()) {
                    val checkWordingTask = tasks.register("checkWording") { t ->
                        t.group = CHECK_GROUP
                        t.description = "Check all wording languages."
                    }.get()
                    checkWordingTask.mustRunAfter(downloadWordingTask)

                    checkWordingTasks.forEach { checkWordingTask.dependsOn(it) }
                }
            }
        }
    }

    private fun createUpdateWordingTask(
        project: Project,
        wordingExtension: WordingPluginExtension,
        language: WordingLanguageExtension
    ): UpdateWordingTask {
        val languageName = language.name.capitalize()

        return project.tasks.register("updateWording$languageName", UpdateWordingTask::class.java) { t ->
            t.group = GROUP
            t.description = "Update wording file ${language.outputFile.relativeTo(project.projectDir)}."

            t.source = wordingExtension.wordingFile
            t.outputFile = language.outputFile

            t.languageName = languageName
            t.keysColumn = wordingExtension.keysColumn
            t.valuesColumn = language.column
            t.commentsColumn = wordingExtension.commentsColumn

            t.skipHeaders = wordingExtension.skipHeaders
            t.sheetNames = wordingExtension.sheetNames
            t.failOnMissingKeys = language.isDefault
            t.addMissingKeys = wordingExtension.addMissingKeys
            t.removeNonExistingKeys = wordingExtension.removeNonExistingKeys
            t.sortWording = wordingExtension.sortWording
        }.get()
    }

    private fun createCheckWordingTask(
        project: Project,
        wordingExtension: WordingPluginExtension,
        language: WordingLanguageExtension
    ): CheckWordingTask? {
        val languageName = language.name.capitalize()

        return language.statesColumn?.let { statesColumn ->
            val validStates = wordingExtension.languageValidWordingStates(language.name)
            if (validStates.isNullOrEmpty()) {
                throw GradleException("No valid states specified for wording check")
            }

            project.tasks.register("checkWording$languageName", CheckWordingTask::class.java) { t ->
                t.group = CHECK_GROUP
                t.description = "Check wording for $languageName language."

                t.source = wordingExtension.wordingFile

                t.languageName = languageName
                t.keysColumn = wordingExtension.keysColumn
                t.statesColumn = statesColumn
                t.validStates = validStates

                t.skipHeaders = wordingExtension.skipHeaders
                t.sheetNames = wordingExtension.sheetNames
            }.get()
        }
    }

    companion object {
        const val GROUP = "Wording"
        const val CHECK_GROUP = "Check Wording"
    }
}
