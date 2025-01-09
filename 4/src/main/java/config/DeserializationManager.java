package config;

import DTO.LibraryManagerDTO;
import DTO.OrdersManagerDTO;
import annotations.DIComponentDependency;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import constants.IOConstants;
import managers.MainManager;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class DeserializationManager {
    @DIComponentDependency
    ObjectMapper mapper;

    public void deserialize(MainManager mainManager) {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

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
        mainManager.getLibraryManager().initialize(deserializedLibrary);
        mainManager.getOrdersManager().initialize(deserializedOrders);
    }

}