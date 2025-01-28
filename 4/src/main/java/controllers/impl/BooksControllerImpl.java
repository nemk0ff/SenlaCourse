package controllers.impl;

import annotations.DIComponentDependency;
import controllers.Action;
import controllers.BooksController;
import constants.IOConstants;
import controllers.impl.IOControllers.ExportController;
import controllers.impl.IOControllers.ImportController;
import manager.MainManagerImpl;
import model.impl.Book;
import view.impl.BooksMenuImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class BooksControllerImpl implements BooksController {
    @DIComponentDependency
    MainManagerImpl mainManager;
    @DIComponentDependency
    BooksMenuImpl booksMenu;

    public BooksControllerImpl() {
    }

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
                booksMenu.showError("Неизвестная команда");
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

            mainManager.addBook(bookId, amount, LocalDate.now());
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

            mainManager.writeOff(id, amount, LocalDate.now());
            booksMenu.showSuccess("Списание книг произведено успешно!");
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
                booksMenu.showError("Такой книги нет в магазине");
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
        Optional<Book> findBook = ImportController.importItem(IOConstants.IMPORT_BOOK_PATH,
                ImportController::bookParser);
        if (findBook.isPresent()) {
            try {
                mainManager.importItem(findBook.get());
                booksMenu.showSuccessImport();
                findBook.ifPresent(booksMenu::showItem);
            } catch (IllegalArgumentException e) {
                booksMenu.showError(e.getMessage());
            }
        } else {
            booksMenu.showErrorImport();
        }
    }

    @Override
    public void exportBook() {
        try {
            booksMenu.showBooks(mainManager.getAllBooks());
            booksMenu.showGetId("Введите id книги, которую хотите экспортировать: ");
            long exportId = getNumberFromConsole();

            String exportString = getExportString(exportId);

            booksMenu.showBooks(mainManager.getAllBooks());
            ExportController.exportItemToFile(exportString, IOConstants.EXPORT_BOOK_PATH, IOConstants.BOOK_HEADER);
            booksMenu.showSuccess("Экспорт выполнен успешно");
        } catch (IllegalArgumentException e) {
            booksMenu.showError(e.getMessage());
        }
    }

    @Override
    public void importAll() {
        List<Book> importedBooks = ImportController.importAllItemsFromFile(IOConstants.IMPORT_BOOK_PATH,
                ImportController::bookParser);
        try {
            if (!importedBooks.isEmpty()) {
                importedBooks.forEach(mainManager::importItem);
                booksMenu.showMessage("Все книги успешно импортированы:");
                importedBooks.forEach(booksMenu::showItem);
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
                    IOConstants.EXPORT_BOOK_PATH, IOConstants.BOOK_HEADER);
        } catch (Exception e) {
            booksMenu.showError(e.getMessage());
        }
    }

    public String getExportString(long id) {
        Optional<Book> book = mainManager.getBook(id);
        if (book.isPresent()) {
            return book.get().toString();
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
                booksMenu.showError(e.getMessage());
            }
        }
        return answer;
    }
}
