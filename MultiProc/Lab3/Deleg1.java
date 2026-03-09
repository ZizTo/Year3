import java.util.concurrent.Semaphore;

public class Deleg1 {
    // Queue<Integer> orders = new LinkedList<>();
    static final int howMany = 100000;
    static final int threadsCount = 2;
    // static BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(threadsCount *
    // 10);
    static Semaphore semaphore = new Semaphore(threadsCount);

    static int foundNums = 0;

    public static void main(String[] args) throws InterruptedException {
        System.out.print("\nMain start");
        long startTime = System.currentTimeMillis();

        for (int i = 1; i < howMany; i += 2) {
            semaphore.acquire();

            final int currentNum = i;

            new Thread(() -> {
                Worker(currentNum);
            }).start();
        }

        semaphore.acquire(threadsCount);

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + foundNums + " nums");
        System.out.println("all threads ended in " + (elapsedTime / 1000.0) + " seconds.");
    }

    static void Worker(int n) {
        for (int i = 2; i < n; i++) {
            if (n % i == 0) {
                semaphore.release();
                return;
            }

        }
        foundNums++;
        semaphore.release();
    }
}
