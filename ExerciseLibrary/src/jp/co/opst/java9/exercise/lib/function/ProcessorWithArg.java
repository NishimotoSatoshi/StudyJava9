package jp.co.opst.java9.exercise.lib.function;

/**
 * チェック例外を考慮した関数です。
 *
 * @param <A> 処理する値
 * @param <R> 処理した結果
 * @param <E> 発生したチェック例外
 */
@FunctionalInterface
public interface ProcessorWithArg<A, R, E extends Exception> {

	/**
	 * プロセッサーを作成します。
	 * 
	 * @param <A> 処理する値
	 * @param <R> 処理した結果
	 * @param <E> 発生したチェック例外
	 * @param processor プロセッサーの関数
	 * @return プロセッサー
	 */
	public static <A, R, E extends Exception> ProcessorWithArg<A, R, E> of(ProcessorWithArg<A, R, E> processor) {
		return processor;
	}

	/**
	 * 処理を行います。
	 * 
	 * @param arg 処理する値
	 * @return 処理した結果
	 * @throws E 処理中にチェック例外が発生した場合
	 */
	public R process(A arg) throws E;

	/**
	 * 処理する値をバインドします。
	 * 
	 * @param arg 処理する値
	 * @return プロセッサー
	 */
	public default Processor<R, E> with(A arg) {
		return () -> process(arg);
	}
}
