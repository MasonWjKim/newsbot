package com.newsbot.rag

import com.newsbot.config.AppProps
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Component
class QdrantClient(props: AppProps) {
    private val base = props.qdrant.host
    private val collection = props.qdrant.collection
    private val client = WebClient.create(base)

    fun ensureCollection(dim: Int) {
        val body = mapOf(
            "vectors" to mapOf("size" to dim, "distance" to "Cosine")
        )
        client.put().uri("/collections/$collection")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve().bodyToMono(Map::class.java).block()
    }

    fun upsert(points: List<QPoint>) {
        val body = mapOf("points" to points.map {
            mapOf(
                "id" to it.id,
                "vector" to it.vector.toList(),
                "payload" to it.payload
            )
        })
        client.put().uri("/collections/$collection/points")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve().bodyToMono(Map::class.java).block()
    }

    fun search(query: FloatArray, topK: Int): List<Map<String, Any?>> {
        val body = mapOf("vector" to query.toList(), "limit" to topK)
        val resp = client.post().uri("/collections/$collection/points/search")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve().bodyToMono(Map::class.java).block() ?: return emptyList()

        @Suppress("UNCHECKED_CAST")
        return resp["result"] as? List<Map<String, Any?>> ?: emptyList()
    }
}
data class QPoint(
    val id: String = UUID.randomUUID().toString(),
    val vector: FloatArray,
    val payload: Map<String, Any?>
)
