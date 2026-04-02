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
public class MatrixMultiplication {
    
    /**
     * Multiplies two matrices sequentially.
     *
     * @param matrixA First matrix
     * @param matrixB Second matrix
     * @return Resulting matrix from the multiplication
     */
    public static double[][] multiplySequential(double[][] matrixA, double[][] matrixB) {
        int rowsA = matrixA.length;
        int colsA = matrixA[0].length;
        int colsB = matrixB[0].length;

        if (colsA != matrixB.length) {
            throw new IllegalArgumentException(
                "Number of columns in matrix A must equal number of rows in matrix B"
            );
        }

        double[][] result = new double[rowsA][colsB];

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                for (int k = 0; k < colsA; k++) {
                    result[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }

        return result;
    }

    /**
     * Multiplies two matrices concurrently, dividing the work by rows.
     *
     * @param matrixA First matrix
     * @param matrixB Second matrix
     * @param numThreads Number of threads to be used
     * @return Resulting matrix from the multiplication
     * @throws Exception If an error occurs during multiplication
     */
    public static double[][] multiplyConcurrent(double[][] matrixA, double[][] matrixB, int numThreads) throws Exception {
        int rowsA = matrixA.length;
        int colsA = matrixA[0].length;
        int colsB = matrixB[0].length;

        if (colsA != matrixB.length) {
            throw new IllegalArgumentException(
                "Number of columns in matrix A must equal number of rows in matrix B"
            );
        }

        double[][] result = new double[rowsA][colsB];

        numThreads = Math.max(1, Math.min(numThreads, rowsA));

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        int rowsPerThread = (int) Math.ceil((double) rowsA / numThreads);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int t = 0; t < numThreads; t++) {
            final int startRow = t * rowsPerThread;
            final int endRow = Math.min((t + 1) * rowsPerThread, rowsA);

            if (startRow >= rowsA) {
                break;
            }

            tasks.add(() -> {
                for (int i = startRow; i < endRow; i++) {
                    for (int j = 0; j < colsB; j++) {
                        for (int k = 0; k < colsA; k++) {
                            result[i][j] += matrixA[i][k] * matrixB[k][j];
                        }
                    }
                }
                return null;
            });
        }

        List<Future<Void>> futures = executor.invokeAll(tasks);

        for (Future<Void> future : futures) {
            future.get();
        }

        executor.shutdown();

        return result;
    }

    /**
     * Prints a matrix to the console.
     *
     * @param matrix Matrix to be printed
     * @param name Name of the matrix
     */
    public static void printMatrix(double[][] matrix, String name) {
        System.out.println("Matrix " + name + ":");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.printf("%8.2f ", matrix[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Main method for demonstration and testing.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        try {
            String filePath = "data/sample_data.csv";
            int matrixSize = 9;

            System.out.println("Reading matrices from CSV file...");
            double[][][] matrices = CSVReader.readTwoSquareMatrices(filePath, matrixSize);
            double[][] matrixA = matrices[0];
            double[][] matrixB = matrices[1];

            printMatrix(matrixA, "A");
            printMatrix(matrixB, "B");

            System.out.println("Multiplying matrices sequentially...");
            long startTime = System.nanoTime();
            double[][] resultSequential = multiplySequential(matrixA, matrixB);
            long endTime = System.nanoTime();
            long sequentialTime = endTime - startTime;
            System.out.println("Execution time (sequential): " + sequentialTime / 1_000_000.0 + " ms");

            printMatrix(resultSequential, "Result (Sequential)");

            int[] threadCounts = {2, 4, 8, 16};
            for (int numThreads : threadCounts) {
                System.out.println("Multiplying matrices concurrently with " + numThreads + " threads...");
                startTime = System.nanoTime();
                double[][] resultConcurrent = multiplyConcurrent(matrixA, matrixB, numThreads);
                endTime = System.nanoTime();
                long concurrentTime = endTime - startTime;
                System.out.println("Execution time (concurrent): " + concurrentTime / 1_000_000.0 + " ms");
                System.out.println("Speedup: " + (double) sequentialTime / concurrentTime);

                boolean resultsMatch = true;
                for (int i = 0; i < resultSequential.length; i++) {
                    for (int j = 0; j < resultSequential[0].length; j++) {
                        if (Math.abs(resultSequential[i][j] - resultConcurrent[i][j]) > 1e-10) {
                            resultsMatch = false;
                            break;
                        }
                    }
                    if (!resultsMatch) {
                        break;
                    }
                }
                System.out.println("Results match: " + resultsMatch);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}