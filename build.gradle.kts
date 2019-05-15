import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.31"
    id("com.adarshr.test-logger") version "1.6.0"
}

group = "net.jiffle"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib-jdk8"))

    compile( group = "io.vavr", name = "vavr-kotlin", version = "0.10.0")
    compile( group = "com.natpryce", name = "result4k", version = "2.0.0")

    testCompile( "io.kotlintest:kotlintest-runner-junit5:3.3.2")

}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}