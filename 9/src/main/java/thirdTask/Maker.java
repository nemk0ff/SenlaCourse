package thirdTask;

import lombok.SneakyThrows;

public class Maker extends Thread {
    @SneakyThrows
    @Override
    public void run() {
        while (Third.operations < 25) {
            synchronized (Third.bufferMonitor) {
                while (Third.bufferCapacity == Third.buffer.size()) {
                    System.out.println("maker ждёт");
                    Third.bufferMonitor.wait();
                }
                Third.buffer.add(Third.random.nextInt() % 100);

                System.out.println("maker добавил число");
                Third.buffer.forEach(k -> System.out.print(k + "; "));
                System.out.println();

                Third.bufferMonitor.notifyAll();
            }
            Third.operations++;
            Thread.sleep(1000);
        }
    }
}
