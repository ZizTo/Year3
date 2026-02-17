import java.util.Scanner;

public class ThreadTest2 {

    public static void main(String[] args) throws InterruptedException {
        ProstChislSch worker = new ProstChislSch();
        Thread thread = new Thread(worker);
        
        System.out.println("Enter to stop");
        thread.start();

        Scanner scan = new Scanner(System.in);
        scan.nextLine();

        thread.interrupt();
        thread.join();
        
        System.out.println("Main thread stopped");
    }

    static class ProstChislSch implements Runnable {
        
        private long lastCheckedNumber = 1;
        private int primesCount = 0;

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                lastCheckedNumber++;
                if (isPrime(lastCheckedNumber)) {
                    primesCount++;
                }
            }
            saveState();
        }

        private void saveState() {
            System.out.println("Thread stopped:");
            System.out.println("Stopped on " + lastCheckedNumber + " number");
            System.out.println("Found " + primesCount + " numbers");
        }

        private boolean isPrime(long n) {
            if (n < 2) return false;
            for (long i = 2; i < n; i++) {
                if (n % i == 0) return false;
            }
            return true;
        }
    }
}
