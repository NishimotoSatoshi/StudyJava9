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
	 * 処理を行います。
	 * 
	 * @throws Throwable 処理中に例外が発生した場合
	 */
	public void invoke() throws E;

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
