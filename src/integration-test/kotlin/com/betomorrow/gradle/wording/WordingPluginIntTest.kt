package com.betomorrow.gradle.wording

import org.assertj.core.api.Assertions.assertThat
import org.gradle.internal.impldep.org.junit.Rule
import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class WordingPluginIntTest {

    @Rule
    val testProjectDir = TemporaryFolder()

    private lateinit var buildFile: File

    @BeforeEach
    fun setUp() {
        testProjectDir.create()
        testProjectDir.newFolder("SampleApp", "Res")
        buildFile = testProjectDir.newFile("build.gradle")
        buildFile.appendText(
            """
            plugins {
                id 'com.betomorrow.dotnet.wording'
            }

            """.trimIndent()
        )
    }

    @Test
    fun `test plugin should be applied successfully`() {
        // Given
        buildFile.appendText(
            """
            wording {
                credentials = "~/.credentials.json"

                clientId = ""
                clientSecret = ""

                sheetId = "qwertyuiop"
                sheetNames = ["commons", "app"]
                filename = "wording.xlsx"
                skipHeaders = true
                keysColumn = "A"
                commentsColumn = "B"
                languages {
                    'default' {
                        output = "SampleApp/Res/StringResources.resx"
                        column = "C"
                    }
                    'fr' {
                        output = "SampleApp/Res/StringResources.fr.resx"
                        column = "D"
                    }
                    'es' {
                        output = "SampleApp/Res/StringResources.es.resx"
                        column = "E"
                    }
                }
            }
            """.trimIndent()
        )

        // When
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("tasks", "--stacktrace", "--all")
            .withPluginClasspath()
            .withDebug(true)
            .build()

        // Then
        assertThat(result.task(":tasks")!!.outcome).isEqualTo(TaskOutcome.SUCCESS)

        assertThat(result.output).contains("downloadWording -")
        assertThat(result.output).contains("updateWording -")
        assertThat(result.output).contains("updateWordingDefault -")
        assertThat(result.output).contains("updateWordingFr -")
        assertThat(result.output).contains("updateWordingEs -")
        assertThat(result.output).contains("upgradeWording -")
    }

}