plugins {
    id("java")
    kotlin("jvm") version "2.1.20" apply false
    id("org.springframework.boot") version "4.1.1" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
    group = "edu.lmdlc.demo"
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java") // Apply Java plugin for Java sub-project configurations
    apply(plugin = "kotlin") // Apply Kotlin plugin (if required) for Kotlin sub-project configurations

    dependencies {
        compileOnly("org.projectlombok:lombok:1.18.36")
        annotationProcessor("org.projectlombok:lombok:1.18.36")
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(23))
        }
    }
}

