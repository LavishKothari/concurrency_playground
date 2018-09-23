package expiry_map;

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

		Thread.sleep(3000);
		assertTrue(map.isEmpty());
	}

}
