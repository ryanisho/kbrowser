package com.browser

import com.browser.html.HtmlParser
import java.awt.*
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
}
