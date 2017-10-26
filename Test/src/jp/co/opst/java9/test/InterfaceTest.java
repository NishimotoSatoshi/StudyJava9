package jp.co.opst.java9.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

/**
 * インターフェースに関するテストです。
 */
public class InterfaceTest {

	/**
	 * オブジェクトから比較可能な値を取り出すためのインターフェースです。
	 * 
	 * @param <T> 取り出し先のオブジェクト
	 * @param <V> 取り出した値
	 */
	@FunctionalInterface
	public interface ComparableExtractor<T, V extends Comparable<V>> extends Comparator<T> {

		/**
		 * オブジェクトから比較可能な値を取り出します。
		 * 
		 * @param target 取り出し先のオブジェクト
		 * @return 取り出した値
		 */
		public V extract(T target);

		/**
		 * ラムダ式またはメソッド参照を、このインターフェースにキャストします。
		 * 
		 * @param <T> 取り出し先のオブジェクト
		 * @param <V> 取り出した値
		 * @param extractor オブジェクトから比較可能な値を取り出す為の、ラムダ式またはメソッド参照
		 * @return 引数をこのインターフェースにキャストしたもの
		 */
		public static <T, V extends Comparable<V>> ComparableExtractor<T, V> of(ComparableExtractor<T, V> extractor) {
			return extractor;
		}

		/**
		 * 2つのオブジェクトから比較可能な値を取り出して、それらを比較します。
		 * 
		 * @param t1 1つめの比較オブジェクト
		 * @param t2 2つめの比較オブジェクト
		 * @return 比較結果
		 */
		@Override
		public default int compare(T t1, T t2) {
			return extract(t1).compareTo(extract(t2));
		}

		/**
		 * オブジェクトから比較可能な値を取り出して、それらを使用してソートを行います。
		 * 
		 * @param <E> 対象オブジェクト
		 * @param targets 対象オブジェクトの配列
		 * @return ソート結果
		 */
		public default <E extends T> List<E> sort(E[] targets) {
			return sort(Stream.of(targets));
		}

		/**
		 * オブジェクトから比較可能な値を取り出して、それらを使用してソートを行います。
		 * 
		 * @param <E> 対象オブジェクト
		 * @param targets 対象オブジェクトのリスト
		 * @return ソート結果
		 */
		public default <E extends T> List<E> sort(Collection<E> targets) {
			return sort(targets.stream());
		}

		/**
		 * オブジェクトから比較可能な値を取り出して、それらを使用してソートを行います。
		 * 
		 * @param <E> 対象オブジェクト
		 * @param stream 対象オブジェクトのストリーム
		 * @return ソート結果
		 */
		private <E extends T> List<E> sort(Stream<E> stream) {
			return stream.sorted(this).collect(Collectors.toList());
		}
	}

	/**
	 * テストに用いる列挙型です。
	 */
	public enum Type {
		FOO, BAR, BAZ
	}

	/**
	 * インターフェースにプライベートメソッドが使えることをテストします。
	 */
	@Test
	void testPrivateMethod() {
		// 列挙型の各値を、名前でソートします。
		List<Type> sortedByName = ComparableExtractor.of(Type::name).sort(Type.values());
		assertEquals(List.of(Type.BAR, Type.BAZ, Type.FOO), sortedByName);
	}
}
