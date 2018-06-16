package core.blockingqueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

public class BlockingQueueTest {

	private static final int DEFAULT_OPERATIONS = 50;
	private static final int DEFAULT_MAX_SLEEP = 500; // ms

	@Test(expected = Test.None.class)
	public void testConcurrentBlockingQueueSimple() throws InterruptedException {
		int operations = DEFAULT_OPERATIONS;
		List<Integer> randomList = getRandomList(operations);
		BlockingQueue<Integer> bq = new BlockingQueueSimple<Integer>(10);
		testConcurrentHelper(bq, randomList, operations);
	}

	@Test(expected = Test.None.class)
	public void testConcurrentBlockingQueueExtended() throws InterruptedException {
		int operations = DEFAULT_OPERATIONS;
		List<Integer> randomList = getRandomList(operations);
		BlockingQueue<Integer> bq = new BlockingQueueExtended<>(10);
		testConcurrentHelper(bq, randomList, operations);
	}

	private <T> void testConcurrentHelper(BlockingQueue<T> bq, List<T> randomList, int operations) {
		CountDownLatch latch = new CountDownLatch(2);

		int maxSleep = DEFAULT_MAX_SLEEP; // ms
		Runnable insertWorker = new InsertWorker<T>(bq, operations, randomList, latch, maxSleep);
		Runnable removeWorker = new RemoveWorker<T>(bq, operations, latch, maxSleep);
		Thread t1 = new Thread(insertWorker);
		Thread t2 = new Thread(removeWorker);
		t1.start();
		t2.start();
		// t1.join();
		// t2.join();
		try {
			latch.await(operations * maxSleep, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Assert.assertEquals(0, latch.getCount());
	}

	@Test(expected = Test.None.class)
	public void testUsingExecutorServiceSimple() throws InterruptedException {
		int operations = DEFAULT_OPERATIONS;
		List<Integer> randomList = getRandomList(operations);
		final BlockingQueue<Integer> bq = new BlockingQueueSimple<>(10);
		testUsingExecutorServiceHelper(bq, randomList, operations);
	}

	@Test(expected = Test.None.class)
	public void testUsingExecutorServiceExtended() throws InterruptedException {
		int operations = DEFAULT_OPERATIONS;
		List<Integer> randomList = getRandomList(operations);
		BlockingQueue<Integer> bq = new BlockingQueueExtended<>(10);
		testUsingExecutorServiceHelper(bq, randomList, operations);
	}

	private <T> void testUsingExecutorServiceHelper(BlockingQueue<T> bq, List<T> randomList, int operations)
			throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(6);

		int maxSleep = DEFAULT_MAX_SLEEP; // ms
		Runnable insertWorker = new InsertWorker<T>(bq, operations, randomList, latch, maxSleep);
		Runnable removeWorker = new RemoveWorker<T>(bq, operations, latch, maxSleep);

		ExecutorService executorService = Executors.newFixedThreadPool(6);
		executorService.submit(removeWorker);
		executorService.submit(removeWorker);
		executorService.submit(removeWorker);
		executorService.submit(insertWorker);
		executorService.submit(insertWorker);
		executorService.submit(insertWorker);

		executorService.shutdown();

		latch.await(operations * maxSleep, TimeUnit.MILLISECONDS);
		Assert.assertEquals(0, latch.getCount());
	}

	private List<Integer> getRandomList(int size) {
		List<Integer> randomList = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			randomList.add(i + 1);
		}
		return randomList;
	}

	private static class RemoveWorker<T> implements Runnable {
		private final BlockingQueue<T> bq;
		private final int operations;
		private final CountDownLatch latch;
		private final int maxSleep;

		public RemoveWorker(BlockingQueue<T> bq, int operations, CountDownLatch latch, int maxSleep) {
			this.bq = bq;
			this.operations = operations;
			this.latch = latch;
			this.maxSleep = maxSleep;
		}

		@Override
		public void run() {
			Random r = new Random();
			for (int i = 0; i < operations; i++) {
				try {
					Thread.sleep(r.nextInt(maxSleep));
				} catch (InterruptedException e) {
					/*
					 * It is important to catch only interrupted exception here, because if there is
					 * any other exception, then we won't be doing latch.countDown to convey to the
					 * main thread that there is some failure in the tests possibly because of some
					 * illegal access
					 */
					e.printStackTrace();
				}
				T element;
				element = bq.remove();
				System.out.println("RemoveWorker got = " + element);

			}
			latch.countDown();
		}

	}

	private static class InsertWorker<T> implements Runnable {
		private final BlockingQueue<T> bq;
		private final List<T> list;
		private final int operations;
		private final CountDownLatch latch;
		private final int maxSleep;

		public InsertWorker(BlockingQueue<T> bq, int operations, List<T> list, CountDownLatch latch, int maxSleep) {
			this.bq = bq;
			this.list = list;
			this.operations = list.size();
			this.latch = latch;
			this.maxSleep = maxSleep;
		}

		@Override
		public void run() {
			Random r = new Random();
			for (int i = 0; i < operations; i++) {
				try {
					Thread.sleep(r.nextInt(maxSleep));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				bq.add(list.get(i));
				System.out.println("InsertWorker inserted = " + list.get(i));

			}
			latch.countDown();
		}
	}

}
