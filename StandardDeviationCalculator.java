/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package concurrent_systems;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author aldol
 */
public class StandardDeviationCalculator {
    
    

    /**
     * Calculates the standard deviation of a data set sequentially.
     *
     * @param data List of numbers for standard deviation calculation
     * @return Standard deviation value
     */
    public static double calculateSequential(List<Double> data) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("The data list cannot be null or empty");
        }

        double sum = 0.0;
        for (Double value : data) {
            sum += value;
        }
        double mean = sum / data.size();

        double squaredDiffSum = 0.0;
        for (Double value : data) {
            double diff = value - mean;
            squaredDiffSum += diff * diff;
        }

        return Math.sqrt(squaredDiffSum / data.size());
    }

    /**
     * Calculates the standard deviation of a data set concurrently.
     *
     * @param data List of numbers for standard deviation calculation
     * @param numThreads Number of threads to use
     * @return Standard deviation value
     * @throws Exception If an error occurs during calculation
     */
    public static double calculateConcurrent(List<Double> data, int numThreads) throws Exception {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("The data list cannot be null or empty");
        }

        numThreads = Math.min(numThreads, data.size());

        double sum = 0.0;
        for (Double value : data) {
            sum += value;
        }
        final double mean = sum / data.size();

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        int chunkSize = (int) Math.ceil((double) data.size() / numThreads);
        List<Callable<Double>> tasks = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            final int startIndex = i * chunkSize;
            final int endIndex = Math.min((i + 1) * chunkSize, data.size());

            if (startIndex >= data.size()) {
                break;
            }

            tasks.add(() -> {
                double partialSum = 0.0;
                for (int j = startIndex; j < endIndex; j++) {
                    double diff = data.get(j) - mean;
                    partialSum += diff * diff;
                }
                return partialSum;
            });
        }

        List<Future<Double>> results = executor.invokeAll(tasks);

        double squaredDiffSum = 0.0;
        for (Future<Double> result : results) {
            squaredDiffSum += result.get();
        }

        executor.shutdown();

        return Math.sqrt(squaredDiffSum / data.size());
    }

    public static void main(String[] args) {
        try {
            String filePath = "data/sample_data.csv";

            System.out.println("Reading data from CSV file...");
            List<Double> data = CSVReader.readCSV(filePath);
            System.out.println("Number of elements: " + data.size());

            System.out.println("\nCalculating standard deviation sequentially...");
            long startTime = System.nanoTime();
            double stdDevSequential = calculateSequential(data);
            long endTime = System.nanoTime();
            long sequentialTime = endTime - startTime;
            System.out.println("Standard deviation (sequential): " + stdDevSequential);
            System.out.println("Execution time (sequential): " + sequentialTime / 1_000_000.0 + " ms");

            int[] threadCounts = {2, 4, 8, 16};
            for (int numThreads : threadCounts) {
                System.out.println("\nCalculating standard deviation concurrently with " + numThreads + " threads...");
                startTime = System.nanoTime();
                double stdDevConcurrent = calculateConcurrent(data, numThreads);
                endTime = System.nanoTime();
                long concurrentTime = endTime - startTime;
                System.out.println("Standard deviation (concurrent): " + stdDevConcurrent);
                System.out.println("Execution time (concurrent): " + concurrentTime / 1_000_000.0 + " ms");
                System.out.println("Speedup: " + (double) sequentialTime / concurrentTime);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}