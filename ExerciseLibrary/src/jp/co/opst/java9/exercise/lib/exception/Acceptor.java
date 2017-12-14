package jp.co.opst.java9.exercise.lib.exception;

/**
 * 引数を受け、結果を返さない関数です。
 * 
 * @param <V> 引数
 * @param <E> 処理中に発生しうる例外
 */
@FunctionalInterface
public interface Acceptor<V, E extends Exception> {

	/**
	 * 関数をアクセプターにキャストします。
	 * 
	 * @param <V> 引数
	 * @param <E> 処理中に発生しうる例外
	 * @param action 関数
	 * @return アクセプター
	 */
	public static <V, E extends Exception> Acceptor<V, E> of(Acceptor<V, E> action) {
		return action;
	}

	/**
	 * 処理を行います。
	 * 
	 * @param value 引数
	 * @throws E 処理中に例外が発生した場合
	 */
	public void accept(V value) throws E;

	/**
	 * ジェネレーターに変換します。
	 * 
	 * @param <R> 結果
	 * @param value 引数
	 * @return ジェネレーター
	 */
	public default <R> Generator<R, E> normalize(V value) {
		return () -> {
			accept(value);
			return null;
		};
	}
}
