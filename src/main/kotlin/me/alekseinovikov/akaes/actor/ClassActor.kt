package me.alekseinovikov.akaes.actor

import akka.actor.typed.Behavior
import akka.actor.typed.SupervisorStrategy
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.cluster.sharding.typed.javadsl.EntityTypeKey
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.javadsl.*
import me.alekseinovikov.akaes.model.*
import me.alekseinovikov.akaes.props.EventSourcingProperties
import java.time.Duration

class ClassActor(
    private val className: String,
    private val context: ActorContext<ClassCommand>,
    private val props: EventSourcingProperties,
    persistenceId: PersistenceId?,
) :
    EventSourcedBehavior<ClassCommand, ClassEvent, ClassState>(
        persistenceId, SupervisorStrategy.restartWithBackoff(
            Duration.ofSeconds(props.restartBackOffMinSeconds),
            Duration.ofSeconds(props.restartBackOffMaxSeconds),
            props.restartBackOffRandomFactor
        )
    ) {

    companion object {
        var ENTITY_TYPE_KEY: EntityTypeKey<ClassCommand> = EntityTypeKey.create(ClassCommand::class.java, "Class")
        fun createBehaviour(
            className: String,
            persistenceId: PersistenceId,
            props: EventSourcingProperties
        ): Behavior<ClassCommand> = Behaviors.setup { context -> ClassActor(className, context, props, persistenceId) }
    }

    override fun retentionCriteria(): SnapshotCountRetentionCriteria = RetentionCriteria
        .snapshotEvery(props.numberOfEvents, props.keepSnapshots)
        .withDeleteEventsOnSnapshot()

    override fun emptyState() = ClassState(className)
    override fun eventHandler(): EventHandler<ClassState, ClassEvent> = EventHandler { state, event -> event.applyTo(state) }

    override fun commandHandler(): CommandHandler<ClassCommand, ClassEvent, ClassState> =
        newCommandHandlerBuilder()
            .forAnyState()
            .onCommand(AddStudentCommand::class.java, this::addStudent)
            .onCommand(DeleteStudentCommand::class.java, this::deleteStudent)
            .onCommand(GetAllStudentsCommand::class.java, this::getState)
            .build()

    private fun addStudent(command: AddStudentCommand): Effect<ClassEvent, ClassState> = AddStudentEvent(command.studentName).let {
        Effect()
            .persist(it)
    }

    private fun deleteStudent(command: DeleteStudentCommand): Effect<ClassEvent, ClassState> = DeleteStudentEvent(command.studentName).let {
        Effect()
            .persist(it)
    }

    private fun getState(command: GetAllStudentsCommand): Effect<ClassEvent, ClassState> = Effect()
        .none()
        .thenRun<ClassState> { newState -> command.replyTo.tell(newState) }

}