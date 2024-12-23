package controllers.impl;

import controllers.Action;
import controllers.BooksController;
import constants.IOConstants;
import controllers.impl.IOControllers.ExportController;
import controllers.impl.IOControllers.ImportController;
import model.impl.Book;
import managers.MainManager;
import view.BooksMenu;
import view.impl.BooksMenuImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class BooksControllerImpl implements BooksController {
    private final MainManager mainManager;
    private final BooksMenu booksMenu;

    public BooksControllerImpl(MainManager mainManager) {
        this.mainManager = mainManager;
        this.booksMenu = new BooksMenuImpl();
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
                getBooksByAlphabet();
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
                ExportController.exportAll(mainManager.getBooks(),
                        IOConstants.EXPORT_BOOK_PATH, IOConstants.BOOK_HEADER);
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
        booksMenu.showBooks(mainManager.getBooks());

        long bookId = getBookId();

        booksMenu.showGetAmountBooks("Сколько книг добавить? Введите число: ");
        int amount = (int) getNumberFromConsole();

        mainManager.addBook(bookId, amount, LocalDate.now());
    }

    @Override
    public void writeOff() {
        booksMenu.showBooks(mainManager.getBooks());

        long id = getBookId();

        booksMenu.showGetAmountBooks("Сколько книг списать? Введите число");
        int amount = (int) getNumberFromConsole();

        while (amount < 0) {
            amount = scanner.nextInt();
            scanner.nextLine();
            booksMenu.showError("Количество книг должно быть положительным числом");
        }

        mainManager.writeOff(id, amount, LocalDate.now());
        booksMenu.showSuccess("Списание книг произведено успешно!");
    }

    @Override
    public void showBookDetails() {
        mainManager.getBook(getBookId()).ifPresent(booksMenu::showItem);
    }

    private long getBookId() {
        long book = getNumberFromConsole();
        while (!mainManager.containsBook(book)) {
            booksMenu.showError("Такой книги нет в магазине");
            book = getNumberFromConsole();
        }
        return book;
    }

    @Override
    public void getBooksByAlphabet() {
        booksMenu.showBooks(mainManager.getBooksByAlphabet());
    }

    @Override
    public void getBooksByDate() {
        booksMenu.showBooks(mainManager.getBooksByDate());
    }

    @Override
    public void getBooksByPrice() {
        booksMenu.showBooks(mainManager.getBooksByPrice());
    }

    @Override
    public void getBooksByAvailable() {
        booksMenu.showBooks(mainManager.getBooksByAvailable());
    }

    @Override
    public void getStaleBooksByDate() {
        booksMenu.showBooks(mainManager.getStaleBooksByDate());
    }

    @Override
    public void getStaleBooksByPrice() {
        booksMenu.showBooks(mainManager.getStaleBooksByPrice());
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
        booksMenu.showBooks(mainManager.getBooks());
        booksMenu.showGetId("Введите id книги, которую хотите экспортировать: ");
        long exportId = getNumberFromConsole();

        String exportString;
        try {
            exportString = getExportString(exportId);
        } catch (IllegalArgumentException e) {
            booksMenu.showError("Книга для экспорта не найдена");
            return;
        }

        booksMenu.showBooks(mainManager.getBooks());
        ExportController.exportItemToFile(exportString, IOConstants.EXPORT_BOOK_PATH, IOConstants.BOOK_HEADER);
        booksMenu.showSuccess("Экспорт выполнен успешно");
    }

    @Override
    public void importAll() {
        List<Book> importedBooks = ImportController.importAllItemsFromFile(IOConstants.IMPORT_BOOK_PATH,
                ImportController::bookParser);

        if (!importedBooks.isEmpty()) {
            importedBooks.forEach(mainManager::importItem);
            booksMenu.showMessage("Все книги успешно импортированы:");
            importedBooks.forEach(booksMenu::showItem);
        } else {
            booksMenu.showError("Не удалось импортировать книги из файла.");
        }
    }

    public String getExportString(long id) {
        Optional<Book> book = mainManager.getBook(id);
        if (book.isPresent()) {
            return book.get().toString();
        }
        throw new IllegalArgumentException();
    }

    private long getNumberFromConsole() {
        long answer;
        while (true) {
            try {
                answer = InputUtils.getNumberFromConsole();
                break;
            } catch (NumberFormatException e) {
                booksMenu.showError("Неверный формат, попробуйте еще раз");
            }
        }
        return answer;
    }
}
