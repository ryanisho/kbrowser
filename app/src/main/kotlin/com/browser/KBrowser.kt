package com.browser

import com.browser.html.HtmlParser
import java.awt.*
import java.net.HttpURLConnection
import java.net.URI
import javax.swing.*
import javax.swing.event.HyperlinkEvent

class KBrowser : JFrame("KBrowser") {
    private val addressBar = JTextField()
    private val contentArea =
            JEditorPane().apply {
                isEditable = false
                contentType = "text/html"

                // hyperlink handling
                addHyperlinkListener { event ->
                    if (event.eventType == HyperlinkEvent.EventType.ACTIVATED) {
                        var url = event.url.toString()
                        addressBar.text = url
                        loadUrl(event.url.toString())
                    }
                }
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
}
