package com.newsbot.model

data class IngestRequest(val feeds: List<String> = emptyList())
data class QueryRequest(val query: String, val topK: Int = 6)
data class QueryResponse(
    val answer: String,
    val sources: List<SourceRef>,
    val latencyMs: Long
)
data class SourceRef(val title: String, val url: String?, val publishedAt: String?)
