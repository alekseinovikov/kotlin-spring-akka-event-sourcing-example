package me.alekseinovikov.akaes.props

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "akka.event-sourcing", ignoreUnknownFields = true)
data class EventSourcingProperties(
    var restartBackOffMinSeconds: Long = 10,
    var restartBackOffMaxSeconds:Long = 30,
    var restartBackOffRandomFactor: Double = 0.2,
    var numberOfEvents: Int = 10,
    var keepSnapshots: Int = 2,
    var askTimeoutSeconds: Long = 120
)