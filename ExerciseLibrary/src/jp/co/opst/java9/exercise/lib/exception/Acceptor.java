package jp.co.opst.java9.exercise.lib.exception;

import java.util.function.Consumer;

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
	 * コンシューマーからアクセプターを作成します。
	 * 
	 * @param <V> 引数
	 * @param consumer コンシューマー
	 * @return アクセプター
	 */
	public static <V> Acceptor<V, RuntimeException> from(Consumer<V> consumer) {
		return consumer::accept;
	}

	/**
	 * 何もしないアクセプターを取得します。
	 * 
	 * @param <V> 引数
	 * @param <E> 処理中に発しうる例外（実際には発生しない）
	 * @return 何もしないアクセプター
	 */
	public static <V, E extends Exception> Acceptor<V, E> nop() {
		return value -> {};
	}

	/**
	 * 処理を行います。
	 * 
	 * @param value 引数
	 * @throws E 処理中に例外が発生した場合
	 */
	public void accept(V value) throws E;

	/**
	 * 他のアクセプターとまとめます。
	 * 
	 * @param after 他のアクセプター
	 * @return まとめられたアクセプター
	 */
	public default Acceptor<V, E> andThen(Acceptor<? super V, ? extends E> after) {
		return value -> {
			accept(value);
			after.accept(value);
		};
	}

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

	/**
	 * 処理を行い、リザルトを生成します。
	 * 
	 * <p>
	 * 非チェック例外が発生した時は、そのまま送出されます。
	 * </p>
	 * 
	 * @param <R> 結果
	 * @param value
	 * @return リザルト
	 */
	public default <R> Result<R, E> getResult(V value) {
		return this.<R>normalize(value).getResult();
	}

	/**
	 * 処理を行います。
	 * 
	 * <p>
	 * チェック例外が発生した時は、IllegalStateExceptionを送出します。
	 * 非チェック例外が発生した時は、そのまま送出されます。
	 * </p>
	 * 
	 * @param value 引数
	 */
	public default void uncheck(V value) {
		normalize(value).uncheck();
	}

	/**
	 * 処理を行います。
	 * 
	 * <p>
	 * 例外が発生した時は、無視します。
	 * </p>
	 * 
	 * @param value 引数
	 */
	public default void ignore(V value) {
		normalize(value).ignore();
	}
}
