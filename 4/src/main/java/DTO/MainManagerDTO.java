package DTO;

import managers.MainManager;

public record MainManagerDTO(
        LibraryManagerDTO libraryManagerDTO,
        OrdersManagerDTO ordersManagerDTO) {
    public MainManagerDTO(MainManager mainManager) {
        this(
            new LibraryManagerDTO(mainManager.getLibraryManager()),
            new OrdersManagerDTO(mainManager.getOrdersManager())
        );
    }
}
