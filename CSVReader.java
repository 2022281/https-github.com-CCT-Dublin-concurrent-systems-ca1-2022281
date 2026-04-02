/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package concurrent_systems;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
public class CSVReader {
    
    /**
     * Reads a CSV file and returns the data as a list of floating-point numbers.
     * 
     * @param filePath Path to the CSV file
     * @return List of numbers read from the file
     * @throws IOException If a reading error occurs
     */
    public static List<Double> readCSV(String filePath) throws IOException {
        List<Double> data = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                for (String value : values) {
                    data.add(Double.parseDouble(value.trim()));
                }
            }
        }
        
        return data;
    }
    
    /**
     * Reads a CSV file concurrently by dividing the work among multiple threads.
     * 
     * @param filePath Path to the CSV file
     * @param numThreads Number of threads to be used
     * @return List of numbers read from the file
     * @throws Exception If an error occurs during reading
     */
    public static List<Double> readCSVConcurrently(String filePath, int numThreads) throws Exception {
        // First, count the number of lines in the file
        int totalLines = countLines(filePath);
        
        // Calculate how many lines each thread should process
        int linesPerThread = (int) Math.ceil((double) totalLines / numThreads);
        
        // Create a thread pool
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        
        // List to store the tasks
        List<Callable<List<Double>>> tasks = new ArrayList<>();
        
        // Create tasks for each thread
        for (int i = 0; i < numThreads; i++) {
            final int startLine = i * linesPerThread;
            final int endLine = Math.min((i + 1) * linesPerThread, totalLines);
            
            // If there are no more lines to process, stop creating tasks
            if (startLine >= totalLines) {
                break;
            }
            
            // Add the task to the list
            tasks.add(() -> readCSVLines(filePath, startLine, endLine));
        }
        
        // Execute tasks and get results
        List<Future<List<Double>>> results = executor.invokeAll(tasks);
        
        // Combine the results
        List<Double> combinedData = new ArrayList<>();
        for (Future<List<Double>> result : results) {
            combinedData.addAll(result.get());
        }
        
        // Shut down the executor
        executor.shutdown();
        
        return combinedData;
    }
    
    /**
     * Counts the number of lines in a file.
     * 
     * @param filePath Path to the file
     * @return Number of lines in the file
     * @throws IOException If a reading error occurs
     */
    private static int countLines(String filePath) throws IOException {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while (br.readLine() != null) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Reads a specific range of lines from a CSV file.
     * 
     * @param filePath Path to the CSV file
     * @param startLine Starting line (inclusive)
     * @param endLine Ending line (exclusive)
     * @return List of numbers read from the specified range
     * @throws IOException If a reading error occurs
     */
    private static List<Double> readCSVLines(String filePath, int startLine, int endLine) throws IOException {
        List<Double> data = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Skip lines until the start line
            for (int i = 0; i < startLine; i++) {
                br.readLine();
            }
            
            // Read lines within the specified range
            for (int i = startLine; i < endLine; i++) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                
                String[] values = line.split(",");
                for (String value : values) {
                    data.add(Double.parseDouble(value.trim()));
                }
            }
        }
        
        return data;
    }
    
    /**
     * Reads a CSV file and returns the data as a two-dimensional matrix.
     * 
     * @param filePath Path to the CSV file
     * @param rows Number of matrix rows
     * @param cols Number of matrix columns
     * @return Two-dimensional matrix with the read data
     * @throws IOException If a reading error occurs
     */
    public static double[][] readCSVAsMatrix(String filePath, int rows, int cols) throws IOException {
        double[][] matrix = new double[rows][cols];
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            for (int i = 0; i < rows; i++) {
                String line = br.readLine();
                if (line == null) {
                    throw new IOException("File does not contain enough lines");
                }
                
                String[] values = line.split(",");
                if (values.length < cols) {
                    throw new IOException("Line does not contain enough columns");
                }
                
                for (int j = 0; j < cols; j++) {
                    matrix[i][j] = Double.parseDouble(values[j].trim());
                }
            }
        }
        
        return matrix;
    }
    
    /**
     * Reads two consecutive square matrices from a CSV file.
     * 
     * @param filePath Path to the CSV file
     * @param size Size of the square matrices (number of rows/columns)
     * @return Array containing the two matrices
     * @throws IOException If a reading error occurs
     */
    public static double[][][] readTwoSquareMatrices(String filePath, int size) throws IOException {
        double[][][] matrices = new double[2][size][size];
        
        // Read the first matrix
        matrices[0] = readCSVAsMatrix(filePath, size, size);
        
        // Read the second matrix, starting from line `size`
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Skip the lines of the first matrix
            for (int i = 0; i < size; i++) {
                br.readLine();
            }
            
            // Read the lines of the second matrix
            for (int i = 0; i < size; i++) {
                String line = br.readLine();
                if (line == null) {
                    throw new IOException("File does not contain enough lines for the second matrix");
                }
                
                String[] values = line.split(",");
                if (values.length < size) {
                    throw new IOException("Line does not contain enough columns for the second matrix");
                }
                
                for (int j = 0; j < size; j++) {
                    matrices[1][i][j] = Double.parseDouble(values[j].trim());
                }
            }
        }
        
        return matrices;
    }
}
    
