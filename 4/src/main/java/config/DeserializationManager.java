package config;


import DTO.LibraryManagerDTO;
import DTO.OrdersManagerDTO;
import annotations.DIComponent;
import annotations.DIComponentDependency;
import com.fasterxml.jackson.databind.ObjectMapper;
import constants.IOConstants;
import managers.impl.MainManagerImpl;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@DIComponent
public class DeserializationManager {
    @DIComponentDependency
    ObjectMapper mapper;

    public MainManagerImpl deserialization() {
        File file = new File(IOConstants.SERIALIZATION_PATH);
        LibraryManagerDTO deserializedLibrary = null;
        OrdersManagerDTO deserializedOrders = null;

        try {
            Map<String, Object> ourDTO =
                    mapper.readValue(file, new com.fasterxml.jackson.core.type.TypeReference<>() {
                    });

            deserializedLibrary = mapper.convertValue(ourDTO.get("libraryManagerDTO"), LibraryManagerDTO.class);
            deserializedOrders = mapper.convertValue(ourDTO.get("ordersManagerDTO"), OrdersManagerDTO.class);
        } catch (IOException e) {
            System.out.printf(e.getMessage());
        }
        return new MainManagerImpl(deserializedLibrary, deserializedOrders);
    }

}