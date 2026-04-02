/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package concurrent_systems;

import java.util.List;
/**
 *
 * @author aldol
 */
public class Main {
     public static void main(String[] args) {
         System.out.println("=== CA1: Application of Concurrency to Common Tasks ===\n");

        try {
            // TASK 1: Standard Deviation
            System.out.println("--- TASK 1: STANDARD DEVIATION ---");
            String filePath = "data/sample_data.csv";
            List<Double> data = CSVReader.readCSV(filePath);
            System.out.println("Loaded " + data.size() + " values");

            long start = System.nanoTime();
            double stdSeq = StandardDeviationCalculator.calculateSequential(data);
            long seqTime = System.nanoTime() - start;
            System.out.printf("Sequential: %.4f (%.2f ms)%n", stdSeq, seqTime / 1_000_000.0);

            start = System.nanoTime();
            double stdCon = StandardDeviationCalculator.calculateConcurrent(data, 4);
            long conTime = System.nanoTime() - start;
            System.out.printf("Concurrent: %.4f (%.2f ms) | Speedup: %.2fx%n%n",
                    stdCon, conTime / 1_000_000.0, (double) seqTime / conTime);

            // TASK 2: Matrix Multiplication
            System.out.println("--- TASK 2: MATRIX MULTIPLICATION (9x9) ---");
            int matrixSize = 9;
            double[][][] matrices = CSVReader.readTwoSquareMatrices(filePath, matrixSize);
            double[][] A = matrices[0];
            double[][] B = matrices[1];

            start = System.nanoTime();
            double[][] resultSeq = MatrixMultiplication.multiplySequential(A, B);
            seqTime = System.nanoTime() - start;
            System.out.printf("Sequential: %.4f ms%n", seqTime / 1_000_000.0);

            start = System.nanoTime();
            double[][] resultCon = MatrixMultiplication.multiplyConcurrent(A, B, 4);
            conTime = System.nanoTime() - start;
            System.out.printf("Concurrent: %.4f ms | Speedup: %.2fx%n",
                    conTime / 1_000_000.0, (double) seqTime / conTime);

            System.out.println("Sample result sequential [0][0]: " + resultSeq[0][0]);
            System.out.println("Sample result concurrent [0][0]: " + resultCon[0][0]);
            System.out.println();

            // TASK 3: Merge Sort
            System.out.println("--- TASK 3: MERGE SORT (DESCENDING) ---");
            double[] array = data.stream().mapToDouble(Double::doubleValue).toArray();

            start = System.nanoTime();
            double[] sortedSeq = MergeSort.sortSequential(array);
            seqTime = System.nanoTime() - start;
            System.out.printf("Sequential largest value: %.2f (%.2f ms)%n",
                    sortedSeq[0], seqTime / 1_000_000.0);

            start = System.nanoTime();
            double[] sortedCon = MergeSort.sortConcurrent(array);
            conTime = System.nanoTime() - start;
            System.out.printf("Concurrent largest value: %.2f (%.2f ms) | Speedup: %.2fx%n%n",
                    sortedCon[0], conTime / 1_000_000.0, (double) seqTime / conTime);

            System.out.println("=== ALL TASKS COMPLETED SUCCESSFULLY ===");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}