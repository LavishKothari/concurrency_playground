package forkjoin;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class MergeSortTest {

    @Test
    public void simpleTest() {
        List<Integer> list = new ArrayList<>();

        int iterations = 10000;
        int maxValue = 10;
        for (int i = 0; i < iterations; i++)
            IntStream.rangeClosed(1, maxValue)
                    .forEach(e -> list.add(e));
        Collections.shuffle(list);

        int counter = 0;
        MergeSort.sort(list);
        for (int i = 1; i <= maxValue; i++) {
            for (int j = 0; j < iterations; j++) {
                Assert.assertEquals(i, (int) list.get(counter++));
            }
        }
    }
}
