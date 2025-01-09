package firstTask;

import lombok.SneakyThrows;

public class CustomThread extends Thread {
    @SneakyThrows
    @Override
    public void run() {
        Thread.sleep(200);

        // Поток запущен
        System.out.println("Состояние: " + Thread.currentThread().getState() + "; Ожидаемое состояние: RUNNABLE");

        First.blocker();

        First.awaiting();
    }
}
