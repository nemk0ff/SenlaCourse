package Controllers.Impl;

import Controllers.Action;
import Controllers.BooksController;
import Model.Book;
import Model.MainManager;
import View.Impl.BooksMenuImpl;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
        int answer = (int) getNumberFromConsole(booksMenu);

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
                importFromFile();
                yield Action.CONTINUE;
            case 11:
                exportToFile();
                yield Action.CONTINUE;
            case 12:
                yield Action.MAIN_MENU;
            case 13:
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
        int amount = (int) getNumberFromConsole(booksMenu);

        mainManager.addBook(bookId, amount, LocalDate.now());
    }

    @Override
    public void writeOff() {
        booksMenu.showBooks(mainManager.getBooks());

        long id = getBookId();

        booksMenu.showGetAmountBooks("Сколько книг списать? Введите число");
        int amount = (int) getNumberFromConsole(booksMenu);

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
        mainManager.getBook(getBookId()).ifPresent(booksMenu::showBook);
    }

    private long getBookId() {
        long book = getBookFromConsole(booksMenu);
        while (!mainManager.containsBook(book)) {
            booksMenu.showError("Такой книги нет в магазине");
            book = getBookFromConsole(booksMenu);
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
    public void importFromFile() {
        printImportFile();

        booksMenu.showMessage("Введите id книги, которую хотите импортировать");
        long bookId = getNumberFromConsole(booksMenu);

        Optional<Book> findBook = findBookInFile(bookId);

        if (findBook.isPresent()) {
            mainManager.importBook(findBook.get());
            booksMenu.showMessage("Книга импортирована:");
            findBook.ifPresent(booksMenu::showBook);
        } else {
            booksMenu.showError("Не удалось получить книгу из файла");
        }
    }

    public Optional<Book> findBookInFile(Long targetBookId) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try (BufferedReader reader = new BufferedReader(new FileReader(importPath))) {
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 8) {
                    long id = Long.parseLong(parts[0].trim());

                    if (id == targetBookId) {
                        String name = parts[1].trim();
                        String author = parts[2].trim();
                        int publicationYear = Integer.parseInt(parts[3].trim());
                        int amount = Integer.parseInt(parts[4].trim());
                        double price = Double.parseDouble(parts[5].trim());
                        LocalDate lastDeliveredDate = parts[6].trim().equals("null") ?
                                null : LocalDate.parse(parts[6].trim(), dateFormatter);
                        LocalDate lastSaleDate = parts[7].trim().equals("null") ?
                                null : LocalDate.parse(parts[7].trim(), dateFormatter);

                        return Optional.of(new Book(id, name, author, amount, price, publicationYear,
                                lastDeliveredDate, lastSaleDate));
                    }
                }
            }
        } catch (IOException e) {
            booksMenu.showError("IOException");
        }
        return Optional.empty();
    }

    public void printImportFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(importPath))) {
            booksMenu.showMessage("Вот, какие книги можно импортировать: ");
            String line;
            while ((line = reader.readLine()) != null) {
                booksMenu.showMessage("[" + line + "]");
            }
        } catch (IOException e) {
            System.err.println(importPath + ": " + e.getMessage());
        }
    }

    @Override
    public void exportToFile() {
        booksMenu.showBooks(mainManager.getBooks());
        long exportId = getBookId();
        String exportString = "";
        if(mainManager.getBook(exportId).isPresent()){
            exportString = mainManager.getBook(exportId).get().toString();
        }

        List<String> newFileStrings = new ArrayList<>();

        String firstString = "id;name;author;publicationYear;amount;price;lastDeliveredDate;lastSaleDate";
        newFileStrings.add(firstString);

        boolean bookIsUpdated = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(exportPath))) {
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                long id = Long.parseLong(parts[0].trim());
                if (id == exportId) {
                    newFileStrings.add(exportString);
                    bookIsUpdated = true;
                } else {
                    newFileStrings.add(line);
                }
            }
        } catch (IOException e) {
            booksMenu.showError("IOException: " + e.getMessage());
            return;
        }

        if (!bookIsUpdated) {
            newFileStrings.add(exportString);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(exportPath))) {
            for (String line : newFileStrings) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            booksMenu.showError("IOException: " + e.getMessage());
        }

        booksMenu.showSuccess("Книга успешно экспортирована");
    }
}
