package com.newsbot.service

import com.newsbot.model.IngestRequest
import com.newsbot.rag.Chunker
import com.newsbot.rag.EmbeddingClient
import com.newsbot.rag.QPoint
import com.newsbot.rag.QdrantClient
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import java.net.URL

@Service
class IngestService(
    private val qdrant: QdrantClient,
    private val embed: EmbeddingClient
) {
    suspend fun pullAndIndex(req: IngestRequest): Int {
        val entries = req.feeds.flatMap { url ->
            val feed = SyndFeedInput().build(XmlReader(URL(url)))
            feed.entries.map { e ->
                val title = e.title ?: ""
                val link = e.link
                val published = e.publishedDate?.toInstant()?.toString()
                val raw = buildString {
                    appendLine(title)
                    appendLine(e.description?.value ?: "")
                }
                val text = Jsoup.parse(raw).text().trim()
                Article(title, link, published, text)
            }
        }

        val chunks = entries.flatMap { a ->
            Chunker.split(a.text, maxLen = 800, overlap = 100).map { c -> Chunked(a, c) }
        }

        // 첫 호출에서 컬렉션 차원 보증
        val dimProbe = embed.embedOne("probe")
        qdrant.ensureCollection(dimProbe.size)

        // 실제 임베딩
        val vectors = embed.embed(chunks.map { it.content })
        val points = vectors.zip(chunks).map { (vec, ch) ->
            QPoint(
                vector = vec,
                payload = mapOf(
                    "title" to ch.article.title,
                    "url" to ch.article.link,
                    "publishedAt" to ch.article.publishedAt,
                    "text" to ch.content
                )
            )
        }
        qdrant.upsert(points)
        return points.size
    }
    private data class Article(val title: String, val link: String?, val publishedAt: String?, val text: String)
    private data class Chunked(val article: Article, val content: String)
}

