package ru.bookstore.dto.mappers;

import java.util.List;
import ru.bookstore.dto.BookDTO;
import ru.bookstore.model.impl.Book;
import org.mapstruct.factory.Mappers;
import org.mapstruct.Mapper;

@Mapper
public interface BookMapper {
  BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

  BookDTO toDTO(Book book);

  List<BookDTO> toListDTO(List<Book> books);
}
