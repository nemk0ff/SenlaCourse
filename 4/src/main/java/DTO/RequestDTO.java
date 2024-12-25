package DTO;

import model.RequestStatus;
import model.impl.Request;

public record RequestDTO(
        long id,
        long bookId,
        int amount,
        RequestStatus status) {
    public RequestDTO(Request request) {
        this(
                request.getId(),
                request.getBookId(),
                request.getAmount(),
                request.getStatus()
        );
    }
}
