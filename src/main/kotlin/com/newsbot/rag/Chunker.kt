package com.newsbot.rag

object Chunker {
    fun split(text: String, maxLen: Int = 800, overlap: Int = 100): List<String> {
        if (text.isBlank()) return emptyList()
        val chunks = mutableListOf<String>()
        var i = 0
        val len = text.length
        while (i < len) {
            val end = (i + maxLen).coerceAtMost(len)
            chunks.add(text.substring(i, end))
            val next = end - overlap
            i = if (next > i) next else end
        }
        return chunks
    }
}
