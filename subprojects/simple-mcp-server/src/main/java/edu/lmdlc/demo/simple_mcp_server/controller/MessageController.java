package edu.lmdlc.demo.simple_mcp_server.controller;

import edu.lmdlc.demo.simple_mcp_server.model.Message;
import edu.lmdlc.demo.simple_mcp_server.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    
    private final MessageService messageService;
    
    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }
    
    @GetMapping
    public ResponseEntity<List<Message>> getAllMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable String id) {
        return messageService.getMessageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/sender/{sender}")
    public ResponseEntity<List<Message>> getMessagesBySender(@PathVariable String sender) {
        return ResponseEntity.ok(messageService.getMessagesBySender(sender));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Message>> searchMessages(@RequestParam String query) {
        return ResponseEntity.ok(messageService.searchMessages(query));
    }
    
    @PostMapping
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        return new ResponseEntity<>(messageService.saveMessage(message), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Message> updateMessage(@PathVariable String id, @RequestBody Message message) {
        return messageService.getMessageById(id)
                .map(existingMessage -> {
                    message.setId(id);
                    return ResponseEntity.ok(messageService.saveMessage(message));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable String id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}
