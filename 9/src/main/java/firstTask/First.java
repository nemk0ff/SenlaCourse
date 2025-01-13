package firstTask;

import lombok.SneakyThrows;

public class First {
    public static final Object monitor = new Object();

    @SneakyThrows
    public static void start() {
        CustomThread customThread = new CustomThread();
        // Только что создали поток, еще не запустили
        System.out.println("Состояние: " + customThread.getState() + "; Ожидаемое состояние: NEW");

        HelperThread helper = new HelperThread();
        helper.start();

        Thread.sleep(500);
        customThread.start();
        Thread.sleep(500);
        // Пытается начать выполнение blocker, но он пока что захвачен хелпером
        System.out.println("Состояние: " + customThread.getState() + "; Ожидаемое состояние: BLOCKED");

        Thread.sleep(600);
        // Выполняется команда Thread.sleep в blocker
        System.out.println("Состояние: " + customThread.getState() + "; Ожидаемое состояние: TIME_WAITING");

        Thread.sleep(1500);

        // Поток зашел в awaiting
        System.out.println("Состояние: " + customThread.getState() + "; Ожидаемое состояние: WAITING");

        synchronized (monitor) {
            monitor.notifyAll();
        }

        Thread.sleep(500);
        System.out.println("Состояние: " + customThread.getState() + "; Ожидаемое состояние: TERMINATED");
    }

    @SneakyThrows
    public static synchronized void blocker() {
        Thread.sleep(1500);
    }

    @SneakyThrows
    public static void awaiting() {
        synchronized (monitor) {
            monitor.wait();
        }
    }
}
