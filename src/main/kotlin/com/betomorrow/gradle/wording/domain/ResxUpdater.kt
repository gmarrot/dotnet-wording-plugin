package com.betomorrow.gradle.wording.domain

import org.w3c.dom.Document
import org.w3c.dom.Element

class ResxUpdater(private val path: String) {

    fun update(wording: Wording, addMissingWordings: Boolean): Set<String> {
        val outputKeys = HashSet<String>()

        val document = loadOrCreateResxDocument(path)

        updateData(document, wording, outputKeys)

        if (addMissingWordings) {
            val missingWordings = wording.keys - outputKeys
            val root = document.firstOrCreateTagName(ROOT_TAG_NAME)
            addMissingWordings(root, missingWordings, wording)

            outputKeys.addAll(missingWordings)
        }

        writeToFile(document, path)

        return outputKeys
    }

    private fun updateData(document: Document, wording: Wording, outputKeys: HashSet<String>) {
        document.getElementsIteratorByTagName(DATA_TAG_NAME).forEach { node ->
            val key = node.getAttribute(NAME_ATTRIBUTE)
            if (wording.containsKey(key)) {
                val element = node as Element

                // TODO Check behavior with null value ?
                val valueChild = element.firstOrCreateTagName(VALUE_TAG_NAME)
                valueChild.textContent = wording.getValue(key)

                wording.getComment(key)?.let { comment ->
                    val commentChild = element.firstOrCreateTagName(COMMENT_TAG_NAME)
                    commentChild.textContent = comment
                }

                outputKeys.add(key)
            }
        }
    }

    private fun addMissingWordings(parent: Element, keys: Iterable<String>, wording: Wording) {
        keys.forEach { key ->
            parent.appendNewChild(DATA_TAG_NAME) {
                setAttribute(NAME_ATTRIBUTE, key)
                setAttributeNS(XML_NAMESPACE_URI, SPACE_ATTRIBUTE, SPACE_DEFAULT_VALUE)

                wording.getValue(key)?.let { value ->
                    val valueChild = this.appendNewChild(VALUE_TAG_NAME)
                    valueChild.appendTextNode(value)
                }

                wording.getComment(key)?.let { comment ->
                    val commentChild = this.appendNewChild(COMMENT_TAG_NAME)
                    commentChild.appendTextNode(comment)
                }
            }
        }
    }

    companion object {
        const val ROOT_TAG_NAME = "root"
        const val DATA_TAG_NAME = "data"
        const val VALUE_TAG_NAME = "value"
        const val COMMENT_TAG_NAME = "comment"

        const val NAME_ATTRIBUTE = "name"

        const val SPACE_ATTRIBUTE = "xml:space"
        const val SPACE_DEFAULT_VALUE = "preserve"

        const val XML_NAMESPACE_URI = "http://www.w3.org/XML/1998/namespace"
    }

}