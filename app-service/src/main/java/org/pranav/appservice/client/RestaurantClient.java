package org.pranav.appservice.client;

import org.pranav.appservice.dto.OrderResponseDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface RestaurantClient {

    @GetExchange("/restaurant/orders/{orderId}")
    OrderResponseDTO getOrder(@PathVariable String orderId);
}
