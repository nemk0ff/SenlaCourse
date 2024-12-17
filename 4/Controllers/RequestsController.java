package Controllers;

public interface RequestsController extends Controller {
    String importPath = "4/Import/importRequests.csv";
    String exportPath = "4/Export/exportRequests.csv";

    void createRequest();

    void getRequestsByCount();

    void getRequestsByPrice();

    void exportRequest();

    void importAll();
}
