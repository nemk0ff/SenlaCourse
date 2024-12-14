package Controllers;

public interface BooksController extends Controller {
    String importPath = "4/Import/importBooks.csv";
    String exportPath = "4/Export/exportBook.csv";

    void addBook();

    void writeOff();

    void showBookDetails();

    void getBooksByAlphabet();

    void getBooksByDate();

    void getBooksByPrice();

    void getBooksByAvailable();

    void getStaleBooksByDate();

    void getStaleBooksByPrice();

    void importFromFile();
    void exportToFile();
}
