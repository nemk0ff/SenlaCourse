package secondTask;

import lombok.SneakyThrows;

public class Second {
    public static final Object monitor = new Object();
    public static boolean flag = true;

    public static void start() {
        FirstThread firstThread = new FirstThread();
        SecondThread secondThread = new SecondThread();
        firstThread.start();
        secondThread.start();
    }

    @SneakyThrows
    public static void demonstrateMultithreading() {
        synchronized (monitor) {
            if (Thread.currentThread() instanceof FirstThread) {
                while (!flag) {
                    monitor.wait();
                }
                System.out.println(Thread.currentThread().getName());
                Thread.sleep(500);
                flag = false;
                monitor.notify();
            } else if (Thread.currentThread() instanceof SecondThread) {
                while (flag) {
                    monitor.wait();
                }
                System.out.println(Thread.currentThread().getName());
                Thread.sleep(500);
                flag = true;
                monitor.notify();
            }
        }
    }
}
