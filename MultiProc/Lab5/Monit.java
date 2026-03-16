import java.io.*;

public class Monit {
    static final String POISON_PILL = "EOF_MARKER_POISON_PILL";

    static class CustomBuffer {
        private String[] buffer;
        private int count = 0, in = 0, out = 0;

        public CustomBuffer(int size) {
            buffer = new String[size];
        }

        public synchronized void put(String item) throws InterruptedException {
            while (count == buffer.length) {
                wait();
            }
            buffer[in] = item;
            in = (in + 1) % buffer.length;
            count++;
            notifyAll();
        }

        public synchronized String take() throws InterruptedException {
            while (count == 0) {
                wait();
            }
            String item = buffer[out];
            out = (out + 1) % buffer.length;
            count--;
            notifyAll();
            return item;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String inputFile = "input.txt";
        String outputFile = "output_mq2.txt";

        CustomBuffer monitor = new CustomBuffer(1000);

        System.out.println("Start Producer-Consumer (wait/notify)...");
        long startTime = System.currentTimeMillis();

        Thread producer = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
                String line;
                while ((line = reader.readLine()) != null)
                    monitor.put(line);
                monitor.put(POISON_PILL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread consumer = new Thread(() -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                while (true) {
                    String line = monitor.take();
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
        System.out.println("Wait/Notify time: " + (time / 1000.0) + " sec.");
    }
}
