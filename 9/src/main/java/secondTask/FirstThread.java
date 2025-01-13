package secondTask;

public class FirstThread extends Thread {
    @Override
    public void run() {
        int i = 0;
        while (i < 10) {
            Second.demonstrateMultithreading();
            i++;
        }
    }
}
