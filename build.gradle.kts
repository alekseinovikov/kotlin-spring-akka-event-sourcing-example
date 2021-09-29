import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.5.4"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.21"
    kotlin("plugin.spring") version "1.5.21"
    id("com.google.cloud.tools.jib") version "3.1.4"
}

val scalaBinaryVersion = "2.13"
val akkaVersion = "2.6.16"
val slickVersion = "3.3.3"
val akkaManagementVersion = "1.1.1"
val postgresDriverVersion = "42.2.24"


group = "me.alekseinovikov.akaes"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("com.typesafe.akka:akka-bom_${scalaBinaryVersion}:${akkaVersion}"))
    implementation("org.postgresql:postgresql:${postgresDriverVersion}")
    implementation("com.typesafe.akka:akka-cluster-sharding-typed_${scalaBinaryVersion}")
    implementation("com.typesafe.akka:akka-persistence-typed_${scalaBinaryVersion}")
    implementation("com.typesafe.akka:akka-serialization-jackson_${scalaBinaryVersion}")
    implementation("com.lightbend.akka:akka-persistence-jdbc_${scalaBinaryVersion}:5.0.4")
    implementation("com.typesafe.akka:akka-persistence-query_${scalaBinaryVersion}:${akkaVersion}")
    implementation("com.typesafe.slick:slick_${scalaBinaryVersion}:${slickVersion}")
    implementation("com.typesafe.slick:slick-hikaricp_${scalaBinaryVersion}:${slickVersion}")
    implementation("com.lightbend.akka.discovery:akka-discovery-kubernetes-api_${scalaBinaryVersion}:${akkaManagementVersion}")
    implementation("com.lightbend.akka.management:akka-management-cluster-bootstrap_${scalaBinaryVersion}:${akkaManagementVersion}")

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jib {
    from {
        platforms {
            platform {
                architecture = "arm64"
                os = "linux"
            }

            platform {
                architecture = "amd64"
                os = "linux"
            }
        }
    }
    to {
        auth {
            username = System.getenv("DOCKER_HUB_USER_NAME")
            password = System.getenv("DOCKER_HUB_PASSWORD")
        }
        image = "alekseinovikov/akaes"

        val tagNameEnv = System.getenv("TAG_NAME")
        tags = if (tagNameEnv.isNullOrBlank()) setOf("develop") else setOf(tagNameEnv, "latest")
    }
}
