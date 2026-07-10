package com.example.springecom.service;

import com.example.springecom.mapper.ProductAiMapper;
import com.example.springecom.model.Product;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class ProductVectorService {

    private final VectorStore vectorStore;
    private final ProductAiMapper mapper;


    public ProductVectorService(VectorStore vectorStore, ProductAiMapper mapper) {
        this.vectorStore = vectorStore;
        this.mapper = mapper;
    }

    public void insertProductToVectorStore(List<Product> products){

        List<Document> documents = products.stream()
                .map(product -> mapper.toAiDocument(product))
                .toList();

        vectorStore.add(documents);
    }

    public void insertSingleProductToVectorStore(Product product) {
        Document document = mapper.toAiDocument(product);

        vectorStore.accept(List.of(document));

        System.out.println("Đã đồng bộ sản phẩm ID: " + product.getId() + " vào PgVector");
    }

    public List<Document> searchSimilarProducts(String query, int topK){
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .build();

        return vectorStore.similaritySearch(searchRequest);
    }
}
