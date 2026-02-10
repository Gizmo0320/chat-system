plugins {
    base
    id("com.gradleup.shadow") version "8.3.5" apply false
}

allprojects {
    group = "com.example"
    version = "0.1.0"

    repositories {
        mavenCentral()
    }
}

subprojects {
    plugins.withId("java") {
        extensions.configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
            withSourcesJar()
            withJavadocJar()
        }

        tasks.withType<JavaCompile>().configureEach {
            options.encoding = "UTF-8"
            options.release.set(17)
        }

        tasks.withType<Javadoc>().configureEach {
            options.encoding = "UTF-8"
            (options as org.gradle.external.javadoc.StandardJavadocDocletOptions).charSet = "UTF-8"
        }

        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
            testLogging {
                events("passed", "skipped", "failed")
                exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            }
        }

        tasks.withType<AbstractArchiveTask>().configureEach {
            isPreserveFileTimestamps = false
            isReproducibleFileOrder = true
        }
    }

    if (path.startsWith(":platforms:")) {
        plugins.withId("com.gradleup.shadow") {
            val jarTask = tasks.named<Jar>("jar") {
                archiveClassifier.set("thin")
            }

            tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
                archiveBaseName.set(jarTask.flatMap { it.archiveBaseName })
                archiveClassifier.set("")
                configurations = listOf(project.configurations.getByName("runtimeClasspath"))
                mergeServiceFiles()
            }

            tasks.named("assemble") {
                dependsOn(tasks.named("shadowJar"))
            }
        }
    }
}
