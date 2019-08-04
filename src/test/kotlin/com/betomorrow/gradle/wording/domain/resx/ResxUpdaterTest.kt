package com.betomorrow.gradle.wording.domain.resx

import com.betomorrow.gradle.wording.domain.wording.Language
import com.betomorrow.gradle.wording.domain.wording.MutableWording
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
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
    fun `test update should only update existing wording when wording file exists and addMissingWording is false`() {
        // Given
        val source = "src/test/resources/wording_update/StringResources.resx"
        val expected = "src/test/resources/wording_update/StringResources-expected.resx"

        testProjectDir.create()
        val copy = Paths.get(testProjectDir.root.absolutePath, "TestUpdateWording.resx")
        Files.copy(Paths.get(source), copy, StandardCopyOption.REPLACE_EXISTING)

        val updater = ResxUpdater(copy.toString())

        // When
        updater.update(MutableWording(language).apply {
            addOrUpdate("key1", "Another value 1", "Comment 1")
            addOrUpdate("key2", "Another value 2", null)
            addOrUpdate("key3", "Another value 3", "Comment 3")
            addOrUpdate("key4", "Another value 4", "Comment 4")
        })

        // Then
        assertThat(copy).hasSameContentAs(Paths.get(expected))
    }

    @Test
    fun `test update should update existing wording and add missing ones when rex file exists and addMissingWording is true`() {
        val source = "src/test/resources/wording_add_or_update/StringResources.resx"
        val expected = "src/test/resources/wording_add_or_update/StringResources-expected.resx"

        testProjectDir.create()
        val copy = Paths.get(testProjectDir.root.absolutePath, "TestAddOrUpdateWording.resx")
        Files.copy(Paths.get(source), copy, StandardCopyOption.REPLACE_EXISTING)

        val updater = ResxUpdater(copy.toString())

        updater.update(MutableWording(language).apply {
            addOrUpdate("key1", "Another value 1", "Comment 1")
            addOrUpdate("key2", "Another value 2", null)
            addOrUpdate("key3", "Another value 3", "Comment 3")
        }, addMissingWordings = true)

        Assertions.assertThat(copy).hasSameContentAs(Paths.get(expected))
    }

    @Test
    fun `test update should update existing wording and remove non-existing ones when removeNonExistingWording is true`() {
        // Given
        val source = "src/test/resources/wording_update_or_remove/StringResources.resx"
        val expected = "src/test/resources/wording_update_or_remove/StringResources-expected.resx"

        testProjectDir.create()
        val copy = Paths.get(testProjectDir.root.absolutePath, "TestAddOrUpdateWording.resx")
        Files.copy(Paths.get(source), copy, StandardCopyOption.REPLACE_EXISTING)

        val updater = ResxUpdater(copy.toString())

        // When
        updater.update(MutableWording(language).apply {
            addOrUpdate("key1", "Another value 1", "Comment 1")
            addOrUpdate("key2", "Another value 2", null)
            addOrUpdate("key3", "Another value 3", "Comment 3")
        }, removeNonExistingWording = true)

        // Then
        assertThat(copy).hasSameContentAs(Paths.get(expected))
    }

    @Test
    fun `test update should update existing wording and add missing ones and remove non-existing ones when addMissingWording and removeNonExistingWording are true`() {
        // Given
        val source = "src/test/resources/wording_add_update_or_remove/StringResources.resx"
        val expected = "src/test/resources/wording_add_update_or_remove/StringResources-expected.resx"

        testProjectDir.create()
        val copy = Paths.get(testProjectDir.root.absolutePath, "TestAddOrUpdateWording.resx")
        Files.copy(Paths.get(source), copy, StandardCopyOption.REPLACE_EXISTING)

        val updater = ResxUpdater(copy.toString())

        // When
        updater.update(MutableWording(language).apply {
            addOrUpdate("key1", "Another value 1", "Comment 1")
            addOrUpdate("key2", "Another value 2", null)
            addOrUpdate("key3", "Another value 3", "Comment 3")
        }, addMissingWordings = true, removeNonExistingWording = true)

        // Then
        assertThat(copy).hasSameContentAs(Paths.get(expected))
    }

    @Test
    fun `test update should create non-existing wording when resx file does not exists and addMissingWording is true`() {
        // Given
        val expected = "src/test/resources/wording_creation/StringResources-expected.resx"

        testProjectDir.create()
        val dest = Paths.get(testProjectDir.root.absolutePath, "TestCreateWording.resx")

        val updater = ResxUpdater(dest.toString())

        // When
        updater.update(MutableWording(language).apply {
            addOrUpdate("key1", "Another value 1", "Comment 1")
            addOrUpdate("key2", "Another value 2", null)
            addOrUpdate("key3", "Another value 3", "Comment 3")
        }, addMissingWordings = true)

        // Then
        assertThat(dest).hasSameContentAs(Paths.get(expected))
    }

    // Sort tests

    @Test
    fun `test update should sort wording when wording file exists and sortWording is true and addMissingWording and removeNonExistingWording are false`() {
        // Given
        val source = "src/test/resources/wording_update_and_sort/StringResources.resx"
        val expected = "src/test/resources/wording_update_and_sort/StringResources-expected.resx"

        testProjectDir.create()
        val copy = Paths.get(testProjectDir.root.absolutePath, "TestUpdateSortWording.resx")
        Files.copy(Paths.get(source), copy, StandardCopyOption.REPLACE_EXISTING)

        val updater = ResxUpdater(copy.toString())

        // When
        updater.update(MutableWording(language).apply {
            addOrUpdate("key1", "Another value 1", "Comment 1")
            addOrUpdate("key2", "Another value 2", null)
            addOrUpdate("key3", "Another value 3", "Comment 3")
            addOrUpdate("key4", "Another value 4", "Comment 4")
        }, sortWording = true)

        // Then
        assertThat(copy).hasSameContentAs(Paths.get(expected))
    }

    @Test
    fun `test update should sort wording when wording file exists and sortWording is true and addMissingWording is true and removeNonExistingWording is false`() {
        // Given
        val source = "src/test/resources/wording_add_update_and_sort/StringResources.resx"
        val expected = "src/test/resources/wording_add_update_and_sort/StringResources-expected.resx"

        testProjectDir.create()
        val copy = Paths.get(testProjectDir.root.absolutePath, "TestAddUpdateSortWording.resx")
        Files.copy(Paths.get(source), copy, StandardCopyOption.REPLACE_EXISTING)

        val updater = ResxUpdater(copy.toString())

        // When
        updater.update(MutableWording(language).apply {
            addOrUpdate("key1", "Another value 1", "Comment 1")
            addOrUpdate("key2", "Another value 2", null)
            addOrUpdate("key3", "Another value 3", "Comment 3")
            addOrUpdate("key4", "Another value 4", "Comment 4")
        }, addMissingWordings = true, sortWording = true)

        // Then
        assertThat(copy).hasSameContentAs(Paths.get(expected))
    }

    @Test
    fun `test update should sort wording when wording file exists and sortWording is true and addMissingWording is false and removeNonExistingWording is true`() {
        // Given
        val source = "src/test/resources/wording_update_remove_and_sort/StringResources.resx"
        val expected = "src/test/resources/wording_update_remove_and_sort/StringResources-expected.resx"

        testProjectDir.create()
        val copy = Paths.get(testProjectDir.root.absolutePath, "TestUpdateRemoveSortWording.resx")
        Files.copy(Paths.get(source), copy, StandardCopyOption.REPLACE_EXISTING)

        val updater = ResxUpdater(copy.toString())

        // When
        updater.update(MutableWording(language).apply {
            addOrUpdate("key1", "Another value 1", "Comment 1")
            addOrUpdate("key2", "Another value 2", null)
            addOrUpdate("key3", "Another value 3", "Comment 3")
            addOrUpdate("key4", "Another value 4", "Comment 4")
        }, removeNonExistingWording = true, sortWording = true)

        // Then
        assertThat(copy).hasSameContentAs(Paths.get(expected))
    }

    @Test
    fun `test update should sort wording when wording file exists and sortWording is true and addMissingWording and removeNonExistingWording are true`() {
        // Given
        val source = "src/test/resources/wording_add_update_remove_and_sort/StringResources.resx"
        val expected = "src/test/resources/wording_add_update_remove_and_sort/StringResources-expected.resx"

        testProjectDir.create()
        val copy = Paths.get(testProjectDir.root.absolutePath, "TestAddUpdateRemoveSortWording.resx")
        Files.copy(Paths.get(source), copy, StandardCopyOption.REPLACE_EXISTING)

        val updater = ResxUpdater(copy.toString())

        // When
        updater.update(MutableWording(language).apply {
            addOrUpdate("key1", "Another value 1", "Comment 1")
            addOrUpdate("key2", "Another value 2", null)
            addOrUpdate("key3", "Another value 3", "Comment 3")
            addOrUpdate("key4", "Another value 4", "Comment 4")
        }, addMissingWordings = true, removeNonExistingWording = true, sortWording = true)

        // Then
        assertThat(copy).hasSameContentAs(Paths.get(expected))
    }

}