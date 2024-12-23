package view.impl;

import view.Menu;

public class MainMenu implements Menu {
    @Override
    public void showMenu() {
        System.out.println("##############################");
        System.out.println("#######  ГЛАВНОЕ МЕНЮ  #######");
        System.out.println("##############################");
        System.out.println("    Выберите действие:");
        System.out.println("1. Действие с книгами");
        System.out.println("2. Действие с заказами");
        System.out.println("3. Действие с запросами");
        System.out.println("4. Выйти из программы");
    }
}
