package view.impl;

import lombok.extern.slf4j.Slf4j;
import view.ImportExportMenu;

/**
 * {@code ImportExportMenuImpl} - Реализация интерфейса {@link ImportExportMenu},
 * предоставляющая методы для отображения сообщений, связанных с импортом и экспортом данных.
 */
@Slf4j
public class ImportExportMenuImpl implements ImportExportMenu {
  @Override
  public void showGetImportId() {
    System.out.print("Введите id объекта, который хотите импортировать: ");
  }

  @Override
  public void showImportDataMessage() {
    System.out.println("Доступные данные для импорта:");
  }

  @Override
  public void showImportData(String line) {
    System.out.println(line);
  }

  @Override
  public void showSuccessExport(String str) {
    log.info("Выполнен экспорт: {}", str);
  }

  @Override
  public void showExportError(String message) {
    log.error("Ошибка при экспорте: {}", message);
  }

  @Override
  public void showImportError(String message) {
    log.error("Ошибка при импорте: {}", message);
  }

  @Override
  public void showInputError(String message) {
    log.error(message);
  }
}
