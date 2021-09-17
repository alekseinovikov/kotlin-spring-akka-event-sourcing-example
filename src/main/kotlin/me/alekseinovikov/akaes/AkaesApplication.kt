package me.alekseinovikov.akaes

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AkaesApplication

fun main(args: Array<String>) {
    runApplication<AkaesApplication>(*args)
}
