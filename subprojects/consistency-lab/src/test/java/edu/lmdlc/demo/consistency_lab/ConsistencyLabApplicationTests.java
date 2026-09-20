package edu.lmdlc.demo.consistency_lab;

import edu.lmdlc.demo.consistency_lab.helper.MongoReplicaSetAwaiter;
import org.assertj.core.api.Assertions;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import java.time.Duration;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ConsistencyLabApplicationTests {

    @Autowired
    private MongoTemplate mongoTemplate;

	@Test
	void replicaSetIsHealthy() {
        // When
        Document replSetStatus = MongoReplicaSetAwaiter.awaitHealthyPrimary(mongoTemplate, Duration.ofSeconds(10));

        // Then
        Assertions.assertThat(replSetStatus.getList("members", Document.class))
                .as("The cluster must be a single-node replica set")
                .hasSize(1);
        Assertions.assertThat(replSetStatus.getString("set"))
                .as("The replica set name must be correctly defined")
                .isEqualTo("docker-rs");
	}

}
