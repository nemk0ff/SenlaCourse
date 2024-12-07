package View;

public interface Menu {
    void showMenu();
    void showInputError();
    default void showError(String error){ System.out.println("Error: " + error); }
    default void showSuccess(String success){ System.out.println("Success: " + success); }
}
