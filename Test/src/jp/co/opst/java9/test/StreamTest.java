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
		Map<String, Integer> map = Map.of("foo", 1, "bar", 2);

		List<Integer> actual = Stream.of("foo", "bar", "baz")
				.flatMap(e -> Stream.ofNullable(map.get(e)))
				.collect(Collectors.toList());

		// 以下のコードでも実現可能だが、Stream::filterでnullガードをしないのがトレンドらしい。
//		List<Integer> actual = Stream.of("foo", "bar", "baz")
//				.map(map::get)
//				.filter(Objects::nonNull)
//				.collect(Collectors.toList());

		assertEquals(List.of(1, 2), actual);
	}

	/**
	 * このメソッドは、指定された条件を満たしている間、要素を生成し続けます。
	 * 
	 * @see Stream#iterate(Object, java.util.function.Predicate, java.util.function.UnaryOperator)
	 */
	@Test
	void testIterate() {
		List<String> actual = Stream.iterate("1234", e -> !e.isEmpty(), e -> e.substring(1))
				.collect(Collectors.toList());

		assertEquals(List.of("1234", "234", "34", "4"), actual);
	}
}
