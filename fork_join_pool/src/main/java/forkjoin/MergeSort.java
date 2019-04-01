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

        private static <T extends Comparable<? super T>> List<T> merge(List<T> list1, List<T> list2) {
            List<T> result = new ArrayList<>(list1.size() + list2.size());
            int i, j;
            for (i = 0, j = 0; i < list1.size() && j < list2.size(); ) {
                if (list1.get(i).compareTo(list2.get(j)) <= 0)
                    result.add(list1.get(i++));
                else
                    result.add(list2.get(j++));
            }
            while (i != list1.size())
                result.add(list1.get(i++));
            while (j != list2.size())
                result.add(list2.get(j++));
            return result;
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
                List<T> merged = merge(list.subList(start, (start + end) / 2),
                        list.subList((start + end) / 2, end));
                int counter = 0;
                for (int i = start; i < end; i++) {
                    list.set(i, merged.get(counter++));
                }
            }
        }
    }
}
