package com.example.springecom.controller;

import com.example.springecom.config.AiSearchTool; // Nhớ import class này
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
@RequestMapping("/api/ai")
public class ChatController {

    private final ChatClient chatClient;
    private final AiSearchTool aiSearchTool;
    private final ChatMemory chatMemory = MessageWindowChatMemory.builder().build();

    public ChatController(ChatClient.Builder builder, AiSearchTool aiSearchTool) {
        this.chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor
                        .builder(chatMemory)
                        .build())
                .build();
        this.aiSearchTool = aiSearchTool;
    }

    @GetMapping("/ask")
    public String getAnswer(@RequestParam String question, @RequestParam String sessionId) {
        try {
            return chatClient.prompt()
                    .user(question)
                    .advisors(advisorSpec -> advisorSpec.param("chat_memory_conversation_id", sessionId))
                    .system("You are a helpful e-commerce shopping assistant for E-Shop. "
                            + "Your job is to guide clients and suggest products based on their questions. "
                            + "Use the 'searchProduct' tool to lookup product information in the database. "
                            + "Always return product name, price, brand, and stock details when making suggestions. "
                            + "Be friendly, polite, and helpful.")
                    .tools(aiSearchTool)
                    .call()
                    .content();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error " + e.getMessage() + " | Cause " +
                    (e.getCause() != null ? e.getCause().getMessage() : "Unknown");
        }
    }

    @PostMapping("/recommend")
    public String recommend(@RequestParam String brand, @RequestParam String category, @RequestParam String sessionId) {
        String tmp = """
                    You are a helpful e-commerce shopping assistant for E-Shop.
                    Your job is to guide clients and suggest products based on their questions.
                    Use the 'searchProduct' tool to lookup product information in the database.
                    Always return product name, price, brand, and stock details when making suggestions.
                    Be friendly, polite, and helpful.
                
                    Recommend products based on the following criteria:
                    Brand: {brand}
                    Category: {category}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(tmp);
        Prompt prompt = promptTemplate.create(Map.of("brand", brand, "category", category));

        String response = chatClient
                .prompt(prompt)
                .advisors(advisorSpec -> advisorSpec.param("chat_memory_conversation_id", sessionId))
                .call()
                .content();

        return response;
    }
}