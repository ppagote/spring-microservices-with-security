package org.pranav.restaurantservice.controller;

import lombok.AllArgsConstructor;
import org.pranav.restaurantservice.dto.OrderResponseDTO;
import org.pranav.restaurantservice.service.RestaurantService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restaurant")
@AllArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping("/home")
    public String greetingMessage() {
        return restaurantService.greeting();
    }

    @GetMapping("/orders/{orderId}")
    public OrderResponseDTO getOrder(@PathVariable String orderId) {

        return restaurantService.getOrder(orderId);
    }
}
