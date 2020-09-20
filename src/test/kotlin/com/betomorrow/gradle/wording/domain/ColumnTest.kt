package com.betomorrow.gradle.wording.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ColumnTest {
    @Test
    fun `test column with name "A" should have index 0`() {
        val column = Column("a")
        assertThat(column.index).isEqualTo(0)
    }

    @Test
    fun `test column with name "B" should have index 1`() {
        val column = Column("b")
        assertThat(column.index).isEqualTo(1)
    }

    @Test
    fun `test column with name "AA" should have index 26`() {
        val column = Column("aa")
        assertThat(column.index).isEqualTo(26)
    }

    @Test
    fun `test column with name "AB" should have index 27`() {
        val column = Column("ab")
        assertThat(column.index).isEqualTo(27)
    }

    @Test
    fun `test column with name "BA" should have index 52`() {
        val column = Column("ba")
        assertThat(column.index).isEqualTo(52)
    }

    @Test
    fun `test column with name "ABCD" should have index 19009`() {
        val column = Column("abcd")
        assertThat(column.index).isEqualTo(19009)
    }

    @Test
    fun `test column with invalid name should throw exception on construction`() {
        assertThrows<AssertionError> {
            Column("0123456789")
        }
    }
}
