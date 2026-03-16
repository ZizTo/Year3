import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Ex23 {
    private final static int MAX = 100000;
    private static ConcurrentLinkedQueue<Task> taskQueue;
    private static LinkedBlockingQueue<Result> resultQueue;

    private record Task(int min, int max) {
        public void compute() {
            int maxDivisors = 0;
            int whichInt = 0;
            for (int i = min; i <= max; i++) {
                int divisors = countDivisors(i);
                if (divisors > maxDivisors) {
                    maxDivisors = divisors;
                    whichInt = i;
                }
            }
            resultQueue.add(new Result(maxDivisors, whichInt));
        }
    }

    private record Result(int maxDivisorFromTask, int intWithMaxFromTask) {
    }

    private static class CountDivisorsThread extends Thread {
        public void run() {
            while (true) {
                Task task = taskQueue.poll();
                if (task == null)
                    break;
                task.compute();
            }
        }
    }

    public static void main(String[] args) {
        int[] threadCounts = { 2, 4, 8 };
        for (int threads : threadCounts) {
            countDivisorsWithThreads(threads);
        }
    }

    private static void countDivisorsWithThreads(int numberOfThreads) {
        long startTime = System.currentTimeMillis();
        resultQueue = new LinkedBlockingQueue<>();
        taskQueue = new ConcurrentLinkedQueue<>();

        CountDivisorsThread[] workers = new CountDivisorsThread[numberOfThreads];
        for (int i = 0; i < workers.length; i++)
            workers[i] = new CountDivisorsThread();

        int numberOfTasks = (MAX + 999) / 1000;
        for (int i = 0; i < numberOfTasks; i++) {
            int start = i * 1000 + 1;
            int end = Math.min((i + 1) * 1000, MAX);
            taskQueue.add(new Task(start, end));
        }

        for (int i = 0; i < numberOfThreads; i++)
            workers[i].start();

        int maxDivisorCount = 0;
        int intWithMaxDivisorCount = 0;
        for (int i = 0; i < numberOfTasks; i++) {
            try {
                Result result = resultQueue.take();
                if (result.maxDivisorFromTask > maxDivisorCount) {
                    maxDivisorCount = result.maxDivisorFromTask;
                    intWithMaxDivisorCount = result.intWithMaxFromTask;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.printf("[Ex 2.3] Threads: %d | Max divisors: %d (for number %d) | Time: %.3f sec\n",
                numberOfThreads, maxDivisorCount, intWithMaxDivisorCount, elapsedTime / 1000.0);
    }

    private static int countDivisors(int N) {
        int count = 0;
        for (int i = 1; i <= N; i++) {
            if (N % i == 0)
                count++;
        }
        return count;
    }
}
