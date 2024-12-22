package DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import managers.OrdersManager;

import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class OrdersManagerDTO {
    private Map<Long, OrderDTO> orders;
    private Map<Long, RequestDTO> requests;

    OrdersManagerDTO(OrdersManager ordersManager) {
        this.orders = ordersManager.getOrders().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new OrderDTO(entry.getValue())));

        this.requests = ordersManager.getRequests().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new RequestDTO(entry.getValue())));
    }
}
