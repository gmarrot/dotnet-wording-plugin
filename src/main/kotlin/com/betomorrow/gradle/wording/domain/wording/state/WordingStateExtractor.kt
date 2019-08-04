package com.betomorrow.gradle.wording.domain.wording.state

import com.betomorrow.gradle.wording.domain.Column
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File

class WordingStateExtractor(
    private val path: String,
    private val keysColumn: Column,
    private val skipHeaders: Boolean = true
) {

    fun extract(statesColumn: Column, sheetNames: List<String> = emptyList()): List<WordingState> {
        val wordingStates = mutableListOf<WordingState>()

        val workbook = WorkbookFactory.create(File(path), null, true)

        val sheetIterator = if (sheetNames.isEmpty()) {
            workbook.sheetIterator()
        } else {
            sheetNames.map { workbook.getSheet(it) }.iterator()
        }

        while (sheetIterator.hasNext()) {
            val sheet = sheetIterator.next()

            val rowIterator = sheet.rowIterator()
            if (skipHeaders) {
                rowIterator.next()
            }
            while (rowIterator.hasNext()) {
                val row = rowIterator.next()

                val key = row.getCell(keysColumn.index)?.stringCellValue ?: continue
                val state = row.getCell(statesColumn.index)?.stringCellValue ?: ""
                wordingStates.add(WordingState(key, state))
            }
        }

        workbook.close()

        return wordingStates
    }

}