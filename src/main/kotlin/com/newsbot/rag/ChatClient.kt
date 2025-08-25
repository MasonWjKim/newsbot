package com.newsbot.rag

import com.newsbot.config.AppProps
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class ChatClient(props: AppProps) {
    private val client = WebClient.builder()
        .baseUrl("https://api.openai.com/v1")
        .defaultHeader("Authorization", "Bearer ${props.openai.apiKey}")
        .build()
    private val model = props.openai.chatModel

    fun chat(systemAndUserText: String): String {
        val body = mapOf(
            "model" to model,
            "messages" to listOf(
                mapOf("role" to "system", "content" to "너는 신뢰할 수 있는 뉴스 요약 도우미다."),
                mapOf("role" to "user", "content" to systemAndUserText)
            )
        )
        val resp = client.post().uri("/chat/completions")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(Map::class.java).block() ?: return ""
        @Suppress("UNCHECKED_CAST")
        val choices = resp["choices"] as List<Map<String, Any>>
        val msg = choices.first()["message"] as Map<String, Any>
        return msg["content"]?.toString() ?: ""
    }
}
