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
 * <p>
 * なお、acquire/releaseの使い方は、まだ分かりません……。
 * </p>
 * 
 * @see VarHandle
 */
class VarHandleTest {

	/** スタティックフィールドのハンドラ。 */
	private static VarHandle staticFieldHandle;

	/** 標準的なフィールドのハンドラ。 */
	private static VarHandle standardFirldHandle;

	/** 配列フィールドのハンドラ。 */
	private static VarHandle arrayFieldHandle;

	/** 配列フィールドの要素のハンドラ。 */
	private static VarHandle arrayFieldElementHandle;

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
		standardFirldHandle = lookup.findVarHandle(VarHandleTest.class, "standardFirld", int.class);
		arrayFieldHandle = lookup.findVarHandle(VarHandleTest.class, "arrayField", int[].class);
		arrayFieldElementHandle = MethodHandles.arrayElementVarHandle(int[].class);
		volatileFieldHandle = lookup.findVarHandle(VarHandleTest.class, "volatileField", int.class);
	}

	/** スタティックフィールド。 */
	private static int staticField;

	/** 標準的なフィールド。 */
	private int standardFirld;

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
		standardFirld = 0;
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
		standardFirldHandle.set(this, 1);
		assertEquals(1, standardFirldHandle.get(this));
		assertEquals(1, standardFirld);
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
		arrayFieldElementHandle.set(arrayField, 1, 1);
		assertEquals(0, arrayFieldElementHandle.get(arrayField, 0));
		assertEquals(1, arrayFieldElementHandle.get(arrayField, 1));
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
