package jp.co.opst.java9.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Optionalの新メソッドを試します。
 * 
 * @see Optional
 */
class OptionalTest {

	/**
	 * このメソッドは、emptyの場合に実行する処理を記述できます。
	 * 
	 * @see Optional#ifPresentOrElse(java.util.function.Consumer, Runnable)
	 */
	@Nested
	class TestIfPresentOrElse {
		/** 結果。 */
		private final AtomicReference<String> actual = new AtomicReference<>();

		/** 呼び出し先。 */
		private final Consumer<String> callee = actual::set;

		/** テスト対象。 */
		private final Consumer<String> tested = arg ->
			Optional.ofNullable(arg)
				.ifPresentOrElse(
						callee::accept,
						() -> callee.accept(""));

		/**
		 * 前準備。
		 */
		@BeforeEach
		void setUp() {
			actual.set(null);
		}

		/**
		 * 引数が "foo" の場合。
		 */
		@Test
		void whenFoo() {
			tested.accept("foo");
			assertEquals("foo", actual.get());
		}

		/**
		 * 引数が null の場合。
		 */
		@Test
		void whenNull() {
			tested.accept(null);
			assertEquals("", actual.get());
		}
	}

	/**
	 * このメソッドは、emptyの場合に別のOptionalを代替できるようになります。
	 * 
	 * @see Optional#or(java.util.function.Supplier)
	 */
	@Nested
	class TestOr {
		/** キーを数値に変換するマップ。 */
		private final Map<String, String> map = Map.of("foo", "A");

		/** テスト対象。 */
		private final UnaryOperator<String> tested = key ->
			Optional.ofNullable(key)
				.or(() -> Optional.of("foo"))
				.map(map::get)
				.orElse("");

		/**
		 * 引数が "foo" の場合。
		 */
		@Test
		void whenFoo() {
			assertEquals("A", tested.apply("foo"));
		}

		/**
		 * 引数が "bar" の場合。
		 */
		@Test
		void whenBar() {
			assertEquals("", tested.apply("bar"));
		}

		/**
		 * 引数が null の場合。
		 */
		@Test
		void whenNull() {
			assertEquals("A", tested.apply(null));
		}
	}

	/**
	 * このメソッドは、1要素のみのStreamを作りますが、emptyの場合は空のStreamを返します。
	 * 
	 * @see Optional#stream()
	 */
	@Test
	void testStream() {
		List<String> actual = Stream.of("foo", null, "bar")
			.map(Optional::ofNullable)
			.flatMap(Optional::stream)
			.collect(Collectors.toList());

		assertEquals(List.of("foo", "bar"), actual);
	}
}
