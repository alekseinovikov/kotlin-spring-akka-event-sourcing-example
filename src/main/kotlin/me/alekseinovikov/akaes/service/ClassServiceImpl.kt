package me.alekseinovikov.akaes.service

import akka.actor.typed.ActorSystem
import akka.cluster.sharding.typed.javadsl.ClusterSharding
import akka.cluster.sharding.typed.javadsl.Entity
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.javadsl.AkkaManagement
import akka.persistence.jdbc.testkit.javadsl.SchemaUtils
import akka.persistence.typed.PersistenceId
import me.alekseinovikov.akaes.actor.ClassActor
import me.alekseinovikov.akaes.message.StudentActionMessage
import me.alekseinovikov.akaes.message.StudentActionType
import me.alekseinovikov.akaes.model.AddStudentCommand
import me.alekseinovikov.akaes.model.ClassState
import me.alekseinovikov.akaes.model.DeleteStudentCommand
import me.alekseinovikov.akaes.model.GetAllStudentsCommand
import me.alekseinovikov.akaes.props.EventSourcingProperties
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.CompletionStage
import javax.annotation.PostConstruct

@Component
class ClassServiceImpl(
    private val actorSystem: ActorSystem<Any>,
    private val props: EventSourcingProperties,
    private val env: Environment
) : ClassService {

    private val sharding = ClusterSharding.get(actorSystem)
    private val classes = mutableSetOf<String>()
    private val askDuration = Duration.ofSeconds(props.askTimeoutSeconds)

    @PostConstruct
    fun init() {
        if (env.activeProfiles.contains("local").not()) {
            AkkaManagement.get(actorSystem).start() //Enable management of cluster
            ClusterBootstrap.get(actorSystem).start() //And nodes auto discovery via kube service
        }

        SchemaUtils.createIfNotExists(actorSystem)
            .toCompletableFuture()
            .get() //Wait for DB schema creation

        sharding.init(Entity.of(ClassActor.ENTITY_TYPE_KEY) { context ->
            ClassActor.createBehaviour(
                context.entityId,
                PersistenceId.of(context.entityTypeKey.name(), context.entityId),
                props
            )
        })
    }


    override fun applyStudentAction(action: StudentActionMessage) = when (action.actionType) {
        StudentActionType.ADD -> action.className.entityRef().tell(AddStudentCommand(action.studentName))
        StudentActionType.DELETE -> action.className.entityRef().tell(DeleteStudentCommand(action.studentName))
    }.also { classes.add(action.className) }

    override fun getCurrentStates(): List<ClassState> = classes
        .map<String, CompletionStage<ClassState>> { className -> className.entityRef().ask({ replyTo -> GetAllStudentsCommand(replyTo) }, askDuration) }
        .map { it.toCompletableFuture() }
        .map { it.get() }

    private fun String.entityRef() = sharding.entityRefFor(ClassActor.ENTITY_TYPE_KEY, this)

}