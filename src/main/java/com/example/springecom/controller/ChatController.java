package com.example.springecom.controller;

import com.example.springecom.config.AiToolConfig; // Nhớ import class này
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@RequestMapping("/api/ai")
public class ChatController {

    private final ChatClient chatClient;
    private final AiToolConfig aiToolConfig;

    public ChatController(ChatClient.Builder builder, AiToolConfig aiToolConfig) {
        this.chatClient = builder.build();
        this.aiToolConfig = aiToolConfig;
    }

    @GetMapping("/recommend")
    public String askForRemcommendation(@RequestParam String question) {
        try {
            return chatClient.prompt()
                    .user(question)
                    .system("You are a helpful e-commerce shopping assistant for E-Shop. "
                            + "Your job is to guide clients and suggest products based on their questions. "
                            + "Use the 'searchProduct' tool to lookup product information in the database. "
                            + "Always return product name, price, brand, and stock details when making suggestions. "
                            + "Be friendly, polite, and helpful.")
                    .tools(aiToolConfig)
                    .call()
                    .content();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error " + e.getMessage() + " | Cause " +
                    (e.getCause() != null ? e.getCause().getMessage() : "Unknown");
        }
    }
}