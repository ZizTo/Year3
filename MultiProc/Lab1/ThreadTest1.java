public class ThreadTest1 {
    static long kol = 200000;
    static int id = 0;

    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            int currentID = id++;
            System.out.println(currentID +" Triggered");
            long startTime = System.currentTimeMillis();

            long count = CountToKol();

            long elapsedTime = System.currentTimeMillis() - startTime;
            System.out.println("Thread " + currentID + " counted " +
             count + " primes in " + (elapsedTime/1000.0) + " seconds.");
        });

        Thread thread2 = new Thread(() -> {
            int currentID = id++;
            System.out.println(currentID +" Triggered");
            long startTime = System.currentTimeMillis();

            long count = CountToKol();

            long elapsedTime = System.currentTimeMillis() - startTime;
            System.out.println("Thread " + currentID + " counted " +
             count + " primes in " + (elapsedTime/1000.0) + " seconds.");
        });

        Thread thread3 = new Thread(() -> {
            int currentID = id++;
            System.out.println(currentID +" Triggered");
            long startTime = System.currentTimeMillis();

            long count = CountToKol();

            long elapsedTime = System.currentTimeMillis() - startTime;
            System.out.println("Thread " + currentID + " counted " +
             count + " primes in " + (elapsedTime/1000.0) + " seconds.");
        });

        Thread thread4 = new Thread(() -> {
            int currentID = id++;
            System.out.println(currentID +" Triggered");
            long startTime = System.currentTimeMillis();

            long count = CountToKol();

            long elapsedTime = System.currentTimeMillis() - startTime;
            System.out.println("Thread " + currentID + " counted " +
             count + " primes in " + (elapsedTime/1000.0) + " seconds.");
        });
        
        System.out.print("\n\n");
        
        long startTime = System.currentTimeMillis();

        
        try{
            thread.start();
            thread.join();
            
            thread2.start();
            thread2.join();
            

            thread3.start();
            thread3.join();

            thread4.start();
            thread4.join();
            
            
        }
        catch(InterruptedException ex) {
            System.out.println("Error while waiting: " + ex);
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("\nTime elapsed: " + elapsedTime/1000.0 + "sec");
        System.out.println("Threads work ended");
    }

    static long CountToKol() {
        int prKol = 0;
        
        for (int i = 1; i < kol; i++) {
            Boolean isPr = true;
            for (int j = 2; j < i; j++) {
                if (i % j == 0) {
                    isPr = false;
                    break;
                }
            }
            prKol += isPr ? 1 : 0;
        }
        
        return prKol;
    }
}