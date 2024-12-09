package View;

import Model.Book;

import java.util.LinkedHashMap;

public interface RequestsMenu extends Menu {
    void showRequests(LinkedHashMap<Book, Long> requests);
}
