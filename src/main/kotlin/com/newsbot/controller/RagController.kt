package com.newsbot.controller

import com.newsbot.model.QueryRequest
import com.newsbot.model.QueryResponse
import com.newsbot.service.RagService
import kotlinx.coroutines.runBlocking
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rag")
class RagController(private val rag: RagService) {

    @PostMapping("/query")
    fun query(@RequestBody req: QueryRequest): QueryResponse = runBlocking {
        rag.answer(req)
    }
}
