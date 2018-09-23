package expiry_map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

class ExpiryMapTest {

	@Test
	void test() throws InterruptedException {

		ExpiryMap<String, String> map = new ExpiryMap<>(2, TimeUnit.SECONDS);
		map.put("Lavish", "Mumbai");
		map.put("Rakshit", "Udaipur");
		map.put("Manoj", "Udaipur");
		Thread.sleep(1000);
		map.put("Madan", "Udaipur");
		Thread.sleep(1500);
		assertEquals(1, map.size());
		Thread.sleep(2000);
		assertTrue(map.isEmpty());
		
	}

}
