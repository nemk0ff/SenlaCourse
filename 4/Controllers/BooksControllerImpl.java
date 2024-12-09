package Controllers;

import Model.Book;
import Model.MainManager;
import View.BooksMenuImpl;

import java.time.LocalDate;
import java.util.Optional;

public class BooksControllerImpl implements BooksController {
    private final MainManager mainManager;
    private final BooksMenuImpl booksMenu;

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
        int answer;
        while (true) {
            String input = scanner.nextLine();
            try {
                answer = Integer.parseInt(input);
                break;
            } catch (NumberFormatException e) {
                booksMenu.showError("Неверный формат, попробуйте еще раз");
            }
        }

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
                yield Action.MAIN_MENU;
            case 11:
                yield Action.EXIT;
            default:
                booksMenu.showInputError();
                yield Action.CONTINUE;
        };
    }

    ;

    @Override
    public void addBook() {
        Book book = getBookFromConsole(booksMenu);

        booksMenu.showGetAmountAdd();
        Integer amount = scanner.nextInt();
        scanner.nextLine();

        mainManager.addBook(book, amount, LocalDate.now());
    }

    @Override
    public void writeOff() {
        Optional<Book> maybeBook = mainManager.getBookDetails(getBookFromConsole(booksMenu));

        if (maybeBook.isEmpty()) {
            booksMenu.showError("Книга не найдена");
            return;
        }

        booksMenu.showGetAmountWriteOff();
        Integer amount = scanner.nextInt();
        scanner.nextLine();

        if (maybeBook.get().getAmount() < amount) {
            booksMenu.showError("Количество книг на складе меньше количества, которое вы хотите списать");
            return;
        }

        mainManager.writeOff(maybeBook.get(), amount, LocalDate.now());
        booksMenu.showSuccess("Списание книг произведено успешно!");
    }

    @Override
    public void showBookDetails() {
        Optional<Book> maybeBook = mainManager.getBookDetails(getBookFromConsole(booksMenu));
        if (maybeBook.isEmpty()) {
            booksMenu.showError("Книга не найдена");
        } else {
            booksMenu.showBook(maybeBook.get());
        }
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
}
