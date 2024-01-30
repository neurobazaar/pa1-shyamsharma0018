import java.io.*;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

public class SimpleFileCleaner {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: java SimpleFileCleaner <Dataset> <cleanedDataset>");
            return;
        }

        Path inputDir = Paths.get(args[0]);
        Path outputDir = Paths.get(args[1]);

        long[] totalBytesCleaned = {0}; // Using an array to make it mutable

        long startTime = System.nanoTime();

        Files.walk(inputDir)
                .filter(Files::isRegularFile)
                .forEachOrdered(inputFile -> {
                    try {
                        long bytesCleaned = cleanFile(inputFile, outputDir, inputDir);
                        totalBytesCleaned[0] += bytesCleaned; // Increment totalBytesCleaned directly
                    } catch (IOException e) {
                        System.err.println("Unable to clean file: " + inputFile + " - " + e.getMessage());
                    }
                });

        long endTime = System.nanoTime();
        long elapsedTimeMillis = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

        printPerformanceMeasurements(totalBytesCleaned[0], elapsedTimeMillis);
    }

    private static long cleanFile(Path inputFile, Path outputDir, Path inputDir) throws IOException {
        Path outputFile = outputDir.resolve(inputDir.relativize(inputFile));
        Files.createDirectories(outputFile.getParent());

        long bytesCleaned = 0;
        try (BufferedReader reader = Files.newBufferedReader(inputFile);
             BufferedWriter writer = Files.newBufferedWriter(outputFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replaceAll("\r", "")
                        .replaceAll("[\\s\\t]+", " ")
                        .replaceAll("[^0-9a-zA-Z\\s]", "");
                bytesCleaned += line.length();
                writer.write(line + "\n");
            }
        }
        return bytesCleaned;
    }

    private static void printPerformanceMeasurements(long totalBytesCleaned, long elapsedTimeMillis) {
        double totalMiB = (double) totalBytesCleaned / 1024.0 / 1024.0;
        double throughputMiBps = (double) totalBytesCleaned / (elapsedTimeMillis / 1000.0) / 1024.0;

        System.out.println("Total bytes cleaned: " + totalBytesCleaned + " bytes (" + totalMiB + " MiB)");
        System.out.println("Total cleaning time in ms: " + elapsedTimeMillis + " milliseconds");
        System.out.println("Speed: " + throughputMiBps + " MiB/s");
    }
}
