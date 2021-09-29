package me.alekseinovikov.akaes.config

import akka.actor.typed.ActorSystem
import akka.actor.typed.javadsl.Behaviors
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
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

}