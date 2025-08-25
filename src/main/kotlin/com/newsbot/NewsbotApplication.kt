package com.newsbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NewsbotApplication

fun main(args: Array<String>) {
    runApplication<NewsbotApplication>(*args)
}
