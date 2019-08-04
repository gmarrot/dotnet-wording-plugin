package com.betomorrow.gradle.wording.domain.resx

import com.betomorrow.gradle.wording.domain.resx.extensions.newResxDocument
import com.betomorrow.gradle.wording.domain.wording.Wording
import com.betomorrow.gradle.wording.kotlin.extensions.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.xml.parsers.DocumentBuilderFactory

class ResxUpdater(private val path: String) {

    fun update(
        wording: Wording,
        addMissingWordings: Boolean = false,
        removeNonExistingWording: Boolean = false,
        sortWording: Boolean = false
    ): Set<String> {
        val document = loadOrCreateResxDocument(path)

        val outputKeys = updateData(document, wording, removeNonExistingWording)

        if (addMissingWordings) {
            val missingWordings = wording.keys - outputKeys
            val root = document.firstOrCreateTagName(ROOT_TAG_NAME)
            addMissingWordings(root, missingWordings, wording)

            outputKeys.addAll(missingWordings)
        }

        if (sortWording) {
            sortData(document, wording)
        }

        writeToFile(document, path)

        return outputKeys
    }

    private fun loadOrCreateResxDocument(path: String): Document {
        val documentBuilderFactory = DocumentBuilderFactory.newInstance()
        val documentBuilder = documentBuilderFactory.newDocumentBuilder()

        return if (Files.isRegularFile(Paths.get(path))) {
            documentBuilder.parse(File(path))
        } else {
            documentBuilder.newResxDocument()
        }
    }

    private fun updateData(
        document: Document,
        wording: Wording,
        removeNonExistingWording: Boolean
    ): MutableSet<String> {
        val updatedKeys = hashSetOf<String>()
        val nonExistingWordings = hashSetOf<Node>()

        document.getElementsIteratorByTagName(DATA_TAG_NAME).forEach { node ->
            val key = node.getAttribute(NAME_ATTRIBUTE)
            val value = wording.getValue(key)
            if (value != null) {
                updateWording(node as Element, value, wording.getComment(key))
                updatedKeys.add(key)
            } else {
                nonExistingWordings.add(node)
            }
        }

        if (removeNonExistingWording) {
            nonExistingWordings.forEach { removeWording(it) }
        }

        return updatedKeys
    }

    private fun updateWording(element: Element, value: String, comment: String?) {
        val valueChild = element.firstOrCreateTagName(VALUE_TAG_NAME)
        valueChild.textContent = value

        if (comment != null) {
            val commentChild = element.firstOrCreateTagName(COMMENT_TAG_NAME)
            commentChild.textContent = comment
        } else {
            element.removeChildren(COMMENT_TAG_NAME)
        }
    }

    private fun removeWording(node: Node) {
        node.removeFromParent()
    }

    private fun addMissingWordings(parent: Element, keys: Iterable<String>, wording: Wording) {
        keys.forEach { key -> addWording(parent, key, wording) }
    }

    private fun addWording(parent: Element, key: String, wording: Wording) {
        parent.appendNewChild(DATA_TAG_NAME) {
            setAttribute(NAME_ATTRIBUTE, key)
            setAttributeNS(
                XML_NAMESPACE_URI,
                SPACE_ATTRIBUTE,
                SPACE_DEFAULT_VALUE
            )

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

    private fun sortData(document: Document, wording: Wording) {
        var i = 0
        document
            .getElementsIteratorByTagName(DATA_TAG_NAME)
            .map { node ->
                val key = node.getAttribute(NAME_ATTRIBUTE)
                val sortScore = if (wording.containsKey(key)) {
                    wording.keys.indexOf(key)
                } else {
                    wording.keys.size + i++
                }

                Pair(node, sortScore)
            }
            .sortedWith(compareBy { it.second })
            .forEach { (node, _) ->
                val parent = node.parentNode
                node.removeFromParent()
                parent.appendChild(node)
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