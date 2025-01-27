import DI.DI;

import controllers.impl.MainController;
import lombok.SneakyThrows;

public class Main {
    @SneakyThrows
    public static void main(String[] args) {

//        BookDAO bookDAO = new BookDAOImpl(DriverManager.getConnection(
//                "jdbc:mysql://localhost:3306/bookstore",
//                "root", "6132"));
//        Optional<Book> book = bookDAO.getBookById(3);
//        if(book.isEmpty()){
//            System.out.println("Empty");
//        }
//        else {
//            System.out.println(book.get().getInfoAbout());
//        }

        DI di = DI.getInstance();

        di.registerBean(MainController.class, new MainController());

        MainController mainController = di.getBean(MainController.class);
        mainController.run();
    }
}
