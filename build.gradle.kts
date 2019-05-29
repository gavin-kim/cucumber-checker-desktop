import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.gavin"
version = "0.0.1"

plugins {
    `build-scan`
    `java-library`

    kotlin("jvm") version "1.3.31"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("org.apache.httpcomponents:httpclient:4.5.1")
    implementation("org.jsoup:jsoup:1.11.3")
    implementation("org.tmatesoft.svnkit:svnkit:1.9.3")
    implementation("no.tornado:tornadofx:1.7.19")

    // Use JUnit test framework
    testImplementation("junit:junit:4.12")
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