package me.alekseinovikov.akaes.config

import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.QueueBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfig {

    @Bean
    fun testQueue(): Queue = QueueBuilder
        .durable("test_queue")
        .build()

}