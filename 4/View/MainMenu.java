package View;

public class MainMenu implements Menu{
    @Override
    public void showMenu(){
        System.out.println("##############################");
        System.out.println("#######  ГЛАВНОЕ МЕНЮ  #######");
        System.out.println("##############################");
        System.out.println("    Выберите действие:");
        System.out.println("1. Действие с книгами");
        System.out.println("4. Выйти из программы");
    }

    @Override
    public void showInputError(){
        System.out.println("Вы ввели неизвестную команду");
    }
}
