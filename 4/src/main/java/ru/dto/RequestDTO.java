package ru.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.model.RequestStatus;
import ru.model.impl.Book;
import ru.model.impl.Request;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDTO {
  private Long id;
  @NotEmpty(message = "Необ")
  private Book book;
  private Integer amount;
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private RequestStatus status;

  public RequestDTO(Request request) {
    this.id = request.getId();
    this.book = request.getBook();
    this.amount = request.getAmount();
    this.status = request.getStatus();
  }
}
