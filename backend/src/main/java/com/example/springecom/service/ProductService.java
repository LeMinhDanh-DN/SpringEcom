package com.example.springecom.service;

import com.example.springecom.model.Product;
import com.example.springecom.service.AI.ProductVectorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.springecom.repo.ProductRepo;
import org.springframework.web.multipart.MultipartFile;
import com.example.springecom.exception.ProductNotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    private final ProductRepo repo;
    private final ProductVectorService productVectorService;
    private final CloudinaryService cloudinaryService;

    public ProductService(ProductRepo repo, ProductVectorService service, CloudinaryService cloudinaryService) {
        this.repo = repo;
        this.productVectorService = service;
        this.cloudinaryService = cloudinaryService;
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    @Transactional
    public Product setProduct(Product p, MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            Map<String, String> uploadResult = cloudinaryService.uploadImage(image);
            p.setImageUrl(uploadResult.get("url"));
            p.setImagePublicId(uploadResult.get("publicId"));
            p.setImageName(image.getOriginalFilename());
            p.setImageType(image.getContentType());
        }

        Product savedProduct = repo.save(p);

        productVectorService.insertSingleProductToVectorStore(savedProduct);

        return savedProduct;
    }

    @Transactional(readOnly = true)
    public Product getProductById(int id) {
        return repo.findById(id).orElseThrow(() -> new ProductNotFoundException("cant find product with id " + id));
    }

    @Transactional(readOnly = true)
    public String getImageUrlById(int id) {
        Product p = getProductById(id);
        return p.getImageUrl();
    }

    @Transactional
    public Product updateProduct(int id, Product p, MultipartFile img) {
        Product existingProduct = getProductById(id);
        p.setId(id);

        try {
            if (img != null && !img.isEmpty()) {
                // Delete existing image on Cloudinary if present
                if (existingProduct.getImagePublicId() != null) {
                    cloudinaryService.deleteImage(existingProduct.getImagePublicId());
                }

                Map<String, String> uploadResult = cloudinaryService.uploadImage(img);
                p.setImageUrl(uploadResult.get("url"));
                p.setImagePublicId(uploadResult.get("publicId"));
                p.setImageName(img.getOriginalFilename());
                p.setImageType(img.getContentType());
            } else {
                // Keep existing image details if no new image uploaded
                p.setImageUrl(existingProduct.getImageUrl());
                p.setImagePublicId(existingProduct.getImagePublicId());
                p.setImageName(existingProduct.getImageName());
                p.setImageType(existingProduct.getImageType());
            }

            return repo.save(p);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process product image: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteProduct(int id) {
        Product p = getProductById(id);
        if (p.getImagePublicId() != null && !p.getImagePublicId().isEmpty()) {
            cloudinaryService.deleteImage(p.getImagePublicId());
        }
        repo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Product> searchProduct(String key) {
        return repo.searchProducts(key);
    }

}

