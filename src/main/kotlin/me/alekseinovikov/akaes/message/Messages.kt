package me.alekseinovikov.akaes.message

enum class StudentActionType {
    ADD,
    DELETE
}

data class StudentActionMessage(
    val className: String,
    val studentName: String,
    val actionType: StudentActionType
)