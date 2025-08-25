package com.newsbot.rag

import com.newsbot.config.AppProps
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class EmbeddingClient(props: AppProps) {
    private val client = WebClient.builder()
        .baseUrl("https://api.openai.com/v1")
        .defaultHeader("Authorization", "Bearer ${props.openai.apiKey}")
        .build()
    private val model = props.openai.embeddingModel

    suspend fun embed(texts: List<String>): List<FloatArray> {
        val req = mapOf("model" to model, "input" to texts)
        val resp = client.post().uri("/embeddings")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(req)
            .retrieve()
            .bodyToMono(Map::class.java)
            .awaitSingle()

        @Suppress("UNCHECKED_CAST")
        val data = resp["data"] as List<Map<String, Any>>
        return data.map {
            val arr = it["embedding"] as List<Number>
            FloatArray(arr.size) { idx -> arr[idx].toFloat() }
        }
    }

    suspend fun embedOne(text: String): FloatArray = embed(listOf(text)).first()
}
