import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Deleg1 {
    static final int howMany = 100000;
    static final int threadsCount = 2;

    static int foundNums = 0;

    public static void main(String[] args) throws InterruptedException {
        System.out.print("\nMain start");
        long startTime = System.currentTimeMillis();

        ExecutorService executor = Executors.newFixedThreadPool(threadsCount);

        for (int i = 1; i < howMany; i += 2) {
            final int currentNum = i;

            executor.submit(() -> {
                Worker(currentNum);
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.DAYS);

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + foundNums + " nums");
        System.out.println("all threads ended in " + (elapsedTime / 1000.0) + " seconds.");
    }

    static void Worker(int n) {
        for (int i = 2; i < n; i++) {
            if (n % i == 0) {
                return;
            }
        }
        foundNums++;
    }
}
