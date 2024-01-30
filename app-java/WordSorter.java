import java.io.*;
import java.nio.file.*;
import java.util.*;

public class WordSorter {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java WordSorter <datadir> <sorteddatadir>");
            return;
        }

        String datadir = args[0];
        String sorteddatadir = args[1];

        long startTime = System.currentTimeMillis();
        processDirectory(datadir, sorteddatadir);
        long endTime = System.currentTimeMillis();

        System.out.println("Processing completed in " + (endTime - startTime) + " ms");
    }

    private static void processDirectory(String datadir, String sorteddatadir) {
        try {
            Files.walk(Paths.get(datadir))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".txt"))
                    .forEach(path -> processFile(path, sorteddatadir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processFile(Path filePath, String sorteddatadir) {
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            List<Map.Entry<String, Integer>> wordList = readWords(reader);
            wordList.sort((a, b) -> b.getValue().compareTo(a.getValue()));

            Path outputPath = Paths.get(sorteddatadir, filePath.getFileName().toString());
            writeWords(wordList, outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Map.Entry<String, Integer>> readWords(BufferedReader reader) throws IOException {
        Map<String, Integer> wordMap = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            if (parts.length == 2) {
                wordMap.put(parts[0], Integer.parseInt(parts[1]));
            }
        }
        return new ArrayList<>(wordMap.entrySet());
    }

    private static void writeWords(List<Map.Entry<String, Integer>> wordList, Path outputPath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
            for (Map.Entry<String, Integer> entry : wordList) {
                writer.write(entry.getKey() + " " + entry.getValue());
                writer.newLine();
            }
        }
    }
}
