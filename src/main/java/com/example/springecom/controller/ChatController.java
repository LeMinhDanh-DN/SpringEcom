package com.example.springecom.controller;

import com.example.springecom.config.AiSearchTool; // Nhớ import class này
import com.example.springecom.model.Product;
import com.example.springecom.service.ProductService;
import com.example.springecom.service.ProductVectorService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
@RequestMapping("/api/ai")
public class ChatController {

    private final ChatClient chatClient;
    private final AiSearchTool aiSearchTool;
    @Autowired
    private ProductVectorService productVectorService;
    @Autowired
    private ProductService productService;

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
            List<Document> similarProducts = productVectorService.searchSimilarProducts(question, 5);
            String productInfo = similarProducts.stream()
                    .map(Document::getText)
                    .collect(Collectors.joining("\n"));

            String systemPrompt = """
                        You are a helpful e-commerce shopping assistant for E-Shop.
                        Your job is to guide clients and suggest products based on their questions.
                        Use the 'aiSearchTool' tool to lookup product information in the database.
                        Always return product name, price, brand, and stock details when making suggestions.
                        Be friendly, polite, and helpful.

                        Here are some relevant product details from our database:
                        %s
                    """.formatted(productInfo);

            return chatClient.prompt()
                    .user(question)
                    .advisors(advisorSpec -> advisorSpec.param("chat_memory_conversation_id", sessionId))
                    .system(systemPrompt)
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
                    Use the 'aiSearchTool' tool to lookup product information in the database.
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
                .tools(aiSearchTool)
                .advisors(advisorSpec -> advisorSpec.param("chat_memory_conversation_id", sessionId))
                .call()
                .content();

        return response;
    }

    @PostMapping("/ingest")
    public String ingestProduct(@RequestParam Integer productId) {
        try {
            productVectorService.insertSingleProductToVectorStore(productService.getProductById(productId));
            return "Product ingested successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error ingesting product: " + e.getMessage();
        }
    }

    @PostMapping("/ingest-all")
    public String ingestAll() {
        try {
            List<Product> products = productService.getAllProducts();
            productVectorService.insertProductToVectorStore(products);
            return "Đã đồng bộ toàn bộ sản phẩm có sẵn sang Vector Database!";
        } catch (Exception e) {
            return "Lỗi: " + e.getMessage();
        }
    }

}