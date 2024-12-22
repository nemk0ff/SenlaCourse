package DTO;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import managers.MainManager;
import managers.impl.MainManagerImpl;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MainManagerDTO {
    private LibraryManagerDTO libraryManagerDTO;
    private OrdersManagerDTO ordersManagerDTO;

    public MainManagerDTO(MainManager mainManager) {
        this.libraryManagerDTO = new LibraryManagerDTO(mainManager.getLibraryManager());
        this.ordersManagerDTO = new OrdersManagerDTO(mainManager.getOrdersManager());
    }
}
