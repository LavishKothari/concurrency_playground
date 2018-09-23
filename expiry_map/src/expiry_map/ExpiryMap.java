package expiry_map;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
 * dev-notes:
 * 
 * What should be the behavior of expiry-map when keys are reinserted 
 * and the map already contains this key?
 * Should the timer of reinserted key be refreshed, 
 * or should we stick to the timer when the key was inserted for the 
 * very first time.
 * 
 * The current implementation follows the latter approach.
 */
public class ExpiryMap<K, V> implements Map<K, V> {

	private final Map<K, V> map;

	private final ScheduledExecutorService scheduledThreadPool;
	private final long timeout;
	private final TimeUnit timeUnit;

	public ExpiryMap(long timeout, TimeUnit timeUnit) {
		this.map = new ConcurrentHashMap<>();
		this.timeout = timeout;
		scheduledThreadPool = Executors.newScheduledThreadPool(4);
		this.timeUnit = timeUnit;
	}

	private static class Cleaner<K, V> implements Runnable {

		final private Map<K, V> map;
		final private Set<? extends K> keySet;

		private Cleaner(Map<K, V> map, Set<? extends K> set) {
			this.map = map;
			this.keySet = set;
		}

		@Override
		public void run() {
			for (K key : keySet) {
				map.remove(key);
			}
		}

	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return map.entrySet();
	}

	@Override
	public V get(Object key) {
		return map.get(key);
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public V put(K key, V value) {
		Set<K> set = new HashSet<>();
		set.add(key);
		scheduledThreadPool.schedule(new Cleaner<K, V>(this, set), timeout, timeUnit);
		return map.put(key, value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> argMap) {
		scheduledThreadPool.schedule(new Cleaner<K, V>(this, argMap.keySet()), timeout, timeUnit);
		map.putAll(argMap);
	}

	@Override
	public V remove(Object key) {
		return map.remove(key);
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Collection<V> values() {
		return map.values();
	}

	@Override
	public String toString() {
		return map.toString();
	}
}
