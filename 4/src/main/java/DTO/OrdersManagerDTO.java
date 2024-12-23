package DTO;

import managers.OrdersManager;

import java.util.Map;
import java.util.stream.Collectors;

public record OrdersManagerDTO(Map<Long, OrderDTO> orders, Map<Long, RequestDTO> requests) {
    public OrdersManagerDTO(OrdersManager ordersManager) {
        this(ordersManager.getOrders().entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> new OrderDTO(entry.getValue()))),
                ordersManager.getRequests().entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> new RequestDTO(entry.getValue())))
        );
    }
}
