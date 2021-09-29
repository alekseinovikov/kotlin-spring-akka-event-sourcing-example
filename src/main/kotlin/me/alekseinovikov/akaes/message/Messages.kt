package me.alekseinovikov.akaes.message

import me.alekseinovikov.akaes.annotations.CborSerializable

enum class StudentActionType {
    ADD,
    DELETE
}

data class StudentActionMessage(
    val className: String,
    val studentName: String,
    val actionType: StudentActionType
) : CborSerializable