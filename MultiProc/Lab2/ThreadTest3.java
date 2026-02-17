import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadTest3 {

    // Диапазон поиска
    private static final int MAX_NUMBER = 200_000;
    private static final int THREAD_COUNT = 4;

    // Общий счетчик (курсор), указывающий на следующее непроверенное число
    private static final AtomicInteger currentNumber = new AtomicInteger(2);

    // Потокобезопасный список для хранения результатов
    private static final List<Integer> primeNumbers = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        Thread[] threads = new Thread[THREAD_COUNT];

        // Создаем и запускаем потоки
        for (int i = 0; i < THREAD_COUNT; i++) {
            threads[i] = new Thread(new PrimeWorker(i));
            threads[i].start();
        }

        // Ждем завершения всех потоков
        for (Thread t : threads) {
            t.join();
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Поиск завершен.");
        System.out.println("Найдено простых чисел: " + primeNumbers.size());
        System.out.println("Затраченное время: " + (endTime - startTime) + " мс");
    }

    // Рабочий класс
    static class PrimeWorker implements Runnable {
        private int threadId;
        private int tasksCompleted = 0; // Для статистики: сколько чисел проверил этот поток

        public PrimeWorker(int id) {
            this.threadId = id;
        }

        @Override
        public void run() {
            while (true) {
                // АТОМАРНО берем число и увеличиваем счетчик
                // Это критическая секция, но она очень быстрая (процессорная инструкция CAS)
                int numberToCheck = currentNumber.getAndIncrement();

                // Условие выхода
                if (numberToCheck > MAX_NUMBER) {
                    break;
                }

                if (isPrime(numberToCheck)) {
                    primeNumbers.add(numberToCheck);
                }
                tasksCompleted++;
            }
            System.out.println("Поток-" + threadId + " завершил работу. Проверено чисел: " + tasksCompleted);
        }

        // Метод проверки на простоту
        private boolean isPrime(int n) {
            if (n < 2)
                return false;
            if (n == 2 || n == 3)
                return true;
            if (n % 2 == 0 || n % 3 == 0)
                return false;

            for (int i = 5; i * i <= n; i += 6) {
                if (n % i == 0 || n % (i + 2) == 0)
                    return false;
            }
            return true;
        }
    }
}
