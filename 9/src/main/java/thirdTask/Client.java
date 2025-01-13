package thirdTask;

import lombok.SneakyThrows;

public class Client extends Thread {
    @SneakyThrows
    @Override
    public void run() {
        while (Third.operations < 25) {
            synchronized (Third.bufferMonitor) {
                while (Third.buffer.isEmpty()) {
                    System.out.println("client ждёт");
                    Third.bufferMonitor.wait();
                }

                int i = Third.random.nextInt();
                if (i < 0) {
                    i *= -1;
                }
                Third.buffer.remove(i % Third.buffer.size());

                System.out.println("client потребил число");
                Third.buffer.forEach(k -> System.out.print(k + "; "));
                System.out.println();

                Third.bufferMonitor.notifyAll();
            }
            Third.operations++;
            Thread.sleep(2000);
        }
    }
}
