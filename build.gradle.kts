import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion = "1.3.50"

group = "com.gavin"
version = "0.0.1"

plugins {
    `build-scan`
    `java-library`

    kotlin("jvm") version "1.3.50"
}

dependencies {
    implementation(kotlin("stdlib-jdk8", kotlinVersion))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0")
    implementation("org.apache.httpcomponents:httpclient:4.5.1")
    implementation("org.jsoup:jsoup:1.11.3")
    implementation("org.tmatesoft.svnkit:svnkit:1.10.1")
    implementation("no.tornado:tornadofx:1.7.19")
    implementation("io.github.microutils:kotlin-logging:1.6.24")
    implementation("org.slf4j:slf4j-simple:1.7.26")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.7")

    // Use JUnit test framework
    testImplementation(kotlin("test", kotlinVersion))
    testImplementation(kotlin("test-junit5", kotlinVersion))
}

repositories {
    jcenter()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}