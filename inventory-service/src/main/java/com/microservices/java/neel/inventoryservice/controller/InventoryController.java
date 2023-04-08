package com.microservices.java.neel.inventoryservice.controller;

import com.microservices.java.neel.inventoryservice.dto.InventoryResponseDTO;
import com.microservices.java.neel.inventoryservice.service.InventoryService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponseDTO> isInStock(@RequestParam List<String> skuCodes) {

        return inventoryService.isInStock(skuCodes);

    }
}
