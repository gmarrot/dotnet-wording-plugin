package com.betomorrow.gradle.wording.domain

import org.assertj.core.api.Assertions
import org.gradle.internal.impldep.org.junit.Rule
import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class ResxUpdaterTest {

    @Rule
    val testProjectDir = TemporaryFolder()

    private lateinit var language: Language

    @BeforeEach
    fun setUp() {
        language = Language("en", "B", "C")
    }

    @Test
    fun `test update should update existing wording`() {
        // Given
        val source = "src/test/resources/StringResources.resx"
        val expected = "src/test/resources/StringResources-expected.resx"

        testProjectDir.create()
        val copy = Paths.get(testProjectDir.root.absolutePath, "TestUpdateExistingWording.resx")
        Files.copy(Paths.get(source), copy, StandardCopyOption.REPLACE_EXISTING)

        val updater = ResxUpdater(copy.toString())

        // When
        updater.update(MutableWording(language).apply {
            addOrUpdate("key1", "Another value 1", "Comment 1")
            addOrUpdate("key2", "Another value 2", null)
            addOrUpdate("key3", "Another value 3", null)
            addOrUpdate("key3", "Another value 3", "Comment 3")
            addOrUpdate("key4", "Another Value 4", "Comment 4")
        }, false)

        // Then
        Assertions.assertThat(copy).hasSameContentAs(Paths.get(expected))
    }

    @Test
    fun `test update should create non-existing wording`() {
        val expected = "src/test/resources/New-StringResources-expected.resx"

        testProjectDir.create()
        val dest = Paths.get(testProjectDir.root.absolutePath, "TestCreateWording.resx")

        val updater = ResxUpdater(dest.toString())

        updater.update(MutableWording(language).apply {
            addOrUpdate("key1", "Another value 1", "Comment 1")
            addOrUpdate("key2", "Another value 2", null)
            addOrUpdate("key3", "Another value 3", "Comment 3")
        }, true)

        Assertions.assertThat(dest).hasSameContentAs(Paths.get(expected))
    }

    @Test
    fun `test update should update existing wording and add missing ones`() {
        val source = "src/test/resources/Partial-StringResources.resx"
        val expected = "src/test/resources/Partial-StringResources-expected.resx"

        testProjectDir.create()
        val copy = Paths.get(testProjectDir.root.absolutePath, "TestPartialUpdateWording.resx")
        Files.copy(Paths.get(source), copy, StandardCopyOption.REPLACE_EXISTING)

        val updater = ResxUpdater(copy.toString())

        updater.update(MutableWording(language).apply {
            addOrUpdate("key1", "Another value 1", "Comment 1")
            addOrUpdate("key2", "Another value 2", null)
            addOrUpdate("key3", "Another value 3", "Comment 3")
        }, true)

        Assertions.assertThat(copy).hasSameContentAs(Paths.get(expected))
    }

}