package thirdTask;

import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Third {
    public static int operations = 0;
    public static final Object bufferMonitor = new Object();
    public static List<Integer> buffer = new ArrayList<>();
    public static final int bufferCapacity = 5;
    public static Random random = new Random();

    @SneakyThrows
    public static void start() {
        Client client = new Client();
        Maker maker = new Maker();
        client.start();
        maker.start();
    }
}
