package me.alekseinovikov.akaes.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloWorldController {

    @GetMapping
    suspend fun hello() = "Hello! World!"

}