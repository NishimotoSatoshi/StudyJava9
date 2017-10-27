package jp.co.opst.java9.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

/**
 * Stack-Walker APIを試します。
 */
public class StackWalkerTest {

	/**
	 * walkを試します。
	 */
	@Test
	void testWalk() {
		List<String> expected = List.of(
			"jp.co.opst.java9.test.StackWalkerTest::getStackFrames",
			"java.util.Optional::map",
			"jp.co.opst.java9.test.StackWalkerTest::testWalk"
		);

		List<String> actual = Optional.of(3).map(this::getStackFrames).get();
		assertEquals(expected, actual);
	}

	/**
	 * testWalkから呼ばれます。
	 * 
	 * @param depth StackFrameの取得数
	 * @return 取得したStackFrameの「クラス::メソッド」のリスト
	 */
	private List<String> getStackFrames(int depth) {
		return StackWalker.getInstance().walk(stream -> stream
			.limit(depth)
			.map(frame -> frame.getClassName() + "::" + frame.getMethodName())
			.collect(Collectors.toList())
		);
	}
}
