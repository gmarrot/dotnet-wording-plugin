package com.betomorrow.gradle.wording

import org.apache.commons.io.FileUtils
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
    }

    @Test
    fun `test plugin should be applied successfully and create downloadWording, updateWording and upgradeWording tasks`() {
        // Given
        FileUtils.copyFile(File("src/integration-test/resources/build_update_only.gradle"), buildFile)

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
    fun `test plugin should be applied successfully and create checkWording task when has statesColumn for languages`() {
        // Given
        FileUtils.copyFile(File("src/integration-test/resources/build_update_and_check.gradle"), buildFile)

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
            "upgradeWording -",
            "checkWording -",
            "checkWordingDefault",
            "checkWordingFr",
            "checkWordingEs"
        )
    }

    @Test
    fun `test plugin should be applied successfully with Gradle 5_0`() {
        // Given
        FileUtils.copyFile(File("src/integration-test/resources/build_update_and_check.gradle"), buildFile)

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
            "upgradeWording -",
            "checkWording -",
            "checkWordingDefault",
            "checkWordingFr",
            "checkWordingEs"
        )
    }

    @Test
    fun `test updateWording task should depend on all updateWordingLanguage tasks`() {
        // Given
        FileUtils.copyFile(File("src/integration-test/resources/build_update_only.gradle"), buildFile)

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
        // Given
        FileUtils.copyFile(File("src/integration-test/resources/build_update_only.gradle"), buildFile)

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

    @Test
    fun `test checkWording task should depend on all checkWordingLanguage tasks`() {
        // Given
        FileUtils.copyFile(File("src/integration-test/resources/build_update_and_check.gradle"), buildFile)

        // When
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("checkWording", "-dry-run", "-quiet")
            .withPluginClasspath()
            .withDebug(true)
            .build()

        // Then
        assertThat(result.output).contains(
            """
            :checkWordingDefault SKIPPED
            :checkWordingEs SKIPPED
            :checkWordingFr SKIPPED
            :checkWording SKIPPED
            """.trimIndent()
        )
    }

}