package jp.co.opst.java9.lib.function;

/**
 * 例外を考慮した関数です。
 *
 * @param <V> 処理する値
 * @param <R> 処理した結果
 * @param <T> 発生した例外
 */
@FunctionalInterface
public interface ValueProcessor<V, R, T extends Throwable> {

	/**
	 * プロセッサーを作成します。
	 * 
	 * @param <V> 処理する値
	 * @param <R> 処理した結果
	 * @param <T> 発生した例外
	 * @param processor プロセッサーの関数
	 * @return プロセッサー
	 */
	public static <V, R, T extends Throwable> ValueProcessor<V, R, T> of(ValueProcessor<V, R, T> processor) {
		return processor;
	}

	/**
	 * 処理を行います。
	 * 
	 * @param value 処理する値
	 * @return 処理した結果
	 * @throws T 処理中に例外が発生した場合
	 */
	public R process(V value) throws T;

	/**
	 * 処理する値をバインドします。
	 * 
	 * @param value 処理する値
	 * @return プロセッサー
	 */
	public default Processor<R, T> with(V value) {
		return () -> process(value);
	}
}
