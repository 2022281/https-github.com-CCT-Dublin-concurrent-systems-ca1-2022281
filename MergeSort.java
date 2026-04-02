/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package concurrent_systems;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;


/**
 *
 * @author aldol
 */
public class MergeSort {
    
 /**
     * Sorts an array sequentially in descending order.
     *
     * @param arr Array to sort
     * @return Sorted copy of the array
     */
    public static double[] sortSequential(double[] arr) {
        double[] result = Arrays.copyOf(arr, arr.length);
        mergeSortSequential(result, 0, result.length - 1);
        return result;
    }

    /**
     * Recursive sequential merge sort.
     */
    private static void mergeSortSequential(double[] arr, int left, int right) {
        if (left >= right) {
            return;
        }

        int mid = left + (right - left) / 2;

        mergeSortSequential(arr, left, mid);
        mergeSortSequential(arr, mid + 1, right);

        merge(arr, left, mid, right);
    }

    /**
     * Merges two sorted halves in descending order.
     */
    private static void merge(double[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        double[] leftArr = new double[n1];
        double[] rightArr = new double[n2];

        for (int i = 0; i < n1; i++) {
            leftArr[i] = arr[left + i];
        }

        for (int j = 0; j < n2; j++) {
            rightArr[j] = arr[mid + 1 + j];
        }

        int i = 0, j = 0, k = left;

        while (i < n1 && j < n2) {
            if (leftArr[i] >= rightArr[j]) {
                arr[k++] = leftArr[i++];
            } else {
                arr[k++] = rightArr[j++];
            }
        }

        while (i < n1) {
            arr[k++] = leftArr[i++];
        }

        while (j < n2) {
            arr[k++] = rightArr[j++];
        }
    }

    /**
     * Sorts an array concurrently in descending order.
     *
     * @param arr Array to sort
     * @return Sorted copy of the array
     */
    public static double[] sortConcurrent(double[] arr) {
        double[] result = Arrays.copyOf(arr, arr.length);

        ForkJoinPool pool = new ForkJoinPool();
        MergeSortTask task = new MergeSortTask(result, 0, result.length - 1);
        pool.invoke(task);
        pool.shutdown();

        return result;
    }

    /**
     * Inner Fork/Join task for concurrent merge sort.
     */
    private static class MergeSortTask extends RecursiveAction {
        private static final int THRESHOLD = 1000;
        private final double[] arr;
        private final int left;
        private final int right;

        public MergeSortTask(double[] arr, int left, int right) {
            this.arr = arr;
            this.left = left;
            this.right = right;
        }

        @Override
        protected void compute() {
            if (left >= right) {
                return;
            }

            if (right - left <= THRESHOLD) {
                mergeSortSequential(arr, left, right);
                return;
            }

            int mid = left + (right - left) / 2;

            MergeSortTask leftTask = new MergeSortTask(arr, left, mid);
            MergeSortTask rightTask = new MergeSortTask(arr, mid + 1, right);

            invokeAll(leftTask, rightTask);

            merge(arr, left, mid, right);
        }
    }
}