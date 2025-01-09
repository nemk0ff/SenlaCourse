package firstTask;

import lombok.SneakyThrows;

public class HelperThread extends Thread {
    @SneakyThrows
    @Override
    public void run() {
        First.blocker();

        Thread.sleep(1000);
    }
}
