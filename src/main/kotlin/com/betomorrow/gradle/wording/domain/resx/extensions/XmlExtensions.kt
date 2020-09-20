package com.betomorrow.gradle.wording.domain.resx.extensions

import com.betomorrow.gradle.wording.domain.resx.ResxUpdater
import com.betomorrow.gradle.wording.kotlin.extensions.appendNewChild
import com.betomorrow.gradle.wording.kotlin.extensions.appendTextNode
import com.betomorrow.gradle.wording.kotlin.extensions.firstOrCreateTagName
import org.w3c.dom.Document
import javax.xml.parsers.DocumentBuilder

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
