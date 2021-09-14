package me.alekseinovikov.akaes

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KotlinSpringAkkaEventSourcingExampleApplication

fun main(args: Array<String>) {
    runApplication<KotlinSpringAkkaEventSourcingExampleApplication>(*args)
}
