package DTO;

import model.RequestStatus;

public record RequestDTO(
        long id,
        long bookId,
        int amount,
        RequestStatus status) {
}
