package com.betomorrow.gradle.wording.domain

import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.Text
import java.io.File
import java.io.FileOutputStream
import java.io.StringWriter
import java.nio.file.Files
import java.nio.file.Paths
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

fun loadOrCreateResxDocument(path: String): Document {
    val documentBuilderFactory = DocumentBuilderFactory.newInstance()
    val documentBuilder = documentBuilderFactory.newDocumentBuilder()

    return if (Files.isRegularFile(Paths.get(path))) {
        documentBuilder.parse(File(path))
    } else {
        documentBuilder.newResxDocument()
    }
}

fun writeToFile(document: Document, path: String) {
    val transformerFactory = TransformerFactory.newInstance()
    transformerFactory.setAttribute("indent-number", 4)

    val transformer = transformerFactory.newTransformer()
    transformer.setOutputProperty(OutputKeys.INDENT, "yes")
    transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes")
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
    transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "4")
    transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_LINE_SEPARATOR, "4")

    val xmlDeclaration = document.createProcessingInstruction("xml", "version=\"1.0\" encoding=\"utf-8\"")
    document.insertBefore(xmlDeclaration, document.documentElement)

    removeEmptyText(document)

    val stringWriter = StringWriter()

    val domSource = DOMSource(document)
    val streamResult = StreamResult(stringWriter)
    transformer.transform(domSource, streamResult)
    correctAndWriteFile(stringWriter, path)
    stringWriter.close()
}

private fun correctAndWriteFile(writer: StringWriter, path: String) {
    val outputXmlString = writer.toString().replaceFirst("?>", "?>\n")
    val outputStream = FileOutputStream(File(path))
    outputStream.write(outputXmlString.toByteArray(Charsets.UTF_8))
    outputStream.close()
}

/*
 * Fix: the text nodes in XML file used for indentation are treated as data.
 * Because of this your indentation is going for toss.
 * This method is used to fix this (cf. https://stackoverflow.com/a/31421664)
 */
private fun removeEmptyText(node: Node) {
    var child = node.firstChild
    while (child != null) {
        val sibling = child.nextSibling
        if (child.nodeType == Node.TEXT_NODE) {
            if (child.textContent.trim().isEmpty()) {
                node.removeChild(child)
            }
        } else {
            removeEmptyText(child)
        }
        child = sibling
    }
}


/**
 * DocumentBuilder Extensions
 */

fun DocumentBuilder.newResxDocument(): Document {
    val document = this.newDocument()
    val root = document.firstOrCreateTagName(ResxUpdater.ROOT_TAG_NAME)

    root.appendNewChild("resheader") {
        setAttribute("name", "resmimetype")
        firstOrCreateTagName("value").appendTextNode("text/microsoft-resx")
    }

    root.appendNewChild("resheader") {
        setAttribute("name", "version")
        firstOrCreateTagName("value").appendTextNode("2.0")
    }

    root.appendNewChild("resheader") {
        setAttribute("name", "reader")
        firstOrCreateTagName("value").appendTextNode("System.Resources.ResXResourceReader, System.Windows.Forms, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
    }

    root.appendNewChild("resheader") {
        setAttribute("name", "writer")
        firstOrCreateTagName("value").appendTextNode("System.Resources.ResXResourceWriter, System.Windows.Forms, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
    }

    return document
}


/**
 * Document Extensions
 */

fun Document.getElementsIteratorByTagName(name: String): Iterable<Node> {
    val elements = this.getElementsByTagName(name)

    return object : Iterable<Node> {
        override fun iterator(): Iterator<Node> {
            return iterator {
                for (i in 0 until elements.length) {
                    yield(elements.item(i))
                }
            }
        }
    }
}

fun Document.firstOrCreateTagName(name: String): Element {
    return this.getElementsByTagName(name).let {
        if (it.length > 0) {
            it.item(0) as Element
        } else {
            val node = this.createElement(name)
            this.appendChild(node)
            node
        }
    }
}


/**
 * Node Extensions
 */

fun Node.getAttribute(name: String): String {
    return this.attributes.getNamedItem(name).nodeValue
}


/**
 * Element Extensions
 */

fun Element.firstOrCreateTagName(name: String): Element {
    return this.getElementsByTagName(name).let {
        if (it.length > 0) {
            it.item(0) as Element
        } else {
            val node = this.ownerDocument.createElement(name)
            this.appendChild(node)
            node
        }
    }
}

fun Element.appendNewChild(name: String, block: Element.() -> Unit = {}): Element {
    val element = this.ownerDocument.createElement(name)
    block(element)
    appendChild(element)

    return element
}

fun Element.appendTextNode(name: String): Text {
    val elt = this.ownerDocument.createTextNode(name)
    appendChild(elt)

    return elt
}