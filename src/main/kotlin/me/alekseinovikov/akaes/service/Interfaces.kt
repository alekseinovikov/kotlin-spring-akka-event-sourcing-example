package me.alekseinovikov.akaes.service

import me.alekseinovikov.akaes.message.StudentActionMessage
import me.alekseinovikov.akaes.model.ClassState

interface ClassService {
    fun applyStudentAction(action: StudentActionMessage)
    suspend fun getCurrentStates(): List<ClassState>
}