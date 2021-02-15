plugins {
    kotlin("jvm") version "1.4.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "Kotlin Discord"
        url = uri("https://maven.kotlindiscord.com/repository/maven-public/")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.kotlindiscord.kord.extensions:kord-extensions:1.4.0-SNAPSHOT")
    implementation("org.slf4j:slf4j-simple:1.7.30")
    val exposedVersion = "0.28.1"
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.xerial:sqlite-jdbc:3.34.0")
    implementation("org.reflections:reflections:0.9.11")
    implementation("io.kesselring.sukejura:Sukejura:1.0.4")
    implementation("io.github.cdimascio:dotenv-kotlin:6.2.2")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "15"
    kotlinOptions.useIR = true
}
