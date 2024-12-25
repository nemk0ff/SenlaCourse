package DTO;

import java.util.List;

public record OrdersManagerDTO(List<OrderDTO> orders, List<RequestDTO> requests) { }
