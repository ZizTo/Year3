import java.io.*;

public class Posl {
    public static void main(String[] args) throws IOException {
        String inputFile = "input.txt";
        String outputFile = "output_posl.txt";

        generateTestFile(inputFile);

        System.out.println("Start Sequential...");
        long startTime = System.currentTimeMillis();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(reverseWordsInLine(line));
                writer.newLine();
            }
        }

        long time = System.currentTimeMillis() - startTime;
        System.out.println("time: " + (time / 1000.0) + " sec.");
    }

    static String reverseWordsInLine(String line) {
        String[] words = line.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            sb.append(new StringBuilder(words[i]).reverse().toString());
            if (i < words.length - 1)
                sb.append(" ");
        }
        return sb.toString();
    }

    static final int kol = 100;
    static final int kolInOne = 50000;

    static void generateTestFile(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("Generating test file...");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (int i = 0; i < kol; i++) {
                    for (int j = 0; j < kolInOne; j++) {
                        writer.write("Hello world this is a test file for multithreading laboratory");
                    }
                    writer.newLine();
                }
            }
        }
    }
}
