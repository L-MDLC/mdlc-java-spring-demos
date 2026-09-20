package edu.lmdlc.demo.consistency_lab.helper;

import com.mongodb.MongoCommandException;
import org.awaitility.Awaitility;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.Duration;
import java.util.List;

public final class MongoReplicaSetAwaiter {

    private MongoReplicaSetAwaiter() {}

    public static Document awaitHealthyPrimary(MongoTemplate template, Duration timeout) {
        return Awaitility.await("replica set primary election")
                .atMost(timeout)
                .pollInterval(Duration.ofMillis(500))
                .ignoreExceptionsInstanceOf(MongoCommandException.class)
                .until(() -> template.getMongoDatabaseFactory()
                                .getMongoDatabase("admin")
                                .runCommand(new Document("replSetGetStatus", 1)),
                        MongoReplicaSetAwaiter::hasHealthyPrimary);
    }

    private static boolean hasHealthyPrimary(Document status) {
        List<Document> members = status.getList("members", Document.class);
        return members != null && members.stream().anyMatch(m ->
                "PRIMARY".equals(m.getString("stateStr"))
                        && ((Number) m.get("health")).intValue() == 1);
    }
}
