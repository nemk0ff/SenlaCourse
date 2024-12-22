package model.impl;

import DTO.RequestDTO;
import model.Item;
import model.RequestStatus;

public class Request implements Item {
    static long counter = 0L;

    private final long id;
    private final long bookId;
    private final int amount;
    private RequestStatus status;

    // Конструктор для запросов, которые создаются в логике магазина
    public Request(long bookId, int amount) {
        this.bookId = bookId;
        this.amount = amount;
        status = RequestStatus.OPEN;

        counter++;
        id = counter;
    }

    // Конструктор для запросов, которые импортируются
    public Request(long id, long bookId, int amount, RequestStatus status) {
        this.id = id;
        this.bookId = bookId;
        this.amount = amount;
        this.status = status;
    }

    public Request(RequestDTO dto) {
        this.id = dto.getId();
        this.bookId = dto.getBookId();
        this.amount = dto.getAmount();
        this.status = dto.getStatus();
    }

    @Override
    public long getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public long getBookId() {
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
        return "[" + id + "]   книга №" + bookId + ", количество: " + amount + ",  статус:" + status;
    }

    @Override
    public String toString() {
        return id + "," + bookId + "," + status.toString();
    }
}
