import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Deleg2 {
    // Queue<Integer> orders = new LinkedList<>();
    static final int howMany = 100000;
    static final int threadsCount = 2;
    static BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(threadsCount * 10);

    public static void main(String[] args) throws InterruptedException {
        System.out.print("\nMain start");
        long startTime = System.currentTimeMillis();

        Thread[] allThreads = new Thread[threadsCount];
        for (int i = 0; i < threadsCount; i++) {
            allThreads[i] = new Thread(() -> {
                try {
                    Worker();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            allThreads[i].start();
        }

        int numbLast = 1;
        while (numbLast < howMany) {
            queue.put(numbLast);
            numbLast += 2;
        }

        for (int i = 0; i < threadsCount; i++) {
            queue.put(-1);
        }
        for (int i = 0; i < threadsCount; i++) {
            allThreads[i].join();
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("all threads ended in " + (elapsedTime / 1000.0) + " seconds.");
    }

    static void Worker() throws InterruptedException {
        int count = 0;

        while (true) {
            int n = queue.take();

            if (n == -1) {
                System.out.println("Thread " + Thread.currentThread().getId()
                        + ": found " + count + " primes");
                return;
            }
            for (int i = 2; i < n; i++) {
                if (n % i == 0) {
                    count -= 1;
                    break;
                }
            }
            count += 1;
        }
    }
}
