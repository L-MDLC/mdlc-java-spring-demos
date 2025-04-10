plugins {
    id("java")
}

version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

dependencies {
    // Kafka Connect
    implementation("org.apache.kafka:connect-json:4.0.0")
    implementation("org.apache.kafka:connect-api:4.0.0")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.17")

    // Tests
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.16.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to "edu.lmdlc.demo.kafka.connect.transforms.SMTDataTransformer"
            )
        )
    }

    // Include runtime dependencies
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
