package core.blockingqueue;

public interface BlockingQueue<T> {
	boolean add(T ele);

	T remove();
}
