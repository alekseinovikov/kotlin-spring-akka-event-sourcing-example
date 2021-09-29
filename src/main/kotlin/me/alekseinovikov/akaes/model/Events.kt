package me.alekseinovikov.akaes.model

import me.alekseinovikov.akaes.annotations.CborSerializable

interface ClassEvent : CborSerializable {
    fun applyTo(classState: ClassState): ClassState
}

data class AddStudentEvent(val studentName: String) : ClassEvent {
    override fun applyTo(classState: ClassState) = classState.apply {
        classState.students.add(studentName)
    }
}

data class DeleteStudentEvent(val studentName: String) : ClassEvent {
    override fun applyTo(classState: ClassState) = classState.apply {
        classState.students.remove(studentName)
    }
}