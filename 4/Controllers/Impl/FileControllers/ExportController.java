package Controllers.Impl.FileControllers;

import View.BooksMenu;
import View.Menu;
import View.OrdersMenu;
import View.RequestsMenu;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ExportController {
    private static final String bookHeader =
            "id;name;author;publicationYear;amount;price;lastDeliveredDate;lastSaleDate";
    private static final String orderHeader =
            "id;clientName;price;status;orderDate;completeDate;book1;amount1;book2;amount2;...;bookN;amountN";
    private static final String requestHeader = "id;bookId;status";


    public static <T> void exportAll(Menu menu, List<T> items, String exportPath) {
        items.forEach(item -> exportItemToFile(menu, item.toString(), exportPath));
        menu.showSuccess("Экспорт выполнен успешно");
    }

    public static void exportItemToFile(Menu menu, String exportString, String exportPath) {
        List<String> newFileStrings = new ArrayList<>();
        newFileStrings.add(getHeader(menu));

        String[] exportParts = exportString.split(",");
        long exportId = Long.parseLong(exportParts[0].trim());

        boolean isUpdated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(exportPath))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                long id = Long.parseLong(parts[0].trim());
                if (id == exportId) {
                    newFileStrings.add(exportString);
                    isUpdated = true;
                } else {
                    newFileStrings.add(line);
                }
            }
        } catch (IOException e) {
            menu.showError("IOException: " + e.getMessage());
            return;
        }

        if (!isUpdated) {
            newFileStrings.add(exportString);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(exportPath))) {
            for (String line : newFileStrings) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            menu.showError("IOException: " + e.getMessage());
        }
    }

    public static String getHeader(Menu menu){
        if(menu instanceof BooksMenu){
            return bookHeader;
        } else if(menu instanceof OrdersMenu){
            return orderHeader;
        } else if(menu instanceof RequestsMenu){
            return requestHeader;
        }
        return "";
    }
}
