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

	@Test(expected = Test.None.class)
	public void testConcurrent() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(2);
		final BlockingQueue<Integer> bq = new BlockingQueue<>(10);
		List<Integer> randomList = new ArrayList<>();
		int operations = 50;
		for (int i = 0; i < operations; i++) {
			randomList.add(i + 1);
		}
		int maxSleep = 500; // ms
		Runnable insertWorker = new InsertWorker<Integer>(bq, operations, randomList, latch, maxSleep);
		Runnable removeWorker = new RemoveWorker<Integer>(bq, operations, latch, maxSleep);
		Thread t1 = new Thread(insertWorker);
		Thread t2 = new Thread(removeWorker);
		t1.start();
		t2.start();
		// t1.join();
		// t2.join();
		latch.await(operations * maxSleep, TimeUnit.MILLISECONDS);
		Assert.assertEquals(0, latch.getCount());
	}

	@Test(expected = Test.None.class)
	public void testUsingExecutorService() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(6);
		final BlockingQueue<Integer> bq = new BlockingQueue<>(10);
		List<Integer> randomList = new ArrayList<>();
		int operations = 50;
		for (int i = 0; i < operations; i++) {
			randomList.add(i + 1);
		}
		int maxSleep = 500; // ms
		Runnable insertWorker = new InsertWorker<Integer>(bq, operations, randomList, latch, maxSleep);
		Runnable removeWorker = new RemoveWorker<Integer>(bq, operations, latch, maxSleep);

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
				try {
					element = bq.remove();
					System.out.println("RemoveWorker got = " + element);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

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
				try {
					bq.add(list.get(i));
					System.out.println("InsertWorker inserted = " + list.get(i));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			latch.countDown();
		}
	}

}
