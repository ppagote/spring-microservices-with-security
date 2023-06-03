package org.pranav.appservice.controller;

import lombok.AllArgsConstructor;
import org.pranav.appservice.client.RestaurantClient;
import org.pranav.appservice.dto.OrderResponseDTO;
import org.pranav.appservice.service.AppService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
@AllArgsConstructor
public class AppController {

    private final AppService appService;
    private final RestaurantClient restaurantClient;

    @GetMapping("/home")
    public String greetingMessage() {
        return appService.greeting();
    }

    @GetMapping("/{orderId}")
    public OrderResponseDTO checkOrderStatus(@PathVariable String orderId) {
        return restaurantClient.getOrder(orderId);
    }
}
