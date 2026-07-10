package com.example.springecom.service;

import com.example.springecom.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.springecom.repo.ProductRepo;
import org.springframework.web.multipart.MultipartFile;
import com.example.springecom.exception.ProductNotFoundException;
import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepo repo;
    private final ProductVectorService productVectorService;

    public ProductService(ProductRepo repo, ProductVectorService service){
        this.repo = repo;
        this.productVectorService = service;
    }

    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    public Product setProduct(Product p, MultipartFile image) throws IOException {
        p.setImageName(image.getOriginalFilename());
        p.setImageType(image.getContentType());
        p.setImageData(image.getBytes());

        Product savedProduct = repo.save(p);

        productVectorService.insertSingleProductToVectorStore(savedProduct);

        return savedProduct;
    }

    public Product getProductById(int id) {
        return repo.findById(id).orElseThrow(() -> new ProductNotFoundException("cant find product with id " + id));
    }

    public byte[] getImageById(int id) {
        Product p = repo.findById(id).orElseThrow(() -> new ProductNotFoundException("cant find product with id " + id));
        return p.getImageData();
    }

    public Product updateProduct(int id, Product p, MultipartFile img) {
        try {
            p.setId(id);
            p.setImageName(img.getOriginalFilename());
            p.setImageType(img.getContentType());
            p.setImageData(img.getBytes());

            return repo.save(p);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process product image: " + e.getMessage());
        }

    }

    public void deleteProduct(int id){
        if(!repo.existsById(id)){
            throw new ProductNotFoundException("cant find product with id " + id);
        }
        repo.deleteById(id);
    }

    public List<Product> searchProduct(String key){
        return repo.searchProducts(key);
    }


}
