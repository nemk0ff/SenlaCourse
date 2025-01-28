package view;

import model.impl.Request;

import java.util.List;
import java.util.Map;

public interface RequestsMenu extends Menu {
    void showRequests(Map<Long, Long> requests);

    void showRequests(List<Request> requests);
}
