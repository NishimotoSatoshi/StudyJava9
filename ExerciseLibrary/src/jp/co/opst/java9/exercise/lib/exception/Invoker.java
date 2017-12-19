package jp.co.opst.java9.exercise.lib.exception;

/**
 * 結果を返さない関数です。
 * 
 * @param <E> 処理中に発しうる例外
 */
@FunctionalInterface
public interface Invoker<E extends Exception> {

	/**
	 * 関数をインボーカーにキャストします。
	 * 
	 * @param <E> 処理中に発しうる例外
	 * @param action 関数
	 * @return インボーカー
	 */
	public static <E extends Exception> Invoker<E> of(Invoker<E> action) {
		return action;
	}

	/**
	 * ランナブルからインボーカーを作成します。
	 * @param runnable ランナブル
	 * @return インボーカー
	 */
	public static Invoker<RuntimeException> from(Runnable runnable) {
		return runnable::run;
	}

	/**
	 * 何もしないインボーカーを作成します。
	 * 
	 * @param <E> 処理中に発しうる例外（実際には発生しない）
	 * @return 何もしないインボーカー
	 */
	public static <E extends Exception> Invoker<E> nop() {
		return () -> {};
	}

	/**
	 * 処理を行います。
	 * 
	 * @throws Throwable 処理中に例外が発生した場合
	 */
	public void invoke() throws E;

	/**
	 * 処理を行います。
	 * 
	 * <p>
	 * チェック例外が発生した時は、IllegalStateExceptionを送出します。
	 * 非チェック例外が発生した時は、そのまま送出されます。
	 * </p>
	 */
	public default void uncheck() {
		normalize().uncheck();
	}

	/**
	 * 処理を行います。
	 * 
	 * <p>
	 * 例外が発生した時は、無視します。
	 * </p>
	 */
	public default void ignore() {
		normalize().ignore();
	}

	/**
	 * 他のインボーカーとまとめます。
	 * 
	 * @param after 他のインボーカー
	 * @return まとめられたインボーカー
	 */
	public default Invoker<E> andThen(Invoker<? extends E> after) {
		return () -> {
			invoke();
			after.invoke();
		};
	}

	/**
	 * ジェネレーターに変換します。
	 * 
	 * @param <R> 結果
	 * @return ジェネレーター
	 */
	public default <R> Generator<R, E> normalize() {
		return () -> {
			invoke();
			return null;
		};
	}
}
