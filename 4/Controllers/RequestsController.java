package Controllers;

public interface RequestsController extends Controller{
    void createRequest();
    void getRequestsByCount();
    void getRequestsByPrice();
}
