package View;

import Model.Items.Impl.Book;
import Model.Items.Impl.Request;

import java.util.LinkedHashMap;
import java.util.List;

public interface RequestsMenu extends Menu {
    void showRequests(LinkedHashMap<Book, Long> requests);

    void showRequests(List<Request> requests);
}
