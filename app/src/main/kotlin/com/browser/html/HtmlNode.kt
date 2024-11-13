package com.browser.html

// html node
sealed class HtmlNode {
    // element node
    data class Element(
            val tagName: String,
            val attributes: Map<String, String> = mapOf(),
            val children: MutableList<HtmlNode> = mutableListOf()
    ) : HtmlNode()

    // text node
    data class Text(val content: String) : HtmlNode()
}
