package com.newsbot.rag

object Prompt {
    fun build(query: String, contexts: List<ContextDoc>): String = buildString {
        appendLine("너는 업계 뉴스를 사실적으로 요약한다. 제공된 문서에서만 근거를 사용하고, 확실하지 않으면 모른다고 말한다.")
        appendLine("질문: $query")
        appendLine("컨텍스트:")
        contexts.forEachIndexed { i, c ->
            appendLine("${i+1}) [${c.source}] ${c.text}")
        }
        appendLine("지침: 3문장 요약 + bullet 2~3개, 끝에 Sources(매체/날짜/링크).")
    }
}
data class ContextDoc(val source: String, val text: String, val title: String, val url: String?, val publishedAt: String?)
