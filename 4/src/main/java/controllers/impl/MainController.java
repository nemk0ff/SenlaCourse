package controllers.impl;

import constants.IOConstants;
import controllers.Action;
import controllers.Controller;
import controllers.RequestsController;
import DTO.MainManagerDTO;
import managers.MainManager;
import managers.impl.MainManagerImpl;
import view.impl.MainMenu;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;

public class MainController implements Controller {
    private final MainMenu mainMenu;
    private final MainManager mainManager;
    private final BooksControllerImpl booksController;
    private final OrdersControllerImpl ordersController;
    private final RequestsController requestsController;


    public MainController() {
        this.mainManager = deserialization();
        this.mainMenu = new MainMenu();
        this.booksController = new BooksControllerImpl(mainManager);
        this.ordersController = new OrdersControllerImpl(mainManager);
        this.requestsController = new RequestsControllerImpl(mainManager);
    }

    @Override
    public Action run() {
        mainMenu.showMenu();
        Action action = checkInput();

        while (action == Action.CONTINUE || action == Action.MAIN_MENU) {
            mainMenu.showMenu();
            action = checkInput();
        }

        serialization();

        return Action.EXIT;
    }

    @Override
    public Action checkInput() {
        int answer = (int) getNumberFromConsole();

        return switch (answer) {
            case 1 -> booksController.run();
            case 2 -> ordersController.run();
            case 3 -> requestsController.run();
            case 4 -> Action.EXIT;
            default -> {
                mainMenu.showError("Неизвестная команда");
                yield Action.CONTINUE;
            }
        };
    }

    private long getNumberFromConsole() {
        long answer;
        while (true) {
            try {
                answer = InputUtils.getNumberFromConsole();
                break;
            } catch (NumberFormatException e) {
                mainMenu.showError("Неверный формат, попробуйте еще раз");
            }
        }
        return answer;
    }

    private MainManager deserialization() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        File file = new File(IOConstants.SERIALIZATION_PATH);
        MainManagerDTO deserializedLibrary = null;
        try {
            deserializedLibrary = mapper.readValue(file, MainManagerDTO.class);
        } catch (IOException e) {
            System.out.printf(e.getMessage());
        }

        return new MainManagerImpl(deserializedLibrary);
    }

    private void serialization() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MainManagerDTO dto = new MainManagerDTO(mainManager);
        File file = new File(IOConstants.SERIALIZATION_PATH);
        try {
            mapper.writeValue(file, dto);
        } catch (IOException e) {
            System.out.printf(e.getMessage());
        }
    }
}
