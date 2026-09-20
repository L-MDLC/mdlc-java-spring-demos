plugins {
    // Lets Gradle download a matching JDK when the toolchain is not installed locally
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "lmdlc-java-spring-demos"

file("subprojects").listFiles()?.forEach { dir ->
    if (dir.isDirectory) {
        include(dir.name)
        project(":${dir.name}").projectDir = dir
    }
}

