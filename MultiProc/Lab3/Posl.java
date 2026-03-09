public class Posl {
    static final int howMany = 100000;

    public static void main(String[] args) throws InterruptedException {
        System.out.print("\nMain start");
        long startTime = System.currentTimeMillis();

        int foundNums = 0;

        for (int i = 1; i < howMany; i += 2) {
            if (Worker(i)) {
                foundNums++;
            }
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + foundNums + " nums");
        System.out.println("all threads ended in " + (elapsedTime / 1000.0) + " seconds.");
    }

    static boolean Worker(int n) {
        for (int i = 2; i < n; i++) {
            if (n % i == 0) {
                return false;
            }

        }
        return true;
    }
}
