package Controllers.Impl.FileControllers;

import Controllers.Controller;
import Model.Items.Impl.Book;
import Model.Items.Impl.Order;
import Model.Items.Impl.Request;
import Model.Items.Item;
import Model.Items.OrderStatus;
import Model.Items.RequestStatus;
import Model.MainManager;
import View.Menu;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

public class ImportController {
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static Book bookParser(String[] parts) {
        if (parts.length != 8) {
            throw new IllegalArgumentException("Неверное количество частей в строке: " + parts.length);
        }
        long id = Long.parseLong(parts[0].trim());
        String name = parts[1].trim();
        String author = parts[2].trim();
        int publicationYear = Integer.parseInt(parts[3].trim());
        int amount = Integer.parseInt(parts[4].trim());
        double price = Double.parseDouble(parts[5].trim());
        LocalDate lastDeliveredDate = parts[6].trim().equals("null") ?
                null : LocalDate.parse(parts[6].trim(), dateFormatter);
        LocalDate lastSaleDate = parts[7].trim().equals("null") ?
                null : LocalDate.parse(parts[7].trim(), dateFormatter);

        return new Book(id, name, author, amount, price, publicationYear, lastDeliveredDate, lastSaleDate);
    }

    public static Request requestParser(String[] parts) {
        if (parts.length != 3) {
            throw new IllegalArgumentException("Неверное количество частей в строке: " + parts.length);
        }
        long id = Long.parseLong(parts[0].trim());
        long bookId = Long.parseLong(parts[1].trim());
        RequestStatus status = RequestStatus.valueOf(parts[2].trim());

        return new Request(id, bookId, status);
    }

    public static Order orderParser(String[] parts) {
        if (parts.length < 7) {
            throw new IllegalArgumentException("Неверное количество частей в строке: " + parts.length);
        }
        long id = Long.parseLong(parts[0].trim());
        String name = parts[1].trim();
        double price = Double.parseDouble(parts[2].trim());
        OrderStatus status = OrderStatus.valueOf(parts[3].trim());
        LocalDate orderDate = parts[4].trim().equals("null") ?
                null : LocalDate.parse(parts[4].trim(), dateFormatter);
        LocalDate completeDate = parts[5].trim().equals("null") ?
                null : LocalDate.parse(parts[5].trim(), dateFormatter);

        Map<Long, Integer> books = new HashMap<>();
        for (int i = 6; i < parts.length; i += 2) {
            long bookId = Long.parseLong(parts[i].trim());
            int amount = Integer.parseInt(parts[i + 1].trim());
            books.put(bookId, amount);
        }
        return new Order(id, name, price, status, orderDate, completeDate, books);
    }

    public static void printImportFile(Menu menu, String importPath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(importPath))) {
            menu.showImportDataMessage();
            String line;
            while ((line = reader.readLine()) != null) {
                menu.showMessage(line);
            }
        } catch (IOException e) {
            menu.showError(importPath + ": " + e.getMessage());
        }
    }

    public static <T extends Item> void importItem(String importPath, Menu menu, MainManager manager, Function<String[], T> parser){
        printImportFile(menu, importPath);

        menu.showGetImportId();
        long itemId = Controller.getNumberFromConsole(menu);

        Optional<T> findItem = findItemInFile(menu, itemId, importPath, parser);

        if (findItem.isPresent()) {
            try {
                manager.importItem(findItem.get());
                menu.showSuccessImport();
                findItem.ifPresent(menu::showItem);
            } catch (IllegalArgumentException e){
                menu.showError(e.getMessage());
            }
        } else {
            menu.showErrorImport();
        }
    }

    public static <T extends Item> Optional<T> findItemInFile(Menu menu, Long targetBookId, String importPath, Function<String[], T> parser) {
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
            menu.showError("IOException" + e.getMessage());
        }
        return Optional.empty();
    }

    public static <T extends Item> List<T> importAllItemsFromFile(Menu menu, String importPath, Function<String[], T> parser) {
        List<T> items = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(importPath))) {
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                items.add(parser.apply(parts));
            }
        } catch (IOException e) {
            menu.showError("IOException при чтении файла: " + e.getMessage());
            return new ArrayList<>();
        }
        return items;
    }
}
