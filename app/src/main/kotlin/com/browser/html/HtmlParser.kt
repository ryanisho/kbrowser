package com.browser.html

class HtmlParser {
    private var pos = 0
    private var input = ""

    fun parse(html: String): HtmlNode.Element {
        input = html
        pos = 0
        return parseNodes(null)
    }

    fun parseNodes(parentTag: String?): HtmlNode.Element {
        val nodes = mutableListOf<HtmlNode>()
        val root = HtmlNode.Element("html", mutableMapOf(), nodes)

        while (pos < input.length) {
            when {
                // handle comments
                input.startsWith("<!--", pos) -> {
                    pos = input.indexOf("-->", pos + 4).takeIf { it != -1 }?.plus(3) ?: input.length
                }

                // handle end tag
                input.startsWith("</", pos) -> {
                    val endTag = parseEndTag()
                    if (endTag == parentTag) {
                        return root
                    }
                }

                // handle start tag
                input.startsWith("<", pos) -> {
                    val element = parseElement()
                    if (element != null) {
                        nodes.add(element)
                    }
                }
                else -> {
                    val text = parseText()
                    if (text.isNotBlank()) {
                        nodes.add(HtmlNode.Text(text))
                    }
                }
            }
        }
        return root
    }

    private fun parseElement(): HtmlNode.Element? {
        if (input[pos] != '<') return null
        pos++ // skip <

        // get tag name
        val tagNameEnd = input.indexOf(arrayOf('>', ' ', '/'), pos)
        if (tagNameEnd == -1) {
            return null
        }
        val tagName = input.substring(pos, tagNameEnd).lowercase()
        pos = tagNameEnd

        // parse attributes
        val attributes = mutableMapOf<String, String>()
        while (pos < input.length && input[pos] != '>' && input[pos] != '/') {
            consumeWhitespace()

            if (pos < input.length && input[pos] != '>' && input[pos] != '/') {
                parseAttribute(attributes)
            }
        }

        // self closing tags
        if (pos < input.length && input[pos] == '/') {
            pos += 2 // skip />
            return HtmlNode.Element(tagName, attributes)
        }

        // skip >
        if (pos < input.length && input[pos] == '>') pos++

        // parse children for non-void elements
        return if (!VOID_ELEMENTS.contains(tagName)) {
            val children = parseNodes(tagName).children
            HtmlNode.Element(tagName, attributes, children)
        } else {
            HtmlNode.Element(tagName, attributes)
        }
    }

    private fun parseAttribute(attributes: MutableMap<String, String>) {
        val nameEnd = input.indexOf(arrayOf('=', '>', ' ', '/'), pos)
        if (nameEnd == -1) return
        val name = input.substring(pos, nameEnd).trim()
        pos = nameEnd

        consumeWhitespace()

        if (pos < input.length && input[pos] == '=') {
            pos++
            consumeWhitespace()

            if (pos < input.length && input[pos] == '"') {
                pos++
                val valueEnd = input.indexOf('"', pos)
                if (valueEnd != -1) {
                    val value = input.substring(pos, valueEnd)
                    attributes[name] = value
                    pos = valueEnd + 1
                }
            }
        }
    }

    private fun parseText(): String {
        val textEnd = input.indexOf('<', pos)
        val text =
                if (textEnd == -1) {
                    input.substring(pos)
                } else {
                    input.substring(pos, textEnd)
                }
        pos = if (textEnd == -1) input.length else textEnd
        return text.trim()
    }

    private fun parseEndTag(): String {
        val start = pos + 2 // skip </
        val end = input.indexOf('>', start)
        if (end == -1) return ""
        pos = end + 1
        return input.substring(start, end).trim().lowercase()
    }

    private fun consumeWhitespace() {
        while (pos < input.length && input[pos].isWhitespace()) {
            pos++
        }
    }

    private fun String.indexOf(chars: Array<Char>, startIndex: Int): Int {
        for (i in startIndex until length) {
            if (chars.contains(this[i])) {
                return i
            }
        }
        return -1
    }
}
