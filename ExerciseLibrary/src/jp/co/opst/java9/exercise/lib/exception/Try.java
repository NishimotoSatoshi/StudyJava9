package jp.co.opst.java9.exercise.lib.exception;

import java.util.function.Function;

/**
 * 例外処理のユーティリティです。
 */
public final class Try {

	/**
	 * 値を返却する関数。
	 * 
	 * @param <R> 返却値
	 */
	public interface WithReturn<R> {

		public R execute() throws Throwable;
	}

	/**
	 * 値を返却しない関数。
	 */
	public interface WithoutReturn {

		public void execute() throws Throwable;
	}

	/**
	 * チェック例外が発生したときは、非チェック例外でラッピングします。
	 * 
	 * @param target 対象となる処理
	 * @param wrapper チェック例外から非チェック例外を作成する関数
	 * @return 処理結果
	 */
	public static <R, T extends RuntimeException> R uncheck(WithReturn<R> target, Function<Throwable, T> wrapper) {
		try {
			return target.execute();
		} catch (RuntimeException r) {
			throw r;
		} catch (Throwable t) {
			throw wrapper.apply(t);
		}
	}

	/**
	 * チェック例外が発生したときは、非チェック例外でラッピングします。
	 * 
	 * @param target 対象となる処理
	 * @param wrapper チェック例外から非チェック例外を作成する関数
	 */
	public static <T extends RuntimeException> void uncheck(WithoutReturn target, Function<Throwable, T> wrapper) {
		try {
			target.execute();
		} catch (RuntimeException r) {
			throw r;
		} catch (Throwable t) {
			throw wrapper.apply(t);
		}
	}

	/**
	 * 例外を無視します。
	 * 
	 * @param target 対象となる処理
	 * @return 処理結果（例外が発生した場合はnull）
	 */
	public static <R> R ignore(WithReturn<R> target) {
		try {
			return target.execute();
		} catch (Throwable t) {
			return null;
		}
	}

	/**
	 * 例外を無視します。
	 * 
	 * @param target 対象となる処理
	 */
	public static void ignore(WithoutReturn target) {
		try {
			target.execute();
		} catch (Throwable t) {
		}
	}

	private Try() {
	}
}
