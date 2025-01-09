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
        LocalTime timeStart = LocalTime.now();
        LocalTime timeEnd;
        while (true) {
            timeEnd = LocalTime.now();
            if (timeEnd.toSecondOfDay() - timeStart.toSecondOfDay() >= n) {
                System.out.println(LocalTime.now());
                timeStart = timeEnd;
            }
        }
    }
}
