package me.alekseinovikov.akaes.service

import akka.cluster.sharding.typed.javadsl.ClusterSharding
import kotlinx.coroutines.reactor.awaitSingle
import me.alekseinovikov.akaes.actor.ClassActor
import me.alekseinovikov.akaes.message.StudentActionMessage
import me.alekseinovikov.akaes.message.StudentActionType
import me.alekseinovikov.akaes.model.AddStudentCommand
import me.alekseinovikov.akaes.model.ClassState
import me.alekseinovikov.akaes.model.DeleteStudentCommand
import me.alekseinovikov.akaes.model.GetAllStudentsCommand
import me.alekseinovikov.akaes.props.EventSourcingProperties
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toMono
import java.time.Duration
import java.util.concurrent.CompletionStage

@Component
class ClassServiceImpl(
    props: EventSourcingProperties,
    private val sharding: ClusterSharding
) : ClassService {

    private val classes = mutableSetOf<String>()
    private val askDuration = Duration.ofSeconds(props.askTimeoutSeconds)

    override fun applyStudentAction(action: StudentActionMessage) = when (action.actionType) {
        StudentActionType.ADD -> action.className.entityRef().tell(AddStudentCommand(action.studentName))
        StudentActionType.DELETE -> action.className.entityRef().tell(DeleteStudentCommand(action.studentName))
    }.also { classes.add(action.className) }

    override suspend fun getCurrentStates(): List<ClassState> = classes
        .map<String, CompletionStage<ClassState>> { className ->
            className.entityRef().ask({ replyTo -> GetAllStudentsCommand(replyTo) }, askDuration)
        }
        .map { it.toCompletableFuture() }
        .map { it.toMono().awaitSingle() }

    private fun String.entityRef() = sharding.entityRefFor(ClassActor.ENTITY_TYPE_KEY, this)

}