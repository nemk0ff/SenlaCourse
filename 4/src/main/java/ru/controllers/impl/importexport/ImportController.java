package ru.controllers.impl.importexport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.manager.MainManager;
import ru.model.BookStatus;
import ru.model.Item;
import ru.model.OrderStatus;
import ru.model.RequestStatus;
import ru.model.impl.Book;
import ru.model.impl.Order;
import ru.model.impl.Request;

@Slf4j
public class ImportController {
  @Setter
  private static MainManager mainManager;

  public static final DateTimeFormatter flexibleDateTimeFormatter = new DateTimeFormatterBuilder()
      .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
      .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))
      .toFormatter();


  public static Book bookParser(String[] parts) {
    if (parts.length != 9) {
      throw new IllegalArgumentException("Неверное количество частей в строке: " + parts.length);
    }
    long id = Long.parseLong(parts[0].trim());
    String name = parts[1].trim();
    String author = parts[2].trim();
    int publicationYear = Integer.parseInt(parts[3].trim());
    int amount = Integer.parseInt(parts[4].trim());
    double price = Double.parseDouble(parts[5].trim());
    LocalDateTime lastDeliveredDate = parts[6].trim().equals("null")
        ? null : LocalDateTime.parse(parts[6].trim(), flexibleDateTimeFormatter);
    LocalDateTime lastSaleDate = parts[7].trim().equals("null")
        ? null : LocalDateTime.parse(parts[7].trim(), flexibleDateTimeFormatter);
    BookStatus status = Book.getStatusFromString(parts[8].trim(), amount);

    return new Book(id, name, author, publicationYear,
        amount, price, lastDeliveredDate, lastSaleDate, status);
  }

  public static Request requestParser(String[] parts) {
    if (parts.length != 4) {
      throw new IllegalArgumentException("Неверное количество частей в строке: " + parts.length);
    }
    long id = Long.parseLong(parts[0].trim());
    long book_id = Long.parseLong(parts[1].trim());
    int amount = Integer.parseInt(parts[2].trim());
    RequestStatus status = RequestStatus.valueOf(parts[3].trim());

    return new Request(id, mainManager.getBook(book_id), amount, status);
  }

  public static Order orderParser(String[] parts) {
    if (parts.length < 7) {
      throw new IllegalArgumentException("Неверное количество частей в строке: " + parts.length);
    }
    long id = Long.parseLong(parts[0].trim());
    String name = parts[1].trim();
    double price = Double.parseDouble(parts[2].trim());
    OrderStatus status = OrderStatus.valueOf(parts[3].trim());
    LocalDateTime orderDate = parts[4].trim().equals("null")
        ? null : LocalDateTime.parse(parts[4].trim(), flexibleDateTimeFormatter);
    LocalDateTime completeDate = parts[5].trim().equals("null")
        ? null : LocalDateTime.parse(parts[5].trim(), flexibleDateTimeFormatter);

    Map<Long, Integer> books = new HashMap<>();
    for (int i = 6; i < parts.length; i += 2) {
      long bookId = Long.parseLong(parts[i].trim());
      int amount = Integer.parseInt(parts[i + 1].trim());
      books.put(bookId, amount);
    }
    return new Order(id, status, price, orderDate, completeDate, name, books);
  }

  private static <T extends Item> Optional<T>
  findItemInFile(Long targetBookId, String importPath, Function<String[], T> parser) {
    try (BufferedReader reader = new BufferedReader(new FileReader(importPath))) {
      reader.readLine();

      String line;
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split(",");
        long id = Long.parseLong(parts[0].trim());

        if (id == targetBookId) {
          return Optional.of(parser.apply(parts));
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return Optional.empty();
  }

  public static <T extends Item> List<T>
  importAllItemsFromFile(String importPath, Function<String[], T> parser) {
    log.info("Импортируем данные из файла: {}...", importPath);
    List<T> items = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(importPath))) {
      reader.readLine();
      String line;
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split(",");
        items.add(parser.apply(parts));
      }
    } catch (IOException e) {
      log.error("Ошибка при импорте: {}", e.getMessage());
      return new ArrayList<>();
    }
    log.info("Выполнен импорт данных из файла: {}.", importPath);
    return items;
  }
}
