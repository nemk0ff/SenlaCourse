package controllers.impl.IOControllers;

import controllers.impl.InputUtils;
import model.impl.Book;
import model.impl.Order;
import model.impl.Request;
import model.Item;
import model.OrderStatus;
import model.RequestStatus;
import view.impl.ImportExportMenuImpl;
import view.ImportExportMenu;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

public class ImportController {
    private static final ImportExportMenu menu = new ImportExportMenuImpl();


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
        LocalDateTime lastDeliveredDate = parts[6].trim().equals("null") ?
                null : LocalDateTime.parse(parts[6].trim(), dateFormatter);
        LocalDateTime lastSaleDate = parts[7].trim().equals("null") ?
                null : LocalDateTime.parse(parts[7].trim(), dateFormatter);

        return new Book(id, name, author, publicationYear, amount, price, lastDeliveredDate, lastSaleDate);
    }

    public static Request requestParser(String[] parts) {
        if (parts.length != 4) {
            throw new IllegalArgumentException("Неверное количество частей в строке: " + parts.length);
        }
        long id = Long.parseLong(parts[0].trim());
        long bookId = Long.parseLong(parts[1].trim());
        int amount = Integer.parseInt(parts[2].trim());
        RequestStatus status = RequestStatus.valueOf(parts[3].trim());

        return new Request(id, bookId, amount, status);
    }

    public static Order orderParser(String[] parts) {
        if (parts.length < 7) {
            throw new IllegalArgumentException("Неверное количество частей в строке: " + parts.length);
        }
        long id = Long.parseLong(parts[0].trim());
        String name = parts[1].trim();
        double price = Double.parseDouble(parts[2].trim());
        OrderStatus status = OrderStatus.valueOf(parts[3].trim());
        LocalDateTime orderDate = parts[4].trim().equals("null") ?
                null : LocalDateTime.parse(parts[4].trim(), dateFormatter);
        LocalDateTime completeDate = parts[5].trim().equals("null") ?
                null : LocalDateTime.parse(parts[5].trim(), dateFormatter);

        Map<Long, Integer> books = new HashMap<>();
        for (int i = 6; i < parts.length; i += 2) {
            long bookId = Long.parseLong(parts[i].trim());
            int amount = Integer.parseInt(parts[i + 1].trim());
            books.put(bookId, amount);
        }
        return new Order(id, status, price, orderDate, completeDate, name, books);
    }

    public static void printImportFile(String importPath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(importPath))) {
            menu.showImportDataMessage();
            String line;
            while ((line = reader.readLine()) != null) {
                menu.showImportData(line);
            }
        } catch (IOException e) {
            menu.showImportError(importPath + ": " + e.getMessage());
        }
    }

    public static <T extends Item> Optional<T> importItem(String importPath, Function<String[], T> parser) {
        printImportFile(importPath);

        menu.showGetImportId();
        long itemId = getNumberFromConsole();

        return findItemInFile(itemId, importPath, parser);
    }

    public static <T extends Item> Optional<T> findItemInFile(Long targetBookId, String importPath, Function<String[], T> parser) {
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
            menu.showImportError("IOException" + e.getMessage());
        }
        return Optional.empty();
    }

    public static <T extends Item> List<T> importAllItemsFromFile(String importPath, Function<String[], T> parser) {
        List<T> items = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(importPath))) {
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                items.add(parser.apply(parts));
            }
        } catch (IOException e) {
            menu.showImportError("IOException при чтении файла: " + e.getMessage());
            return new ArrayList<>();
        }
        return items;
    }

    private static long getNumberFromConsole() {
        long answer;
        while (true) {
            try {
                answer = InputUtils.getNumberFromConsole();
                break;
            } catch (NumberFormatException e) {
                menu.showInputError("Неверный формат, попробуйте еще раз");
            }
        }
        return answer;
    }
}
