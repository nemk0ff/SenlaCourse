package DTO;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import model.impl.Request;
import model.RequestStatus;
import lombok.Data;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class RequestDTO {
    private final long id;
    private final long bookId;
    private final int amount;
    private RequestStatus status;

    RequestDTO(Request request) {
        this.id = request.getId();
        this.bookId = request.getBookId();
        this.amount = request.getAmount();
        this.status = request.getStatus();
    }
}
