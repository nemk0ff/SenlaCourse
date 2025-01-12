package fourthTask;

import lombok.SneakyThrows;

import java.time.LocalTime;

public class CustomThread extends Thread {
    private final int n;

    CustomThread(int n) {
        this.n = n;
    }

    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            Thread.sleep(n * 1000L);
            System.out.println(LocalTime.now());
        }
    }
}
