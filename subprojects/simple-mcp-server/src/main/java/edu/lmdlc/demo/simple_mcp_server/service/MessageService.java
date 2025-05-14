package edu.lmdlc.demo.simple_mcp_server.service;

import edu.lmdlc.demo.simple_mcp_server.model.Message;
import edu.lmdlc.demo.simple_mcp_server.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    
    private final MessageRepository messageRepository;
    
    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
    
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }
    
    public Optional<Message> getMessageById(String id) {
        return messageRepository.findById(id);
    }
    
    public List<Message> getMessagesBySender(String sender) {
        return messageRepository.findBySender(sender);
    }
    
    public List<Message> getMessagesAfterTimestamp(LocalDateTime timestamp) {
        return messageRepository.findByTimestampAfter(timestamp);
    }
    
    public List<Message> searchMessages(String contentSubstring) {
        return messageRepository.findByContentContaining(contentSubstring);
    }
    
    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }
    
    public void deleteMessage(String id) {
        messageRepository.deleteById(id);
    }
}
