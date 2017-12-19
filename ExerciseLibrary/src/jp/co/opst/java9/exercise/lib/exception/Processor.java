package jp.co.opst.java9.exercise.lib.exception;

import java.util.function.Function;

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
	 * ファンクションからプロセッサーを作成します。
	 * 
	 * @param function ファンクション
	 * @return プロセッサー
	 */
	public static <V, R> Processor<V, R, RuntimeException> from(Function<V, R> function) {
		return function::apply;
	}

	/**
	 * 引数をそのまま返すプロセッサーを作成します。
	 * 
	 * @param <V> 引数
	 * @param <E> 処理中に発生しうる例外（実際には発生しない）
	 * @return 引数をそのまま返すプロセッサー
	 */
	public static <V, E extends Exception> Processor<V, V, E> pipe() {
		return value -> value;
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
	 * 処理を行います。
	 * 
	 * <p>
	 * チェック例外が発生した時は、IllegalStateExceptionを送出します。
	 * 非チェック例外が発生した時は、そのまま送出されます。
	 * </p>
	 * 
	 * @param value 引数
	 * @return 結果
	 */
	public default R uncheck(V value) {
		return normalize(value).uncheck();
	}

	/**
	 * 処理を行います。
	 * 
	 * <p>
	 * 例外が発生した時は、結果がnullになります。
	 * </p>
	 * 
	 * @param value 引数
	 * @return 結果
	 */
	public default R ignore(V value) {
		return normalize(value).ignore();
	}

	/**
	 * 結果をさらに処理するプロセッサーと連結します。
	 * 
	 * @param <RR> 連結されたプロセッサーの結果
	 * @param after 結果をさらに処理するプロセッサー
	 * @return 連結されたプロセッサー
	 */
	public default <RR> Processor<V, RR, Exception> andThen(Processor<? super R, RR, Exception> after) {
		return value -> after.process(process(value));
	}

	/**
	 * ジェネレーターに変換します。
	 * 
	 * @return ジェネレーター
	 */
	public default Generator<R, E> normalize(V value) {
		return () -> process(value);
	}
}
