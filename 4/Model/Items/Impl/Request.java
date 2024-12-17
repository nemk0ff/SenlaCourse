package Model.Items.Impl;

import Model.Items.Item;
import Model.Items.RequestStatus;

public class Request implements Item {
    static long counter = 0L;

    private final long id;
    private final long bookId;
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

    @Override
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

    @Override
    public String getInfoAbout() {
        return "[" + id + "]   книга №" + bookId + ",  статус:" + status;
    }

    @Override
    public String toString() {
        return id + "," + bookId + "," + status.toString();
    }
}
