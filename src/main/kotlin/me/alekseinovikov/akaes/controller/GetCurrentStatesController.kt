package me.alekseinovikov.akaes.controller

import me.alekseinovikov.akaes.service.ClassService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class GetCurrentStatesController(private val classService: ClassService) {

    @GetMapping
    suspend fun hello() = classService.getCurrentStates()

}