package me.alekseinovikov.akaes.model

import akka.actor.typed.ActorRef
import me.alekseinovikov.akaes.annotations.CborSerializable

interface ClassCommand: CborSerializable

abstract class StudentClassCommand(val studentName: String) : ClassCommand

class AddStudentCommand(studentName: String) : StudentClassCommand(studentName)
class DeleteStudentCommand(studentName: String) : StudentClassCommand(studentName)
class GetAllStudentsCommand(val replyTo: ActorRef<ClassState>): ClassCommand