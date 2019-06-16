package com.betomorrow.gradle.wording.domain

class Language(val name: String, valuesColumn: String, commentsColumn: String?) {

    val valuesColumnIndex: Int = Column(valuesColumn).index

    val commentsColumnIndex: Int? = if (!commentsColumn.isNullOrEmpty()) Column(commentsColumn).index else null

}