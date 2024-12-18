package View.Impl;

import View.ImportExportMenu;

public class ImportExportMenuImpl implements ImportExportMenu {
    @Override
    public void showGetImportId(){
        System.out.print("Введите id объекта, который хотите импортировать: ");
    }

    @Override
    public void showImportDataMessage(){
        System.out.println("Доступные данные для импорта:");
    }

    @Override
    public void showImportData(String line){
        System.out.println(line);
    }

    @Override
    public void showSuccessExport(){
        System.out.println("Экспорт выполнен успешно");
    }

    @Override
    public void showExportError(String message){
        System.out.println("Ошибка при экспорте: " + message);
    }

    @Override
    public void showImportError(String message){
        System.out.println("Ошибка при импорте: " + message);
    }

    @Override
    public void showInputError(String message){
        System.out.println(message);
    }
}
