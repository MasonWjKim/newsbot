package com.newsbot.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app")
class AppProps {
    val qdrant = QdrantProps()
    val openai = OpenAIProps()

    class QdrantProps { var host: String = ""; var collection: String = "news" }
    class OpenAIProps { var apiKey: String = ""; var embeddingModel: String = ""; var chatModel: String = "" }
}
