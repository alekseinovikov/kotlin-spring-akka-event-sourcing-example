package me.alekseinovikov.akaes.config

import akka.actor.typed.ActorSystem
import akka.actor.typed.javadsl.Behaviors
import akka.cluster.sharding.typed.javadsl.ClusterSharding
import akka.cluster.sharding.typed.javadsl.Entity
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.javadsl.AkkaManagement
import akka.persistence.jdbc.testkit.javadsl.SchemaUtils
import akka.persistence.typed.PersistenceId
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import me.alekseinovikov.akaes.actor.ClassActor
import me.alekseinovikov.akaes.props.EventSourcingProperties
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
class AkkaConfig {

    @Bean
    fun akkaConfiguration(env: Environment): Config {
        val filePostfix = if (env.activeProfiles.contains("local")) "local" else "kube"
        return ConfigFactory
            .parseResources("akka_$filePostfix.cfg")
            .resolve();
    }

    @Bean
    fun actorSystem(
        config: Config,
        @Value("\${akka.cluster.system.name}") systemName: String
    ): ActorSystem<Any> = ActorSystem.create(Behaviors.empty(), systemName, config)

    @Bean
    fun clusterSharding(
        actorSystem: ActorSystem<Any>,
        env: Environment,
        props: EventSourcingProperties
    ): ClusterSharding {
        val sharding = ClusterSharding.get(actorSystem)
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

        return sharding
    }

}