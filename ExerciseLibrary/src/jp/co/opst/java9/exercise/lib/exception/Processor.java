package jp.co.opst.java9.exercise.lib.exception;

/**
 * 引数を受け、結果を返す関数です。
 * 
 * @param <V> 引数
 * @param <R> 結果
 * @param <E> 処理中に発生しうる例外
 */
@FunctionalInterface
public interface Processor<V, R, E extends Exception> {

	/**
	 * 関数をプロセッサーにキャストします。
	 * 
	 * @param <V> 引数
	 * @param <R> 結果
	 * @param <E> 処理中に発生しうる例外
	 * @param action 関数
	 * @return プロセッサー
	 */
	public static <V, R, E extends Exception> Processor<V, R, E> of(Processor<V, R, E> action) {
		return action;
	}

	/**
	 * 処理を行います。
	 * 
	 * @param value 引数
	 * @return 結果
	 * @throws E 処理中に例外が発生した場合
	 */
	public R process(V value) throws E;

	/**
	 * ジェネレーターに変換します。
	 * 
	 * @return ジェネレーター
	 */
	public default Generator<R, E> normalize(V value) {
		return () -> process(value);
	}
}
