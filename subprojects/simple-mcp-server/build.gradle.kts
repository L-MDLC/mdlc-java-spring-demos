plugins {
	id("java")
	id("org.springframework.boot")
	id("io.spring.dependency-management")
}

version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
	implementation("org.springframework.ai:spring-ai-mcp-server-webmvc-spring-boot-starter:1.0.0-M6")

	// MongoDB dependencies
	implementation("org.mongodb:mongodb-driver-sync") // version managed by the Spring Boot BOM
	implementation("org.springframework.data:spring-data-mongodb")
	
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
