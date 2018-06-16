package core.blockingqueue;

import java.util.ArrayList;
import java.util.List;

public class BlockingQueueSimple<T> implements BlockingQueue<T> {
	private final List<T> values;
	private final int capacity;

	/*
	 * This is the max time for which you will wait for any insert or remove
	 * operation If we can't do it within maxWaitTime then you will ignore that
	 * operation and simply return
	 */
	// private int maxWaitTime; // in ms - default 5000 ms

	public BlockingQueueSimple(int capacity, int maxWaitTime) {
		this.capacity = capacity;
		if (capacity < 0) {
			throw new RuntimeException("The capacity should be positive");
		}
		values = new ArrayList<T>(capacity);
		// this.maxWaitTime = maxWaitTime;
	}

	public BlockingQueueSimple(int capacity) {
		this(capacity, 5000);
	}

	@Override
	public synchronized boolean add(T element) {
		/*
		 * At the time of inserting you need to take care that if the size of list is
		 * equal to specified capacity, you should block and wait for any consumer to
		 * first consume an element and only then you should insert
		 */
		while (values.size() == capacity) {
			// wait(maxWaitTime);
			/*
			 * There is no way for you to determine whether the wait was over because of
			 * notify or because of timeout.
			 * 
			 * If you are thinking the following strategy, then you are mistaken just after
			 * wait returns, check if values.size() == capacity and if this is true, then
			 * definitely the wait was over because of timeout (This is wrong) Reason: It
			 * might be the case that, some other thread (which was also notified) again
			 * inserted an element, and made the list full (this is the reason we loop and
			 * put wait inside the loop)
			 */
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		values.add(element);
		if (values.size() > capacity) {
			// This should never be executed
			throw new RuntimeException("The size of list grew up beyond the capacity");
		}
		notifyAll();
		return true;
	}

	@Override
	public synchronized T remove() {
		/*
		 * At the time of removal you need to take care that if the size of the list is
		 * zero then you should block and wait for a producer to first produce
		 */
		while (values.size() == 0) {
			// wait(maxWaitTime);
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		T retValue = values.get(0);
		values.remove(0); // automatically throws exception if there's no element in the list

		notifyAll();
		return retValue;
	}
}
