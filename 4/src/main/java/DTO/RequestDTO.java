package DTO;

import model.impl.Request;
import model.RequestStatus;

public record RequestDTO(
        long id,
        long bookId,
        int amount,
        RequestStatus status) {
    RequestDTO(Request request) {
        this(
            request.getId(),
            request.getBookId(),
            request.getAmount(),
            request.getStatus()
        );
    }
}
