package me.alekseinovikov.akaes.controller

import me.alekseinovikov.akaes.service.ClassService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class GetCurrentStatesController(private val classService: ClassService) {

    private val uuid = UUID.randomUUID().toString()


    @GetMapping
    suspend fun currentStatuses() = classService.getCurrentStates()

    @GetMapping("/hello")
    suspend fun hello() = uuid

}