package edu.lmdlc.demo.simple_mcp_server.repository;

import edu.lmdlc.demo.simple_mcp_server.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    
    List<Message> findBySender(String sender);
    
    List<Message> findByTimestampAfter(LocalDateTime timestamp);
    
    List<Message> findByContentContaining(String contentSubstring);
}
