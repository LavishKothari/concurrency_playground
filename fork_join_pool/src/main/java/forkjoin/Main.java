package forkjoin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Main {
    private static final Random rand = new Random();
    private static final int ITERATIONS = 10;
    private static final int ITEMS_PER_LIST = 10000000;

    public static void main(String[] args) {
        for (int i = 0; i < ITERATIONS; i++) {
            runTest();
        }
    }

    private static void runTest() {
        List<Integer> originalList = getRandomIntegerList(ITEMS_PER_LIST, false);
        List<Integer> list1 = new ArrayList<>(originalList);
        List<Integer> list2 = new ArrayList<>(originalList);

        long start1, start2, end1, end2;

        start1 = System.currentTimeMillis();
        MergeSort.sort(list1);
        start2 = end1 = System.currentTimeMillis();
        Collections.sort(list2);
        end2 = System.currentTimeMillis();

        long time1 = end1 - start1;
        long time2 = end2 - start2;
        System.out.println(time1 + " " + time2);

        doBlackHoleOperation(list1);
        doBlackHoleOperation(list2);

    }

    private static List<Integer> getRandomIntegerList(int n, boolean randomize) {
        List<Integer> list = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            if (randomize)
                list.add(rand.nextInt());
            else list.add(i);
        }

        if (!randomize)
            Collections.shuffle(list);

        return list;
    }

    private static void doBlackHoleOperation(List<Integer> list) {
        System.out.println("_______________________________________ " + Collections.max(list));
    }
}
