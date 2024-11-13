package com.browser

import com.browser.html.HtmlParser
import java.awt.*
import java.net.HttpURLConnection
import java.net.URI
import javax.swing.*

class KBrowser : JFrame("KBrowser") {
    private val addressBar = JTextField()
    private val contentArea =
            JTextArea().apply {
                isEditable = false
                font = Font("Monospaced", Font.PLAIN, 12)
            }
    private val parser = HtmlParser()

    init {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        size = Dimension(800, 600)

        // toolbar + address bar
        val toolbar =
                JPanel(BorderLayout()).apply {
                    add(JLabel("URL: "), BorderLayout.WEST)
                    add(addressBar, BorderLayout.CENTER)
                }

        // url loading on enter
        addressBar.addActionListener {
            // loadUrl(addressBar.text)
            // TODO: Implement loadUrl(url : String)
        }

        layout = BorderLayout()
        add(toolbar, BorderLayout.NORTH)
        add(JScrollPane(contentArea), BorderLayout.CENTER)
    }

    // fetchWebpage: fetches the content of a webpage
    private fun fetchWebpage(urlString: String): String {
        val url = URI(urlString).toURL()
        val connection = url.openConnection() as HttpURLConnection
        return connection.inputStream.bufferedReader().use { it.readText() }
    }

    // renderHtml: renders the HTML content in the content area
    private fun renderTree(node: com.browser.html.HtmlNode, indent: Int = 0): String {
        val sb = StringBuilder()
        val padding = " ".repeat(indent)

        when (node) {
            is com.browser.html.HtmlNode.Element -> {
                sb.appendLine("$padding<${node.tagName}${renderAttributes(node.attributes)}>")
                // render the children
                node.children.forEach { child -> sb.append(renderTree(child, indent + 1)) }
                if (node.children.isNotEmpty()) {
                    sb.appendLine("$padding</${node.tagName}>")
                }
            }
            is com.browser.html.HtmlNode.Text -> {
                if (node.content.isNotBlank()) {
                    sb.appendLine("$padding${node.content}")
                }
            }
        }
        return sb.toString()
    }

    // renderHtml: renders the HTML content in the content area
    private fun renderAttributes(attributes: Map<String, String>): String {
        if (attributes.isEmpty()) return ""
        return attributes.entries.joinToString(" ", prefix = " ") { (key, value) ->
            "$key\"$value\""
        }
    }
}
