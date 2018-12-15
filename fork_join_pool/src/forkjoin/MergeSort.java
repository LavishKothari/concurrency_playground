package forkjoin;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

final public class MergeSort {
    private MergeSort() {

    }

    public static <T extends Comparable<? super T>> void sort(List<T> list) {
        new ForkJoinPool().commonPool().invoke(new RecursiveMergeSortAction<T>(list, 0, list.size()));
    }


    private static class RecursiveMergeSortAction<T extends Comparable<? super T>> extends RecursiveAction {

        private final List<T> list;
        private final int start, end;
        private final int threshold;
        private static final int DEFAULT_THRESHOLD = 1000;

        RecursiveMergeSortAction(List<T> list, int start, int end) {
            this(list, start, end, DEFAULT_THRESHOLD);
        }

        RecursiveMergeSortAction(List<T> list, int start, int end, int threshold) {
            this.list = list;
            this.end = end;
            this.start = start;
            this.threshold = threshold;
        }

        @Override
        public void compute() {
            if (end - start <= threshold) {
                Collections.sort(list.subList(start, end));
            } else {
                RecursiveMergeSortAction leftAction = new RecursiveMergeSortAction(list, start, (start + end)/2, threshold);
                RecursiveMergeSortAction rightAction = new RecursiveMergeSortAction(list, (start + end)/2, end, threshold);
                leftAction.fork();
                rightAction.compute();
                leftAction.join();
            }
        }
    }
}
