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
    }

    @Test
    fun `test plugin should be applied successfully and create downloadWording, updateWording and upgradeWording tasks`() {
        // When
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("tasks", "--stacktrace", "--all")
            .withPluginClasspath()
            .withDebug(true)
            .build()

        // Then
        assertThat(result.task(":tasks")!!.outcome).isEqualTo(TaskOutcome.SUCCESS)

        assertThat(result.output).contains(
            "downloadWording -",
            "updateWording -",
            "updateWordingDefault -",
            "updateWordingFr -",
            "updateWordingEs -",
            "upgradeWording -"
        )
    }

    @Test
    fun `test plugin should be applied successfully with Gradle 5_0`() {
        // When
        val result = GradleRunner.create()
            .withGradleVersion("5.0")
            .withProjectDir(testProjectDir.root)
            .withArguments("tasks", "--stacktrace", "--all")
            .withPluginClasspath()
            .withDebug(true)
            .build()

        // Then
        assertThat(result.task(":tasks")!!.outcome).isEqualTo(TaskOutcome.SUCCESS)

        assertThat(result.output).contains(
            "downloadWording -",
            "updateWording -",
            "updateWordingDefault -",
            "updateWordingFr -",
            "updateWordingEs -",
            "upgradeWording -"
        )
    }

    @Test
    fun `test updateWording task should depend on all updateWordingLanguage tasks`() {
        // When
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("updateWording", "-dry-run", "--quiet")
            .withPluginClasspath()
            .withDebug(true)
            .build()

        // Then
        assertThat(result.output).contains(
            """
            :updateWordingDefault SKIPPED
            :updateWordingEs SKIPPED
            :updateWordingFr SKIPPED
            :updateWording SKIPPED
            """.trimIndent()
        )
        assertThat(result.output).doesNotContain(
            ":downloadWording",
            ":upgradeWording"
        )
    }

    @Test
    fun `test upgradeWording task should depend on downloadWording and updateWording tasks`() {
        // When
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("upgradeWording", "-dry-run", "-quiet")
            .withPluginClasspath()
            .withDebug(true)
            .build()

        // Then
        assertThat(result.output).contains(
            """
            :downloadWording SKIPPED
            :updateWordingDefault SKIPPED
            :updateWordingEs SKIPPED
            :updateWordingFr SKIPPED
            :updateWording SKIPPED
            :upgradeWording SKIPPED
            """.trimIndent()
        )
    }

}