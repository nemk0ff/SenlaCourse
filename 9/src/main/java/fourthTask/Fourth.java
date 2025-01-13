package fourthTask;

public class Fourth {
    public static void start() {
        CustomThread customThread = new CustomThread(5);
        customThread.start();
    }
}
