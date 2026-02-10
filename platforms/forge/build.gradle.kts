plugins {
    `java-library`
}

description = "Chat adapter for Forge"

repositories {
    maven("https://maven.minecraftforge.net/")
}

tasks.jar {
    archiveBaseName.set("chat-system-forge")
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
    compileOnly("net.minecraftforge:forge:1.20.1-47.2.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.2")
}
