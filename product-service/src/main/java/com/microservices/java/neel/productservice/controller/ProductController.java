package com.microservices.java.neel.productservice.controller;

import com.microservices.java.neel.productservice.dto.ProductRequestDTO;
import com.microservices.java.neel.productservice.dto.ProductResponseDTO;
import com.microservices.java.neel.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductRequestDTO productRequest) {
        productService.createProduct(productRequest);

    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponseDTO> getAllProducts(){
      return productService.getAllProducts();
    }
}
