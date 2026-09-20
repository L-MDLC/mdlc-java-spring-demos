plugins {
	id("java")
	id("org.springframework.boot")
	id("io.spring.dependency-management")
}

version = "1.0-SNAPSHOT"

dependencies {
	implementation("org.springframework.ai:spring-ai-starter-mcp-server-webmvc:2.0.1")

	// MongoDB dependencies
	implementation("org.mongodb:mongodb-driver-sync") // version managed by the Spring Boot BOM
	implementation("org.springframework.data:spring-data-mongodb")
	
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
