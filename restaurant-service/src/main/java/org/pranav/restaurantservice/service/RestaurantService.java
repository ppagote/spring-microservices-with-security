package org.pranav.restaurantservice.service;

import lombok.AllArgsConstructor;
import org.pranav.restaurantservice.dao.RestaurantDao;
import org.pranav.restaurantservice.dto.OrderResponseDTO;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RestaurantService {

    private final RestaurantDao restaurantDao;

    public String greeting() {
        return "Welcome to Restaurant Service";
    }

    public OrderResponseDTO getOrder(String orderId) {
        return restaurantDao.getOrders(orderId);
    }
}
