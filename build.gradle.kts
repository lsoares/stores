import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.4.30"
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "com.luissoares"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.javalin:javalin:3.+")
    implementation("org.slf4j:slf4j-simple:1.+")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.+")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.+")

    testImplementation("org.skyscreamer:jsonassert:1.+")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.+")
    testImplementation("io.mockk:mockk:1.+")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks {
    named<ShadowJar>("shadowJar") {
        manifest {
            attributes(mapOf("Main-Class" to "restapi.AppKt"))
        }
    }
}
