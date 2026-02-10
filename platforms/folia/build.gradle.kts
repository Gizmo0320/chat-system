plugins {
    `java-library`
}

description = "Chat adapter for Folia"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

tasks.jar {
    archiveBaseName.set("chat-system-folia")
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
    compileOnly("dev.folia:folia-api:1.20.4-R0.1-SNAPSHOT")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.2")
}
