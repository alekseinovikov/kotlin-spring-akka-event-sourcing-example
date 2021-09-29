package me.alekseinovikov.akaes.model

import me.alekseinovikov.akaes.annotations.CborSerializable

data class ClassState(
    val name: String,
    val students: MutableSet<String> = mutableSetOf()
): CborSerializable