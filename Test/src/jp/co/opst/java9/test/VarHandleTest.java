package jp.co.opst.java9.test;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * VarHandleを試します。
 * 
 * @see VarHandle
 */
class VarHandleTest {

	/** スタティックフィールドのハンドラ。 */
	private static VarHandle staticFieldHandle;

	/** 標準的なフィールドのハンドラ。 */
	private static VarHandle standardFieldHandle;

	/** 配列フィールドのハンドラ。 */
	private static VarHandle arrayFieldHandle;

	/** 配列フィールドの要素のハンドラ。 */
	private static VarHandle arrayElementHandle;

	/** ボラタイルフィールドのハンドラ。 */
	private static VarHandle volatileFieldHandle;

	/**
	 * ロード時の準備処理。
	 * 
	 * @throws Throwable 準備処理に失敗した場合
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Throwable {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		staticFieldHandle = lookup.findStaticVarHandle(VarHandleTest.class, "staticField", int.class);
		standardFieldHandle = lookup.findVarHandle(VarHandleTest.class, "standardField", int.class);
		arrayFieldHandle = lookup.findVarHandle(VarHandleTest.class, "arrayField", int[].class);
		arrayElementHandle = MethodHandles.arrayElementVarHandle(int[].class);
		volatileFieldHandle = lookup.findVarHandle(VarHandleTest.class, "volatileField", int.class);
	}

	/** スタティックフィールド。 */
	private static int staticField;

	/** 標準的なフィールド。 */
	private int standardField;

	/** 配列フィールド。 */
	private int[] arrayField;

	/** ボラタイルフィールド。 */
	private volatile int volatileField;

	/**
	 * テスト前の準備処理。
	 */
	@BeforeEach
	void setUp() {
		staticField = 0;
		standardField = 0;
		arrayField = null;
		volatileField = 0;
	}

	/**
	 * スタティックフィールドへの単純なアクセス。
	 * 
	 * @see VarHandle#set(Object...)
	 * @see VarHandle#get(Object...)
	 */
	@Test
	void testAccessToStaticFieldSimply() {
		staticFieldHandle.set(100);
		assertEquals(100, staticFieldHandle.get());
		assertEquals(100, staticField);
	}

	/**
	 * 標準的なフィールドへの単純なアクセス。
	 * 
	 * @see VarHandle#set(Object...)
	 * @see VarHandle#get(Object...)
	 */
	@Test
	void testAccessToStandardFieldSimply() {
		standardFieldHandle.set(this, 1);
		assertEquals(1, standardFieldHandle.get(this));
		assertEquals(1, standardField);
	}

	/**
	 * 配列フィールドへの単純なアクセス。
	 * 
	 * @see VarHandle#set(Object...)
	 * @see VarHandle#get(Object...)
	 */
	@Test
	void testAccessToArrayFieldSimply() {
		arrayFieldHandle.set(this, new int[] {0, 1});
		assertArrayEquals(new int[] {0, 1}, (int[]) arrayFieldHandle.get(this));
		assertArrayEquals(new int[] {0, 1}, arrayField);
	}

	/**
	 * 配列フィールドの要素への単純なアクセス。
	 * 
	 * @see VarHandle#set(Object...)
	 * @see VarHandle#get(Object...)
	 */
	@Test
	void testAccessToArrayFieldElementSimply() {
		arrayField = new int[] {0, 0};
		arrayElementHandle.set(arrayField, 1, 1);
		assertEquals(0, arrayElementHandle.get(arrayField, 0));
		assertEquals(1, arrayElementHandle.get(arrayField, 1));
		assertArrayEquals(new int[] {0, 1}, arrayField);
	}

	/**
	 * フィールドが期待する値の場合のみ、フィールドの値を変更する。
	 * 
	 * <p>
	 * なぜか、ちゃんと返却値を受け取らないと、
	 * "NoSuchMethodError: VarHandle.compareAndSet(VarHandleTest,int,int)void"
	 * という例外が発生してしまう。
	 * </p>
	 * 
	 * @see VarHandle#compareAndSet(Object...)
	 */
	@Test
	void testCompareAndSet() {
		assertTrue(volatileFieldHandle.compareAndSet(this, 0, 1));
		assertFalse(volatileFieldHandle.compareAndSet(this, 0, 2));
		assertEquals(1, volatileField);
	}

	/**
	 * フィールドが期待する値の場合のみ、フィールドの値を変更して、変更前の値を取得する。
	 * 
	 * @see VarHandle#compareAndExchange(Object...)
	 */
	@Test
	void testCompareAndExchange() {
		assertEquals(0, volatileFieldHandle.compareAndExchange(this, 0, 1));
		assertEquals(1, volatileFieldHandle.compareAndExchange(this, 0, 2));
		assertEquals(1, volatileField);
	}
}
