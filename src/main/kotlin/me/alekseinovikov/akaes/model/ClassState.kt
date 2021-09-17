package me.alekseinovikov.akaes.model

data class ClassState(
    val name: String,
    val students: MutableSet<String> = mutableSetOf()
)