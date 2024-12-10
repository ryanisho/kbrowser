package com.browser

import com.browser.html.HtmlParser
import java.awt.*
import java.net.HttpURLConnection
import java.net.URI
import javax.swing.*

class KBrowser : JFrame("KBrowser") {
    private val addressBar = JTextField()
    private val contentArea =
            JEditorPane().apply {
                isEditable = false
                // font = Font("Monospaced", Font.PLAIN, 12)
                contentType = "text/html"
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
        addressBar.addActionListener { loadUrl(addressBar.text) }

        layout = BorderLayout()
        add(toolbar, BorderLayout.NORTH)
        add(JScrollPane(contentArea), BorderLayout.CENTER)
    }

    // loadUrl: loads the content of the webpage in the content area
    private fun loadUrl(urlString: String) {
        try {
            val normalizedUrl =
                    if (!urlString.startsWith("http")) {
                        "http://$urlString"
                    } else urlString

            object : SwingWorker<String, Void>() {
                        override fun doInBackground(): String {
                            return fetchWebpage(normalizedUrl)
                        }

                        override fun done() {
                            try {
                                val htmlContent = get()
                                // val htmlTree = parser.parse(htmlContent)
                                // contentArea.text = renderTree(htmlTree)
                                contentArea.text = htmlContent
                            } catch (e: Exception) {
                                JOptionPane.showMessageDialog(
                                        this@KBrowser,
                                        "Error loading page: ${e.message}",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE
                                )
                            }
                        }
                    }
                    .execute()
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid URL: ${e.message}",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            )
        }
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
