package jp.co.opst.java9.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.ToIntFunction;

import org.junit.jupiter.api.Test;

/**
 * 匿名クラスに関するテストです。
 */
public class AnonymousClassTest {

	/**
	 * 匿名クラスの生成にダイアモンド演算子が使えることをテストします。
	 */
	@Test
	void testDiamondOperator() {
		// あえて匿名クラスで記述しています。
		ToIntFunction<String> parser = new ToIntFunction<>() {
			@Override
			public int applyAsInt(String arg) {
				return Integer.parseInt(arg);
			}
		};

		assertEquals(10, parser.applyAsInt("10"));
	}
}
