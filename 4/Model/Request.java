package Model;

public class Request {
    static long counter = 0L;

    private final long id;
    private final long bookId;
    private RequestStatus status;

    public Request(long bookId) {
        this.bookId = bookId;
        status = RequestStatus.OPEN;

        counter++;
        id = counter;
    }

    public long getId() {
        return id;
    }

    public long getBook() {
        return bookId;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void closeRequest() {
        status = RequestStatus.CLOSED;
    }

    public String getInfoAbout(){
        return "id запроса: [" + id + "], статус: " + status.toString();
    }
}
