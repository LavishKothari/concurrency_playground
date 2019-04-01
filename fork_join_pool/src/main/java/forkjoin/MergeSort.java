package forkjoin;

import java.util.ArrayList;
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

        private static final int DEFAULT_THRESHOLD = 1000;
        private final List<T> list;
        private final int start, end;
        private final int threshold;

        RecursiveMergeSortAction(List<T> list, int start, int end) {
            this(list, start, end, DEFAULT_THRESHOLD);
        }

        RecursiveMergeSortAction(List<T> list, int start, int end, int threshold) {
            this.list = list;
            this.end = end;
            this.start = start;
            this.threshold = threshold;
        }

        private void merge(List<T> list, int start, int end) {
            int mid = (start + end) / 2;
            List<T> result = new ArrayList<>(end - start);
            int i, j;
            for (i = start, j = mid; i < mid && j < end; ) {
                if (list.get(i).compareTo(list.get(j)) <= 0)
                    result.add(list.get(i++));
                else result.add(list.get(j++));
            }
            while (i != mid)
                result.add(list.get(i++));
            while (j != end)
                result.add(list.get(j++));
            int counter = start;
            for (int x = 0; x < result.size(); x++)
                list.set(counter++, result.get(x));
        }

        @Override
        public void compute() {
            if (end - start <= threshold) {
                Collections.sort(list.subList(start, end));
            } else {
                RecursiveMergeSortAction leftAction = new RecursiveMergeSortAction(list, start, (start + end) / 2, threshold);
                RecursiveMergeSortAction rightAction = new RecursiveMergeSortAction(list, (start + end) / 2, end, threshold);
                leftAction.fork();
                rightAction.compute();
                leftAction.join();
                merge(list, start, end);
            }
        }
    }
}
