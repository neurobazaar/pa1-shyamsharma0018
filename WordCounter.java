import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class WordSorter {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java WordSorter <input_directory> <output_directory>");
            return;
        }

        Path inputDir = Paths.get(args[0]);
        Path outputDir = Paths.get(args[1]);

        try {
            processFiles(inputDir, outputDir);
        } catch (IOException e) {
            System.err.println("Error processing files: " + e.getMessage());
        }
    }

    private static void processFiles(Path inputDir, Path outputDir) throws IOException {
        Files.walkFileTree(inputDir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                try {
                    sortWordsInFile(file, outputDir);
                } catch (Exception e) {
                    System.err.println("Error processing file: " + file.getFileName() + " - " + e.getMessage());
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static void sortWordsInFile(Path inputFile, Path outputDir) throws IOException {
        long startTime = System.nanoTime();

        Path outputFile = outputDir.resolve(inputFile.getParent().relativize(inputFile));

        Map<String, Integer> wordCounts = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(inputFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid input format in file: " + inputFile);
                }
                String word = parts[0];
                int count = Integer.parseInt(parts[1]);
                wordCounts.put(word, count);
            }
        }

        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(wordCounts.entrySet());
        sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        try (BufferedWriter writer = Files.newBufferedWriter(outputFile)) {
            for (Map.Entry<String, Integer> entry : sortedEntries) {
                writer.write(entry.getKey() + " " + entry.getValue() + "\n");
            }
        }

        long endTime = System.nanoTime();
        double elapsedTimeMillis = (endTime - startTime) / 1000000.0;
        System.out.printf("Time to sort words in %s: %.2f milliseconds\n", inputFile.getFileName(), elapsedTimeMillis);
    }
}
