package com.betomorrow.gradle.wording.domain.wording

import com.betomorrow.gradle.wording.domain.Column
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class WordingExtractorTest {
    @Test
    fun `test extract should return correct English wording`() {
        // Given
        val extractor = WordingExtractor(
            "src/test/resources/wording.xlsx",
            Column("A")
        )
        val language = Language("en", "C", "B")

        // When
        val en = extractor.extract(language)

        // Then
        assertThat(en.keys).hasSize(7)

        assertThat(en.getValue("key1")).isEqualTo("Value 1")
        assertThat(en.getComment("key1")).isEqualTo("Sample key 1")

        assertThat(en.getValue("key2")).isEqualTo("Value 2")
        assertThat(en.getComment("key2")).isEqualTo("Sample key 2")

        assertThat(en.getValue("key3")).isEqualTo("Value 3")
        assertThat(en.getComment("key3")).isNull()

        assertThat(en.getValue("key4")).isEqualTo("Value 4")
        assertThat(en.getComment("key4")).isEqualTo("Sample key 4")

        assertThat(en.getValue("key5")).isEqualTo("Value 5")
        assertThat(en.getComment("key5")).isNull()

        assertThat(en.getValue("key6")).isEqualTo("Value 6")
        assertThat(en.getComment("key6")).isEqualTo("Sample key 6")

        assertThat(en.getValue("key7")).isEqualTo("Value 7")
        assertThat(en.getComment("key7")).isEqualTo("Sample key 7")
    }

    @Test
    fun `test extract should return correct French wording`() {
        // Given
        val extractor = WordingExtractor(
            "src/test/resources/wording.xlsx",
            Column("A")
        )
        val language = Language("fr", "D", "B")

        // When
        val en = extractor.extract(language)

        // Then
        assertThat(en.keys).hasSize(7)

        assertThat(en.getValue("key1")).isEqualTo("Valeur 1")
        assertThat(en.getComment("key1")).isEqualTo("Sample key 1")

        assertThat(en.getValue("key2")).isEqualTo("Valeur 2")
        assertThat(en.getComment("key2")).isEqualTo("Sample key 2")

        assertThat(en.getValue("key3")).isEqualTo("Valeur 3")
        assertThat(en.getComment("key3")).isNull()

        assertThat(en.getValue("key4")).isEqualTo("Valeur 4")
        assertThat(en.getComment("key4")).isEqualTo("Sample key 4")

        assertThat(en.getValue("key5")).isEqualTo("Valeur 5")
        assertThat(en.getComment("key5")).isNull()

        assertThat(en.getValue("key6")).isEqualTo("Valeur 6")
        assertThat(en.getComment("key6")).isEqualTo("Sample key 6")

        assertThat(en.getValue("key7")).isEqualTo("Valeur 7")
        assertThat(en.getComment("key7")).isEqualTo("Sample key 7")
    }

    @Test
    fun `test extract should return correct Spanish wording`() {
        // Given
        val extractor = WordingExtractor(
            "src/test/resources/wording.xlsx",
            Column("A")
        )
        val language = Language("en", "E", "B")

        // When
        val en = extractor.extract(language)

        // Then
        assertThat(en.keys).hasSize(7)

        assertThat(en.getValue("key1")).isEqualTo("Valor 1")
        assertThat(en.getComment("key1")).isEqualTo("Sample key 1")

        assertThat(en.getValue("key2")).isEqualTo("Valor 2")
        assertThat(en.getComment("key2")).isEqualTo("Sample key 2")

        assertThat(en.getValue("key3")).isEqualTo("Valor 3")
        assertThat(en.getComment("key3")).isNull()

        assertThat(en.getValue("key4")).isEqualTo("Valor 4")
        assertThat(en.getComment("key4")).isEqualTo("Sample key 4")

        assertThat(en.getValue("key5")).isEqualTo("Valor 5")
        assertThat(en.getComment("key5")).isNull()

        assertThat(en.getValue("key6")).isEqualTo("Valor 6")
        assertThat(en.getComment("key6")).isEqualTo("Sample key 6")

        assertThat(en.getValue("key7")).isEqualTo("Valor 7")
        assertThat(en.getComment("key7")).isEqualTo("Sample key 7")
    }

    @Test
    fun `test extract should return wording containing only given sheets' content`() {
        // Given
        val extractor = WordingExtractor(
            "src/test/resources/wording.xlsx",
            Column("A")
        )
        val language = Language("en", "C", "B")

        // When
        val en = extractor.extract(language, listOf("Sheet 2"))

        // Then
        assertThat(en.keys).hasSize(2)

        assertThat(en.getValue("key6")).isEqualTo("Value 6")
        assertThat(en.getComment("key6")).isEqualTo("Sample key 6")

        assertThat(en.getValue("key7")).isEqualTo("Value 7")
        assertThat(en.getComment("key7")).isEqualTo("Sample key 7")
    }

    @Test
    fun `test extract should return correct wording when file has no header`() {
        // Given
        val extractor = WordingExtractor(
            "src/test/resources/wording-without-header.xlsx",
            Column("A"),
            false
        )
        val language = Language("en", "C", "B")

        // When
        val en = extractor.extract(language)

        // Then
        assertThat(en.keys).hasSize(2)

        assertThat(en.getValue("key1")).isEqualTo("Value 1")
        assertThat(en.getComment("key1")).isEqualTo("Sample key 1")

        assertThat(en.getValue("key2")).isEqualTo("Value 2")
        assertThat(en.getComment("key2")).isEqualTo("Sample key 2")
    }
}
