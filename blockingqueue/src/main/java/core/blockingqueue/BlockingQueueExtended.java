package core.blockingqueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/*
 * A similar and simpler implementation can be found here
 * https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/locks/Condition.html
 */
public class BlockingQueueExtended<T> implements BlockingQueue<T> {
	private final int capacity;
	private final List<T> values;
	private final long maxWaitTime; // default 5000ms
	private final ReentrantLock lock;
	private final Condition notEmpty;
	private final Condition notFull;

	private static final long DEFAULT_WAIT_TIME = 5000; // ms

	public BlockingQueueExtended(int capacity, long maxWaitTime) {
		this.capacity = capacity;
		values = new ArrayList<T>(capacity);
		this.maxWaitTime = maxWaitTime;
		lock = new ReentrantLock();
		notEmpty = lock.newCondition();
		notFull = lock.newCondition();
	}

	public BlockingQueueExtended(int capacity) {
		this(capacity, DEFAULT_WAIT_TIME);
	}

	public boolean add(T element) {
		long currentWait = maxWaitTime;
		lock.lock();
		try {
			while (values.size() == capacity) {
				currentWait = notFull.awaitNanos(currentWait);
				/*
				 * You already waited long enough
				 */
				if (currentWait < 0) {
					return false;
				}
			}
			values.add(element);
			notEmpty.signal();

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		return true;
	}

	/*
	 * returns null if it waited enough and exceeded the timeout
	 */
	public T remove() {
		long currentWait = maxWaitTime;
		T retValue = null;
		lock.lock();
		try {
			while (values.size() == 0) {
				currentWait = notEmpty.awaitNanos(currentWait);
				if (currentWait < 0) {
					/*
					 * You have waited long enough
					 */
					return null;
				}
			}
			retValue = values.get(0);
			values.remove(0);
			notFull.signal();

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		return retValue;
	}

}
