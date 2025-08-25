package com.newsbot.service

import com.newsbot.model.QueryRequest
import com.newsbot.model.QueryResponse
import com.newsbot.model.SourceRef
import com.newsbot.rag.*
import org.springframework.stereotype.Service
import kotlin.system.measureTimeMillis

@Service
class RagService(
    private val embed: EmbeddingClient,
    private val qdrant: QdrantClient,
    private val chat: ChatClient
) {
    suspend fun answer(req: QueryRequest): QueryResponse {
        var content = ""
        val elapsed = measureTimeMillis {
            val qv = embed.embedOne(req.query)
            val hits = qdrant.search(qv, req.topK)

            val ctx = hits.mapNotNull { h ->
                val payload = h["payload"] as? Map<*, *> ?: return@mapNotNull null
                val title = payload["title"]?.toString() ?: ""
                val url = payload["url"]?.toString()
                val publishedAt = payload["publishedAt"]?.toString()
                val text = payload["text"]?.toString() ?: ""
                ContextDoc(
                    source = "$title | $publishedAt",
                    text = text, title = title, url = url, publishedAt = publishedAt
                )
            }.take(6)

            val prompt = Prompt.build(req.query, ctx)
            content = chat.chat(prompt)
        }
        val sources = extractSources(content)
        return QueryResponse(answer = content, sources = sources, latencyMs = elapsed)
    }

    private fun extractSources(answer: String): List<SourceRef> {
        val idx = answer.indexOf("Sources", ignoreCase = true)
        if (idx < 0) return emptyList()
        return answer.substring(idx).lines()
            .drop(1)
            .filter { it.isNotBlank() }
            .take(4)
            .map { SourceRef(title = it.trim(), url = null, publishedAt = null) }
    }
}
