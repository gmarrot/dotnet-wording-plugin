package com.betomorrow.gradle.wording.domain

import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File

class XlsxExtractor(private val path: String, private val keysColumn: Column, private val skipHeaders: Boolean = true) {

    fun extract(language: Language, sheetNames: List<String> = emptyList()): Wording {
        val wording = MutableWording(language)

        val workbook = WorkbookFactory.create(File(path))

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
                val key = row.getCell(keysColumn.index)?.stringCellValue
                val value = row.getCell(language.valuesColumnIndex)?.stringCellValue
                val comment = if (language.commentsColumnIndex != null) {
                    row.getCell(language.commentsColumnIndex)?.stringCellValue
                } else {
                    null
                }
                if (key != null && value != null) {
                    wording.addOrUpdate(key, value, comment)
                }
            }
        }

        workbook.close()

        return wording
    }

}