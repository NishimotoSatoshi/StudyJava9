package jp.co.opst.java9.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Collections Frameworkの新メソッドを試します。
 * 
 * @see List
 * @see Set
 * @see Map
 */
class CollectionTest {

	/**
	 * このメソッドは、不変Listを作成します。
	 * 
	 * @see List#of(Object...)
	 */
	@Test
	void testListOf() {
		List<String> actual = List.of("foo", "bar", "baz");
		assertEquals(3, actual.size());
		assertEquals("foo", actual.get(0));
		assertEquals("bar", actual.get(1));
		assertEquals("baz", actual.get(2));
		assertThrows(UnsupportedOperationException.class, () -> actual.clear());
	}

	/**
	 * このメソッドは、不変Setを作成します。
	 * 
	 * @see Set#of(Object...)
	 */
	@Test
	void testSetOf() {
		Set<String> actual = Set.of("foo", "bar", "baz");
		assertEquals(3, actual.size());
		assertTrue(actual.contains("foo"));
		assertTrue(actual.contains("bar"));
		assertTrue(actual.contains("baz"));
		assertThrows(UnsupportedOperationException.class, () -> actual.clear());
	}

	/**
	 * このメソッドは、不変Mapを作成します。
	 * 
	 * <p>
	 * Mapは、ListやSetと違って、10エントリまでしか指定できないようです。
	 * </p>
	 * 
	 * @see Map#of(Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object)
	 */
	@Test
	void testMapOf() {
		Map<Integer, String> actual = Map.of(1, "foo", 2, "bar", 3, "baz");
		assertEquals(3, actual.size());
		assertEquals("foo", actual.get(1));
		assertEquals("bar", actual.get(2));
		assertEquals("baz", actual.get(3));
		assertThrows(UnsupportedOperationException.class, () -> actual.clear());
	}
}
