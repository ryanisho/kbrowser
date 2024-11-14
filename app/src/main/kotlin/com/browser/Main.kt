package com.browser

import javax.swing.SwingUtilities
import javax.swing.UIManager

fun main() {
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    } catch (e: Exception) {
        e.printStackTrace()
    }

    SwingUtilities.invokeLater { KBrowser().apply { isVisible = true } }
}
