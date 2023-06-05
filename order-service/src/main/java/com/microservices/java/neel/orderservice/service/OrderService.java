package com.microservices.java.neel.orderservice.service;

import com.microservices.java.neel.orderservice.dto.InventoryResponseDTO;
import com.microservices.java.neel.orderservice.dto.OrderLineItemsDTO;
import com.microservices.java.neel.orderservice.dto.OrderRequestDTO;
import com.microservices.java.neel.orderservice.event.OrderPlacedEvent;
import com.microservices.java.neel.orderservice.model.Order;
import com.microservices.java.neel.orderservice.model.OrderLineItems;
import com.microservices.java.neel.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {
    //private final WebClient webClient;
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    // private final RestTemplate restTemplate;
    private final Tracer tracer;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public String placeOrder(OrderRequestDTO orderRequest) throws URISyntaxException {

        List<OrderLineItems> orderLineItemsList = orderRequest.getOrderLineItemsDTOList()
                .stream()
                .map(OrderLineItemsDTO::mapToOrderLineItemsDTO)
                .collect(Collectors.toList());
        Order order = Order.builder().orderNumber(UUID.randomUUID().toString())
                .orderLineItemsList(orderLineItemsList)
                .build();

        List<String> skuCodes = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).collect(Collectors.toList());
        // call to inventoryservice and place order if product is in stock
        log.info("Calling inventory service");
        Span inventoryServiceTrace = tracer.nextSpan().name("inventory-service-trace");
        try (Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceTrace.start())) {
            InventoryResponseDTO[] inventoryResponseArray = webClientBuilder.build().get()
                    .uri("http://inventory-service/api/inventory",
                            uriBuilder -> uriBuilder.queryParam("skuCodes", skuCodes).build())
                    .retrieve()
                    .bodyToMono(InventoryResponseDTO[].class)
                    .block();
        /*String url = "http://localhost:8082/api/inventory";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url).queryParam("skuCode", skuCodes);

        ResponseEntity<InventoryResponseDTO[]> responseEntity = restTemplates
                .getForEntity(uriBuilder.toUriString(),
                        InventoryResponseDTO[].class);*/

            assert inventoryResponseArray != null;
            boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponseDTO::getIsInStock);
            //boolean allProductsInStock = Arrays.stream(responseEntity.getBody()).allMatch(InventoryResponseDTO::getIsInStock);
            if (allProductsInStock) {
                orderRepository.save(order);
                kafkaTemplate.send("notificationTopic",new OrderPlacedEvent(order.getOrderNumber() ));
                log.info("Order {} is placed successfully", order.getId());
                return "Order placed successfully";
            } else {
                throw new IllegalArgumentException("Product is not in stock, please try again latter.");
            }
        } finally {
            inventoryServiceTrace.end();
        }

    }


}
