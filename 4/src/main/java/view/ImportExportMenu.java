package view;

import model.Item;

/**
 * {@code ImportExportMenu} - Интерфейс, определяющий методы для отображения
 * сообщений, связанных с операциями импорта и экспорта данных.
 */
public interface ImportExportMenu {
  void showGetImportId();

  void showImportDataMessage();

  void showImportData(String line);

  void showSuccessExport(String str);

  void showExportError(String message);

  void showImportError(String message);

  void showInputError(String message);
}
