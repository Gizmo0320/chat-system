plugins {
    `java-library`
    id("com.gradleup.shadow")
}

description = "Chat adapter for Neoforge"

repositories {
    maven("https://maven.neoforged.net/releases")
}

tasks.jar {
    archiveBaseName.set("chat-system-neoforge")
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version
        )
    }
}

dependencies {
    implementation(project(":core"))
    compileOnly("org.jetbrains:annotations:24.1.0")

    // Platform API
    compileOnly("net.neoforged:neoforge:20.6.125")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.2")
}
