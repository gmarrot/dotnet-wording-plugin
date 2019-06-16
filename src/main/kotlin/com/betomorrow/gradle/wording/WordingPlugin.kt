package com.betomorrow.gradle.wording

import com.betomorrow.gradle.wording.extensions.WORDING_EXTENSION_NAME
import com.betomorrow.gradle.wording.extensions.WordingPluginExtension
import com.betomorrow.gradle.wording.tasks.DownloadWordingTask
import com.betomorrow.gradle.wording.tasks.UpdateWordingTask
import org.gradle.api.Plugin
import org.gradle.api.Project

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

                val updateWordingTask = tasks.register("updateWording") { t ->
                    t.group = GROUP
                    t.description = "Update all wording files."
                }.get()
                updateWordingTask.mustRunAfter(downloadWordingTask)

                wordingExtension.languages.forEach { language ->
                    val languageName = language.name.capitalize()

                    val task = p.tasks.register("updateWording$languageName", UpdateWordingTask::class.java) { t ->
                        t.group = GROUP
                        t.description = "Update wording file ${language.outputFile.relativeTo(project.projectDir)}."

                        t.source = wordingExtension.wordingFile
                        t.outputFile = language.outputFile

                        t.languageName = languageName
                        t.skipHeaders = wordingExtension.skipHeaders
                        t.keysColumn = wordingExtension.keysColumn
                        t.valuesColumn = language.column
                        t.commentsColumn = wordingExtension.commentsColumn

                        t.sheetNames = wordingExtension.sheetNames
                        t.failOnMissingKeys = language.isDefault
                        t.addMissingKeys = wordingExtension.addMissingKeys
                    }.get()

                    task.mustRunAfter(downloadWordingTask)
                    updateWordingTask.dependsOn(task)
                }

                tasks.register("upgradeWording") { t ->
                    t.group = GROUP
                    t.description = "Download and update all wording files."

                    t.dependsOn(downloadWordingTask, updateWordingTask)
                }
            }
        }
    }

    companion object {
        const val GROUP = "Wording"
    }

}