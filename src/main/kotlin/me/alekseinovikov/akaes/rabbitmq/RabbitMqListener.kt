package me.alekseinovikov.akaes.rabbitmq

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import me.alekseinovikov.akaes.message.StudentActionMessage
import me.alekseinovikov.akaes.service.ClassService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class RabbitMqListener(
    private val objectMapper: ObjectMapper,
    private val classService: ClassService
) {

    @RabbitListener(queues = ["test_queue"])
    fun listen(messagePayload: String) {
        messagePayload.parseToStudentActionMessage().also { classService.applyStudentAction(it) }
    }

    private fun String.parseToStudentActionMessage(): StudentActionMessage = objectMapper.readValue(this)

}