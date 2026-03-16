import java.io.*;
import java.util.concurrent.Semaphore;

public class Semaph {
    static final String POISON_PILL = "EOF_MARKER_POISON_PILL";

    static class SemaphoreBuffer {
        private String[] buffer;
        private int in = 0, out = 0;

        private Semaphore mutex = new Semaphore(1);
        private Semaphore empty;
        private Semaphore full = new Semaphore(0);

        public SemaphoreBuffer(int size) {
            buffer = new String[size];
            empty = new Semaphore(size);
        }

        public void put(String item) throws InterruptedException {
            empty.acquire();
            mutex.acquire();

            buffer[in] = item;
            in = (in + 1) % buffer.length;

            mutex.release();
            full.release();
        }

        public String take() throws InterruptedException {
            full.acquire();
            mutex.acquire();

            String item = buffer[out];
            out = (out + 1) % buffer.length;

            mutex.release();
            empty.release();

            return item;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String inputFile = "input.txt";
        String outputFile = "output_sem.txt";

        SemaphoreBuffer sharedBuffer = new SemaphoreBuffer(1000);

        System.out.println("Start Producer-Consumer (Semaphores)...");
        long startTime = System.currentTimeMillis();

        Thread producer = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sharedBuffer.put(line);
                }
                sharedBuffer.put(POISON_PILL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread consumer = new Thread(() -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                while (true) {
                    String line = sharedBuffer.take();
                    if (line.equals(POISON_PILL))
                        break;

                    writer.write(Posl.reverseWordsInLine(line));
                    writer.newLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();

        long time = System.currentTimeMillis() - startTime;
        System.out.println("Semaphore time: " + (time / 1000.0) + " sec.");
    }
}
