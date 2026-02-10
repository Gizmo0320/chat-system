plugins {
    `java-library`
}

description = "Shared chat system core"

tasks.jar {
    archiveBaseName.set("chat-system-core")
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version
        )
    }
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.1.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.2")
}
