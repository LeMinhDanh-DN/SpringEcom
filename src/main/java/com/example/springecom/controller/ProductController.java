package com.example.springecom.controller;

import com.example.springecom.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.springecom.service.ProductService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class ProductController {

    @Autowired
    private ProductService service;

    //get all products
    @GetMapping("products")
    public ResponseEntity< List<Product> >getProducts(){
        return new ResponseEntity<>(service.getAllProducts(), HttpStatus.OK);
    }
    //add product
    @PostMapping("product")
    public ResponseEntity<?> addProduct(@RequestPart Product product, @RequestPart MultipartFile imageFile){
        Product savedP = null;
        try {
            savedP = service.setProduct(product, imageFile);
            return new ResponseEntity<>(savedP, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    //get product by id
    @GetMapping("product/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable() int id){
        Product p = service.getProductById(id);
        return new ResponseEntity<>(p,HttpStatus.OK);
    }

    //get product image
    @GetMapping("product/{id}/image")
    public ResponseEntity<byte[]> getImageByProductId(@PathVariable int id){

        byte[] image = service.getImageById(id);
        return new ResponseEntity<>(image, HttpStatus.OK);
    }

    //update
    @PutMapping("product/{id}")
    public ResponseEntity<String> updateProductById(@PathVariable int id, @RequestPart Product product, @RequestPart MultipartFile imageFile){
        service.updateProduct(id, product, imageFile);

        return new ResponseEntity<>("Updated!", HttpStatus.OK);
    }

    //delete
    @DeleteMapping("product/{id}")
    public ResponseEntity<String> deleteProductById(@PathVariable int id){
        service.deleteProduct(id);

        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }

    @GetMapping("products/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword){
        List<Product> ps = service.searchProduct(keyword);
        System.out.println("Searching with" + keyword);
        return new ResponseEntity<>(ps, HttpStatus.OK);
    }
}
