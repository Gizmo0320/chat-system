plugins {
    `java-library`
    id("com.gradleup.shadow")
}

description = "Chat adapter for Sponge"

repositories {
    maven("https://repo.spongepowered.org/repository/maven-public/")
}

tasks.jar {
    archiveBaseName.set("chat-system-sponge")
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
    compileOnly("org.spongepowered:spongeapi:10.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.2")
}
