package controllers.impl;

import annotations.ComponentDependency;
import constants.FileConstants;
import controllers.Action;
import controllers.BooksController;
import controllers.impl.importexport.ExportController;
import controllers.impl.importexport.ImportController;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.NoArgsConstructor;
import manager.MainManagerImpl;
import model.impl.Book;
import view.impl.BooksMenuImpl;

/**
 * {@code BooksControllerImpl} - Реализация интерфейса {@link BooksController},
 * управляющая операциями, связанными с книгами.  Отвечает за обработку
 * пользовательского ввода в меню книг, взаимодействие с сервисами для
 * выполнения операций над книгами и отображение результатов пользователю
 * через меню {@link BooksMenuImpl}.
 */
@NoArgsConstructor
public class BooksControllerImpl implements BooksController {
  @ComponentDependency
  MainManagerImpl mainManager;
  @ComponentDependency
  BooksMenuImpl booksMenu;

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
      booksMenu.showGetAmountBooks("Сколько книг добавить? Введите число: ");
      int amount = (int) getNumberFromConsole();

      mainManager.addBook(bookId, amount, LocalDateTime.now());
      booksMenu.showSuccess("Добавлено " + amount + " книг №" + bookId);
    } catch (IllegalArgumentException e) {
      booksMenu.showError(e.getMessage());
    }
  }

  @Override
  public void writeOff() {
    try {
      booksMenu.showBooks(mainManager.getAllBooks());

      long id = getBookId();
      booksMenu.showGetAmountBooks("Сколько книг списать? Введите число: ");
      int amount = (int) getNumberFromConsole();

      mainManager.writeOff(id, amount, LocalDateTime.now());
      booksMenu.showSuccess("Списано " + amount + " книг №" + id);
    } catch (IllegalArgumentException e) {
      booksMenu.showError(e.getMessage());
    }
  }

  @Override
  public void showBookDetails() {
    try {
      mainManager.getBook(getBookId()).ifPresent(booksMenu::showItem);
    } catch (Exception e) {
      booksMenu.showError(e.getMessage());
    }
  }

  private long getBookId() {
    try {
      booksMenu.showGetId("Введите id книги: ");
      long book = getNumberFromConsole();

      while (!mainManager.containsBook(book)) {
        booksMenu.showError("Не удалось найти книгу №" + book + " в магазине");
        book = getNumberFromConsole();
      }
      return book;
    } catch (Exception e) {
      booksMenu.showError(e.getMessage());
    }
    return getBookId();
  }

  @Override
  public void getBooksByName() {
    try {
      booksMenu.showBooks(mainManager.getAllBooksByName());
    } catch (Exception e) {
      booksMenu.showError(e.getMessage());
    }
  }

  @Override
  public void getBooksByDate() {
    try {
      booksMenu.showBooks(mainManager.getAllBooksByDate());
    } catch (Exception e) {
      booksMenu.showError(e.getMessage());
    }
  }

  @Override
  public void getBooksByPrice() {
    try {
      booksMenu.showBooks(mainManager.getAllBooksByPrice());
    } catch (Exception e) {
      booksMenu.showError(e.getMessage());
    }
  }

  @Override
  public void getBooksByAvailable() {
    try {
      booksMenu.showBooks(mainManager.getAllBooksByAvailable());
    } catch (Exception e) {
      booksMenu.showError(e.getMessage());
    }
  }

  @Override
  public void getStaleBooksByDate() {
    try {
      booksMenu.showBooks(mainManager.getAllStaleBooksByDate());
    } catch (Exception e) {
      booksMenu.showError(e.getMessage());
    }
  }

  @Override
  public void getStaleBooksByPrice() {
    try {
      booksMenu.showBooks(mainManager.getAllStaleBooksByPrice());
    } catch (Exception e) {
      booksMenu.showError(e.getMessage());
    }
  }

  @Override
  public void importBook() {
    try {
      Optional<Book> findBook = ImportController.importItem(FileConstants.IMPORT_BOOK_PATH,
          ImportController::bookParser);
      if (findBook.isPresent()) {
        mainManager.importItem(findBook.get());
        booksMenu.showSuccessImport(findBook.get());
        findBook.ifPresent(booksMenu::showItem);
      } else {
        booksMenu.showErrorImport();
      }
    } catch (IllegalArgumentException e) {
      booksMenu.showError(e.getMessage());
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
    } catch (IllegalArgumentException e) {
      booksMenu.showError(e.getMessage());
    }
  }

  @Override
  public void importAll() {
    try {
      List<Book> importedBooks = ImportController.importAllItemsFromFile(
          FileConstants.IMPORT_BOOK_PATH, ImportController::bookParser);
      if (!importedBooks.isEmpty()) {
        importedBooks.forEach(mainManager::importItem);
        booksMenu.showMessage("Все книги успешно импортированы:");
        importedBooks.forEach(booksMenu::showSuccessImport);
      } else {
        booksMenu.showError("Не удалось импортировать книги из файла.");
      }
    } catch (Exception e) {
      booksMenu.showError(e.getMessage());
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
        booksMenu.showInputError(e.getMessage());
      }
    }
    return answer;
  }
}
