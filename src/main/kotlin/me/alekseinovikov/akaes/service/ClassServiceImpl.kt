package me.alekseinovikov.akaes.service

import me.alekseinovikov.akaes.message.StudentActionMessage
import me.alekseinovikov.akaes.message.StudentActionType
import me.alekseinovikov.akaes.model.ClassState
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class ClassServiceImpl : ClassService {

    private val classMap: MutableMap<String, ClassState?> = ConcurrentHashMap()


    override fun applyStudentAction(action: StudentActionMessage) {
        classMap.computeIfPresent(action.className) { _, oldState ->
            when (action.actionType) {
                StudentActionType.ADD -> oldState.students.add(action.studentName)
                StudentActionType.DELETE -> oldState.students.remove(action.studentName)
            }

            return@computeIfPresent oldState
        }

        classMap.computeIfAbsent(action.className) {
            when (action.actionType) {
                StudentActionType.ADD -> ClassState(action.className).also { it.students.add(action.studentName) }
                StudentActionType.DELETE -> null
            }
        }
    }

    override fun getCurrentStates(): List<ClassState> = this.classMap.values.filterNotNull().toList()

}