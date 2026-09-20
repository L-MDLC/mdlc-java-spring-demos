package edu.lmdlc.demo.consistency_lab;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

	@Bean
	@ServiceConnection
	MongoDBContainer mongoDbContainer() {
        // MongoDB 8.3.11 - The latest stable version available on September 2026
		return new MongoDBContainer(DockerImageName.parse("mongo:8.3.11")).withReplicaSet();
	}

	@Bean
	@ServiceConnection
	PostgreSQLContainer postgresContainer() {
        // Postgres 18.6 - The latest stable version available on September 2026
        return new PostgreSQLContainer(DockerImageName.parse("postgres:18.6"));
	}

}
