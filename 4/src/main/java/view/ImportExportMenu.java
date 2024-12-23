package view;

public interface ImportExportMenu{
    void showGetImportId();

    void showImportDataMessage();

    void showImportData(String line);

    void showSuccessExport();

    void showExportError(String message);

    void showImportError(String message);

    void showInputError(String message);
}
