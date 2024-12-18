package Controllers;

public interface RequestsController extends Controller {
    void createRequest();

    void getRequestsByCount();

    void getRequestsByPrice();

    void getAllRequests();

    void exportRequest();

    void importRequest();

    void importAll();
}
