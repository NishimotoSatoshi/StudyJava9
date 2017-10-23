package jp.co.opst.java9.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

/**
 * Streamの新メソッドを試します。
 * 
 * @see Stream
 */
class StreamTest {

	/**
	 * このメソッドは、指定された条件を満たさなくなった時点で、その要素以降を捨てます。
	 * 
	 * @see Stream#takeWhile(java.util.function.Predicate)
	 */
	@Test
	void testTakeWhile() {
		List<Integer> actual = Stream.of(1, 2, 3, 1, 2, 3)
				.takeWhile(i -> i < 3)
				.collect(Collectors.toList());

		assertEquals(List.of(1, 2), actual);
	}

	/**
	 * このメソッドは、先頭から、指定された条件が満たされ続けているまでの要素を捨てます。
	 * 
	 * @see Stream#dropWhile(java.util.function.Predicate)
	 */
	@Test
	void testDropWhile() {
		List<Integer> actual = Stream.of(1, 2, 3, 1, 2, 3)
				.dropWhile(i -> i < 3)
				.collect(Collectors.toList());

		assertEquals(List.of(3, 1, 2, 3), actual);
	}

	/**
	 * このメソッドは、1要素のみのStreamを作りますが、nullの場合は空のStreamを返します。
	 * 
	 * @see Stream#ofNullable(Object)
	 */
	@Test
	void testOfNullable() {
		
		Map<String, List<Integer>> map = Map.of(
				"foo", List.of(1, 2, 3),
				"baz", List.of(4, 5, 6)
		);

		List<Integer> actual = Stream.of("foo", "bar", "baz")
				.flatMap(e -> Stream.ofNullable(map.get(e)))
				.flatMap(List::stream)
				.collect(Collectors.toList());

		// うーん、こう書いた方がすっきりみえる……。
//		List<Integer> actual = Stream.of("foo", "bar", "baz")
//				.map(map::get)
//				.filter(Objects::nonNull)
//				.flatMap(List::stream)
//				.collect(Collectors.toList());

		assertEquals(List.of(1, 2, 3, 4, 5, 6), actual);
	}

	/**
	 * このメソッドは、指定された条件を満たしている間、要素を生成し続けます。
	 * 
	 * @see Stream#iterate(Object, java.util.function.Predicate, java.util.function.UnaryOperator)
	 */
	@Test
	void testIterate() {
		List<String> actual = Stream.iterate("1234567", e -> e.length() >= 2, e -> e.substring(2))
				.collect(Collectors.toList());

		// これも、takeWhileと組み合わせるだけでいいような……。
//		List<String> actual = Stream.iterate("1234567", e -> e.substring(2))
//				.takeWhile(e -> e.length() >= 2)
//				.collect(Collectors.toList());

		assertEquals(List.of("1234567", "34567", "567"), actual);
	}
}
