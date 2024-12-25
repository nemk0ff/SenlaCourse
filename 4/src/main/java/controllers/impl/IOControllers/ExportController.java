package controllers.impl.IOControllers;


import view.impl.ImportExportMenuImpl;
import view.ImportExportMenu;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ExportController {
    private static final ImportExportMenu menu = new ImportExportMenuImpl();

    public static <T> void exportAll(List<T> items, String exportPath, String header) {
        items.forEach(item -> exportItemToFile(item.toString(), exportPath, header));
        menu.showSuccessExport();
    }

    public static void exportItemToFile(String exportString, String exportPath, String header) {
        List<String> newFileStrings = new ArrayList<>();
        newFileStrings.add(header);

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
            menu.showExportError("IOException: " + e.getMessage());
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
            menu.showExportError("IOException: " + e.getMessage());
        }
    }
}
