package com.microservices.java.neel.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class InventoryResponseDTO {
    private String skuCode;
    private Boolean isInStock;
}
