package com.betomorrow.gradle.wording.domain.wording

import com.betomorrow.gradle.wording.domain.Column

class Language(val name: String, valuesColumn: String, commentsColumn: String?) {

    val valuesColumnIndex: Int = Column(valuesColumn).index

    val commentsColumnIndex: Int? = if (!commentsColumn.isNullOrEmpty()) Column(
        commentsColumn
    ).index else null

}