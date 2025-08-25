package com.newsbot.controller

import com.newsbot.model.IngestRequest
import com.newsbot.service.IngestService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/ingest")
class IngestController(private val ingest: IngestService) {

    @PostMapping("/pull")
    suspend fun pull(@RequestBody req: IngestRequest): ResponseEntity<Map<String, Any>> {
        val count = ingest.pullAndIndex(req)
        return ResponseEntity.ok(mapOf("indexedChunks" to count))
    }
}
