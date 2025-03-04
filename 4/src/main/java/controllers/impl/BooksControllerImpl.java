package controllers.impl;

import constants.FileConstants;
import controllers.Action;
import controllers.BooksController;
import controllers.impl.importexport.ExportController;
import controllers.impl.importexport.ImportController;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manager.MainManager;
import model.impl.Book;
import org.springframework.stereotype.Component;
import view.BooksMenu;
import view.impl.BooksMenuImpl;

/**
 * {@code BooksControllerImpl} - Реализация интерфейса {@link BooksController},
 * управляющая операциями, связанными с книгами.  Отвечает за обработку
 * пользовательского ввода в меню книг, взаимодействие с сервисами для
 * выполнения операций над книгами и отображение результатов пользователю
 * через меню {@link BooksMenuImpl}.
 */
@Slf4j
@Component
@AllArgsConstructor
public class BooksControllerImpl implements BooksController {
  private final MainManager mainManager;
  private final BooksMenu booksMenu;

  @Override
  public Action run() {
    booksMenu.showMenu();
    Action action = checkInput();

    while (action == Action.CONTINUE) {
      booksMenu.showMenu();
      action = checkInput();
    }
    return action;
  }

  @Override
  public Action checkInput() {
    int answer = (int) getNumberFromConsole();

    return switch (answer) {
      case 1:
        addBook();
        yield Action.CONTINUE;
      case 2:
        writeOff();
        yield Action.CONTINUE;
      case 3:
        showBookDetails();
        yield Action.CONTINUE;
      case 4:
        getBooksByName();
        yield Action.CONTINUE;
      case 5:
        getBooksByDate();
        yield Action.CONTINUE;
      case 6:
        getBooksByPrice();
        yield Action.CONTINUE;
      case 7:
        getBooksByAvailable();
        yield Action.CONTINUE;
      case 8:
        getStaleBooksByDate();
        yield Action.CONTINUE;
      case 9:
        getStaleBooksByPrice();
        yield Action.CONTINUE;
      case 10:
        importBook();
        yield Action.CONTINUE;
      case 11:
        exportBook();
        yield Action.CONTINUE;
      case 12:
        importAll();
        yield Action.CONTINUE;
      case 13:
        exportAll();
        yield Action.CONTINUE;
      case 14:
        yield Action.MAIN_MENU;
      case 15:
        yield Action.EXIT;
      default:
        booksMenu.showInputError("Неизвестная команда");
        yield Action.CONTINUE;
    };
  }

  @Override
  public void addBook() {
    try {
      booksMenu.showBooks(mainManager.getAllBooks());
      long bookId = getBookId();

      booksMenu.showGetAmountBooks();
      int amount = (int) getNumberFromConsole();

      mainManager.addBook(bookId, amount, LocalDateTime.now());
    } catch (Exception e) {
      log.error("При добавлении книг произошла ошибка:", e);
    }
  }

  @Override
  public void writeOff() {
    try {
      booksMenu.showBooks(mainManager.getAllBooks());
      long id = getBookId();

      booksMenu.showGetAmountBooks();
      int amount = (int) getNumberFromConsole();

      mainManager.writeOff(id, amount, LocalDateTime.now());
    } catch (Exception e) {
      log.error("При списании книг произошла ошибка: ", e);
    }
  }

  @Override
  public void showBookDetails() {
    try {
      booksMenu.showItem(mainManager.getBook(getBookId())
          .orElseThrow(() -> new IllegalArgumentException("Книга с таким id не найдена")));
    } catch (Exception e) {
      log.error("При получении книги произошла ошибка: {}", e.getMessage(), e);
    }
  }

  private long getBookId() {
    booksMenu.showGetId("Введите id книги: ");
    return getNumberFromConsole();
  }

  private void showBooks(List<Book> books) {
    try {
      booksMenu.showBooks(books);
    } catch (Exception e) {
      log.error("При выводе книг произошла ошибка: {}", e.getMessage(), e);
    }
  }

  @Override
  public void getBooksByName() {
    showBooks(mainManager.getAllBooksByName());
  }

  @Override
  public void getBooksByDate() {
    showBooks(mainManager.getAllBooksByDate());
  }

  @Override
  public void getBooksByPrice() {
    showBooks(mainManager.getAllBooksByPrice());
  }

  @Override
  public void getBooksByAvailable() {
    showBooks(mainManager.getAllBooksByAvailable());
  }

  @Override
  public void getStaleBooksByDate() {
    showBooks(mainManager.getAllStaleBooksByDate());
  }

  @Override
  public void getStaleBooksByPrice() {
    showBooks(mainManager.getAllStaleBooksByPrice());
  }

  @Override
  public void importBook() {
    try {
      Optional<Book> findBook = ImportController.importItem(FileConstants.IMPORT_BOOK_PATH,
          ImportController::bookParser);
      if (findBook.isPresent()) {
        mainManager.importItem(findBook.get());
      } else {
        booksMenu.showErrorImport();
      }
    } catch (Exception e) {
      log.error("При импорте книги произошла ошибка: {}", e.getMessage(), e);
    }
  }

  @Override
  public void exportBook() {
    try {
      booksMenu.showBooks(mainManager.getAllBooks());
      booksMenu.showGetId("Введите id книги, которую хотите экспортировать: ");
      long exportId = getNumberFromConsole();

      Book exportBook = getExportBook(exportId);

      ExportController.exportItemToFile(exportBook,
          FileConstants.EXPORT_BOOK_PATH, FileConstants.BOOK_HEADER);
    } catch (Exception e) {
      log.error("Ошибка при экспорте: {}", e.getMessage(), e);
    }
  }

  @Override
  public void importAll() {
    try {
      List<Book> importedBooks = ImportController.importAllItemsFromFile(
          FileConstants.IMPORT_BOOK_PATH, ImportController::bookParser);
      if (!importedBooks.isEmpty()) {
        log.info("Формируем из импортированных данных книги...");
        importedBooks.forEach(mainManager::importItem);
        log.info("Импорт всех книг выполнен.");
      } else {
        booksMenu.showError("Не удалось импортировать книги из файла. Возможно, файл пуст");
      }
    } catch (Exception e) {
      log.error("Ошибка при импорте: {}", e.getMessage(), e);
    }
  }

  @Override
  public void exportAll() {
    try {
      ExportController.exportAll(mainManager.getAllBooks(),
          FileConstants.EXPORT_BOOK_PATH, FileConstants.BOOK_HEADER);
    } catch (Exception e) {
      booksMenu.showError(e.getMessage());
    }
  }

  private Book getExportBook(long id) {
    Optional<Book> book = mainManager.getBook(id);
    if (book.isPresent()) {
      return book.get();
    }
    throw new IllegalArgumentException("Книга №" + id + " не найдена");
  }

  private long getNumberFromConsole() {
    long answer;
    while (true) {
      try {
        answer = InputUtils.getNumberFromConsole();
        break;
      } catch (NumberFormatException e) {
        log.warn("Ошибка при попытке ввести число. Попробуйте еще раз: ");
      }
    }
    return answer;
  }
}
