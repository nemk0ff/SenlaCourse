package Controllers;

import Model.Book;
import Model.MainManager;
import View.BooksMenuImpl;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Scanner;

public class BooksControllerImpl implements Controller, BooksController{
    private final MainManager mainManager;
    private final BooksMenuImpl booksMenu;
    private final Scanner scanner;


    public BooksControllerImpl(MainManager mainManager){
        this.mainManager = mainManager;
        this.booksMenu = new BooksMenuImpl();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public boolean run() {
        booksMenu.showMenu();

        while(!checkInput()){
            booksMenu.showMenu();
            checkInput();
        }
        return true;
    }

    @Override
    public boolean checkInput() {
        int answer = scanner.nextInt();
        scanner.nextLine();

        return switch (answer) {
            case 1:
                addBook();
                yield false;
            case 2 :
                writeOff();
                yield false;
            case 3 :
                showBookDetails();
                yield false;
            case 4 :
                getBooksByAlphabet();
                yield false;
            case 5 :
                getBooksByDate();
                yield false;
            case 6 :
                getBooksByPrice();
                yield false;
            case 7 :
                getBooksByAvailable();
                yield false;
            case 8 :
                getStaleBooksByDate();
                yield false;
            case 9 :
                getStaleBooksByPrice();
                yield false;
            case 10 : yield true;
            default :
                booksMenu.showInputError();
                yield false;
        };
    };

    @Override
    public Book getBookFromConsole(){
        // TODO: добавить проверки на ввод некорректных данных, обернуть в Optional
        booksMenu.showGetName();
        String name = scanner.nextLine();

        booksMenu.showGetAuthor();
        String author = scanner.nextLine();

        booksMenu.showGetPublicationDate();
        Integer publicationDate = scanner.nextInt();
        scanner.nextLine();

        booksMenu.showGetPrice();
        Integer price = scanner.nextInt();
        scanner.nextLine();

        return new Book(name, author, price, publicationDate);
    }

    @Override
    public void addBook() {
        Book book = getBookFromConsole();

        booksMenu.showGetAmountAdd();
        Integer amount = scanner.nextInt();
        scanner.nextLine();

        mainManager.addBook(book, amount, LocalDate.now());
    }

    @Override
    public void writeOff() {
        Optional<Book> maybeBook = mainManager.getBookDetails(getBookFromConsole());

        if(maybeBook.isEmpty()) {
            booksMenu.showError("Книга не найдена");
            return;
        }

        booksMenu.showGetAmountWriteOff();
        Integer amount = scanner.nextInt();
        scanner.nextLine();

        if(maybeBook.get().getAmount() < amount){
            booksMenu.showError("Количество книг на складе меньше количества, которое вы хотите списать");
            return;
        }

        mainManager.writeOff(maybeBook.get(), amount, LocalDate.now());
        booksMenu.showSuccess("Списание книг произведено успешно!");
    }

    @Override
    public void showBookDetails() {
        Optional<Book> maybeBook = mainManager.getBookDetails(getBookFromConsole());
        if(maybeBook.isEmpty()){
            booksMenu.showError("Книга не найдена");
        }
        else{
            booksMenu.showBook(maybeBook.get());
        }
    }

    @Override
    public void getBooksByAlphabet() { booksMenu.showBooks(mainManager.getBooksByAlphabet()); }

    @Override
    public void getBooksByDate() { booksMenu.showBooks(mainManager.getBooksByDate()); }

    @Override
    public void getBooksByPrice() { booksMenu.showBooks(mainManager.getBooksByPrice()); }

    @Override
    public void getBooksByAvailable() { booksMenu.showBooks(mainManager.getBooksByAvailable()); }

    @Override
    public void getStaleBooksByDate() { booksMenu.showBooks(mainManager.getStaleBooksByDate()); }

    @Override
    public void getStaleBooksByPrice() { booksMenu.showBooks(mainManager.getStaleBooksByPrice()); }
}
