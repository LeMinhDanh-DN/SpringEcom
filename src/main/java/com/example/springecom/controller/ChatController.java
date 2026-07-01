package com.example.springecom.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@RequestMapping("/api/ai")

public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/recommend")
    public String askForRemcommendation(@RequestParam String question) {
        return chatClient.prompt()
                .user(question)
                .system("you are helpful e-commerce shopping asstistance")
                .call()
                .content();
    }
}
