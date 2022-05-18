import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion = "2.0.1"
val coroutinesVersion = "1.6.1"

plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"
    application
}

application {
    mainClass.set("ApplicationKt")
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        implementation("org.apache.kafka:kafka-clients:2.8.0")
        implementation("io.github.microutils:kotlin-logging:2.1.21")
        implementation("org.slf4j:slf4j-simple:1.7.36")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
        implementation("io.ktor:ktor-client-core:$ktorVersion")
        implementation("io.ktor:ktor-client-apache:$ktorVersion")
        implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
        implementation("io.ktor:ktor-serialization:$ktorVersion")
        implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
        testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
        testImplementation("io.kotlintest:kotlintest-assertions:3.4.2")
        testImplementation("io.mockk:mockk:1.12.4")
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "17"
            kotlinOptions.allWarningsAsErrors = false
        }

        test {
            useJUnitPlatform()
        }
    }
}