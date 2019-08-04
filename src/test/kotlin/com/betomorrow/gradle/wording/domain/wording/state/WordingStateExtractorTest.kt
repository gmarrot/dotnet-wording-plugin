package com.betomorrow.gradle.wording.domain.wording.state

import com.betomorrow.gradle.wording.domain.Column
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class WordingStateExtractorTest {

    @Test
    fun `test extract should return correct English wording state`() {
        // Given
        val extractor = WordingStateExtractor(
            "src/test/resources/wording-with-states.xlsx",
            Column("A")
        )

        // When
        val en = extractor.extract(Column("D"))

        // Then
        assertThat(en).hasSize(7)
        assertThat(en).containsExactly(
            WordingState("key1", "Validated"),
            WordingState("key2", "Validated"),
            WordingState("key3", "Validated"),
            WordingState("key4", "Validated"),
            WordingState("key5", "Validated"),
            WordingState("key6", "Validated"),
            WordingState("key7", "Validated")
        )
    }

    @Test
    fun `test extract should return correct French wording state`() {
        // Given
        val extractor = WordingStateExtractor(
            "src/test/resources/wording-with-states.xlsx",
            Column("A")
        )

        // When
        val fr = extractor.extract(Column("F"))

        // Then
        assertThat(fr).hasSize(7)
        assertThat(fr).containsExactly(
            WordingState("key1", "Validated"),
            WordingState("key2", ""),
            WordingState("key3", "Validated"),
            WordingState("key4", "Validated"),
            WordingState("key5", "Validated"),
            WordingState("key6", "To validate"),
            WordingState("key7", "Validated")
        )
    }

    @Test
    fun `test extract should return correct Spanish wording state`() {
        // Given
        val extractor = WordingStateExtractor(
            "src/test/resources/wording-with-states.xlsx",
            Column("A")
        )

        // When
        val es = extractor.extract(Column("H"))

        // Then
        assertThat(es).hasSize(7)
        assertThat(es).containsExactly(
            WordingState("key1", "Validated"),
            WordingState("key2", "Validated"),
            WordingState("key3", "Validated"),
            WordingState("key4", "Validated"),
            WordingState("key5", "Validated"),
            WordingState("key6", "Validated"),
            WordingState("key7", "To validate")
        )
    }

    @Test
    fun `test extract should return wording state containing only given sheets' content`() {
        // Given
        val extractor = WordingStateExtractor(
            "src/test/resources/wording-with-states.xlsx",
            Column("A")
        )

        // When
        val en = extractor.extract(Column("D"), listOf("Sheet 2"))

        // Then
        assertThat(en).hasSize(2)
        assertThat(en).containsExactly(
            WordingState("key6", "Validated"),
            WordingState("key7", "Validated")
        )
    }

    @Test
    fun `test extract should return correct wording state when file has no header`() {
        // Given
        val extractor = WordingStateExtractor(
            "src/test/resources/wording-with-states-without-header.xlsx",
            Column("A"),
            false
        )

        // When
        val en = extractor.extract(Column("D"))

        // Then
        Assertions.assertThat(en).hasSize(7)
        assertThat(en).containsExactly(
            WordingState("key1", "Validated"),
            WordingState("key2", "Validated"),
            WordingState("key3", "Validated"),
            WordingState("key4", "Validated"),
            WordingState("key5", "Validated"),
            WordingState("key6", "Validated"),
            WordingState("key7", "Validated")
        )
    }

}