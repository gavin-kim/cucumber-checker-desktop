import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion = "1.3.40"

group = "com.gavin"
version = "0.0.1"

plugins {
    `build-scan`
    `java-library`

    kotlin("jvm") version "1.3.31"
}

dependencies {
    implementation(kotlin("stdlib-jdk8", kotlinVersion))

    implementation("org.apache.httpcomponents:httpclient:4.5.1")
    implementation("org.jsoup:jsoup:1.11.3")
    implementation("org.tmatesoft.svnkit:svnkit:1.9.3")
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

val prop1: String by project
val prop2: String by project
val prop3: String by project

tasks.register("printProps") {
    doLast {
        println(prop1)
        println(prop2)
        println(prop3)
        println(System.getProperty("system"))
    }
}