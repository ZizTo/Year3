import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Ex24 {
    private final static int MAX = 100000;

    private record Result(int maxDivisorCount, int intWithMax) {
    }

    private static class Task implements Callable<Result> {
        int min, max;

        Task(int min, int max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public Result call() {
            int maxDivisors = 0;
            int whichInt = 0;
            for (int i = min; i <= max; i++) {
                int divisors = countDivisors(i);
                if (divisors > maxDivisors) {
                    maxDivisors = divisors;
                    whichInt = i;
                }
            }
            return new Result(maxDivisors, whichInt);
        }
    }

    public static void main(String[] args) {
        int[] threadCounts = { 2, 4, 8 };
        for (int threads : threadCounts) {
            countDivisorsWithExecutor(threads);
        }
    }

    private static void countDivisorsWithExecutor(int numberOfThreads) {
        long startTime = System.currentTimeMillis();

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<Result>> results = new ArrayList<>();

        int numberOfTasks = (MAX + 999) / 1000;
        for (int i = 0; i < numberOfTasks; i++) {
            int start = i * 1000 + 1;
            int end = Math.min((i + 1) * 1000, MAX);

            Future<Result> futureResult = executor.submit(new Task(start, end));
            results.add(futureResult);
        }

        executor.shutdown();

        int maxDivisorCount = 0;
        int intWithMaxDivisorCount = 0;

        for (Future<Result> future : results) {
            try {
                Result result = future.get();
                if (result.maxDivisorCount > maxDivisorCount) {
                    maxDivisorCount = result.maxDivisorCount;
                    intWithMaxDivisorCount = result.intWithMax;
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.printf("[Ex 12.4] Threads: %d | Max divisors: %d (for number %d) | Time: %.3f sec\n",
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
