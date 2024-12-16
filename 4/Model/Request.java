package Model;

public class Request {
    static long counter = 0L;

    private final long id;
    private long bookId;
    private RequestStatus status;

    // Конструктор для запросов, которые создаются в логике магазина
    public Request(long bookId) {
        this.bookId = bookId;
        status = RequestStatus.OPEN;

        counter++;
        id = counter;
    }

    // Конструктор для запросов, которые импортируются
    public Request(long id, long bookId, RequestStatus status) {
        this.id = id;
        this.bookId = bookId;
        this.status = status;
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

    public String getInfoAbout() {
        return "[" + id + "]   книга №" + bookId + ",  статус:" + status;
    }

    public void copyOf(Request other) {
        this.bookId = other.bookId;
        this.status = other.status;
    }

    @Override
    public String toString() {
        return id + "," + bookId + "," + status.toString();
    }
}
