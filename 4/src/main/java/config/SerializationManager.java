package config;

import DTO.*;
import annotations.DIComponent;
import annotations.DIComponentDependency;
import com.fasterxml.jackson.databind.ObjectMapper;
import constants.IOConstants;
import managers.MainManager;
import DI.DI;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@DIComponent
public class SerializationManager {
    @DIComponentDependency
    ObjectMapper mapper;

    public SerializationManager() {
    }

    public void serialize() {
        DI di = DI.getInstance();
        MainManager mainManager = di.getBean(MainManager.class);

        LibraryManagerDTO libraryManagerDTO =
                new LibraryManagerDTO(mainManager.getBooks().stream().map(BookDTO::new).toList());
        OrdersManagerDTO ordersManagerDTO =
                new OrdersManagerDTO(mainManager.getOrders().stream().map(OrderDTO::new).toList(),
                        mainManager.getRequests().stream().map(RequestDTO::new).toList());
        File file = new File(IOConstants.SERIALIZATION_PATH);

        try {
            Map<String, Object> combinedDTO = new HashMap<>();
            combinedDTO.put("libraryManagerDTO", libraryManagerDTO);
            combinedDTO.put("ordersManagerDTO", ordersManagerDTO);
            mapper.writeValue(file, combinedDTO);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
