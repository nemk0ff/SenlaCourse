package DTO;

import managers.OrdersManager;

import java.util.List;
import java.util.stream.Collectors;

public record OrdersManagerDTO(List<OrderDTO> orders, List<RequestDTO> requests) {
    public OrdersManagerDTO(OrdersManager ordersManager) {
        this(ordersManager.getOrders().values().stream()
                        .map(order -> new OrderDTO(order.getId(),
                                order.getClientName(),
                                order.getStatus(),
                                order.getBooks(),
                                order.getPrice(),
                                order.getOrderDate(),
                                order.getCompleteDate()))
                        .collect(Collectors.toList()),
                ordersManager.getRequests().values().stream()
                        .map(request -> new RequestDTO(request.getId(),
                                request.getBookId(),
                                request.getAmount(),
                                request.getStatus()))
                        .collect(Collectors.toList()));
    }
}
