package com.microservices.java.neel.productservice.service;

import com.microservices.java.neel.productservice.dto.ProductRequestDTO;
import com.microservices.java.neel.productservice.dto.ProductResponseDTO;
import com.microservices.java.neel.productservice.model.Product;
import com.microservices.java.neel.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public void createProduct(ProductRequestDTO productRequest) {
        Product product = Product.builder().name(productRequest.getName()).description(productRequest.getDescription()).price(productRequest.getPrice()).build();

        productRepository.save(product);
        log.info("Product {} is saved", product.getId());
    }

    public List<ProductResponseDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(product -> ProductResponseDTO.mapToProductResponseDTO(product)).collect(Collectors.toList());

    }


}
