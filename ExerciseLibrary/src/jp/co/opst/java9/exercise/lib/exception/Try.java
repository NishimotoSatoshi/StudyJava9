package jp.co.opst.java9.exercise.lib.exception;

import java.util.Optional;
import java.util.function.Function;

/**
 * 例外処理のユーティリティです。
 * 
 * @param <T> 実行するオブジェクト
 */
public final class Try<T> {

	/**
	 * 処理を行い、リザルトを生成します。
	 * 
	 * <p>
	 * 非チェック例外が発生した時は、そのまま送出されます。
	 * </p>
	 * 
	 * @param <R> 結果
	 * @param <E> 処理中に発しうる例外
	 * @param action 処理を行う関数
	 * @return リザルト
	 */
	public static <R, E extends Exception> Result<R, E> getResult(Generator<R, E> action) {
		return action.getResult();
	}

	/**
	 * 処理を行い、リザルトを生成します。
	 * 
	 * <p>
	 * 非チェック例外が発生した時は、そのまま送出されます。
	 * </p>
	 * 
	 * @param <R> 結果 (実際には、常にnull)
	 * @param <E> 処理中に発しうる例外
	 * @param action 処理を行う関数
	 * @return リザルト
	 */
	public static <R, E extends Exception> Result<R, E> getResult(Invoker<E> action) {
		return action.getResult();
	}

	/**
	 * 処理を行います。
	 * 
	 * <p>
	 * チェック例外が発生した時は、IllegalStateExceptionを送出します。
	 * 非チェック例外が発生した時は、そのまま送出されます。
	 * </p>
	 * 
	 * @param <R> 結果
	 * @param action 処理を行う関数
	 * @return 結果
	 */
	public static <R> R uncheck(Generator<R, ?> action) {
		return action.uncheck();
	}

	/**
	 * 処理を行います。
	 * 
	 * <p>
	 * チェック例外が発生した時は、IllegalStateExceptionを送出します。
	 * 非チェック例外が発生した時は、そのまま送出されます。
	 * </p>
	 * 
	 * @param action 処理を行う関数
	 */
	public static void uncheck(Invoker<?> action) {
		action.uncheck();
	}

	/**
	 * 処理を行います。
	 * 
	 * <p>
	 * 例外が発生した時は、結果がnullになります。
	 * </p>
	 * 
	 * @param <R> 結果
	 * @param action 処理を行う関数
	 * @return 結果
	 */
	public static <R> R ignore(Generator<R, ?> action) {
		return action.ignore();
	}

	/**
	 * 処理を行います。
	 * 
	 * <p>
	 * 例外が発生しても無視します。
	 * </p>
	 * 
	 * @param action 処理を行う関数
	 */
	public static void ignore(Invoker<?> action) {
		action.ignore();
	}

	/**
	 * オブジェクトを指定して、トライを作成します。
	 * 
	 * @param <T> オブジェクト
	 * @param target オブジェクト
	 * @return トライ
	 */
	public static <T> Try<T> of(T target) {
		return new Try<>(target);
	}

	/** オブジェクト。 */
	private final Optional<T> optionalTarget;

	/**
	 * コンストラクター。
	 * 
	 * @param target オブジェクト
	 */
	private Try(T target) {
		this.optionalTarget = Optional.ofNullable(target);
	}

	/**
	 * オブジェクトがnullでない場合のみ処理を行い、リザルトを生成します。
	 * 
	 * <p>
	 * 非チェック例外が発生した時は、そのまま送出されます。
	 * </p>
	 * 
	 * @param <R> 結果
	 * @param <E> 処理中に発しうる例外
	 * @param action 処理を行う関数
	 * @return リザルト
	 */
	public <R, E extends Exception> Result<R, E> getResult(Processor<? super T, R, E> action) {
		return getResultOfTarget(action::normalize);
	}

	/**
	 * オブジェクトがnullでない場合のみ処理を行い、リザルトを生成します。
	 * 
	 * <p>
	 * 非チェック例外が発生した時は、そのまま送出されます。
	 * </p>
	 * 
	 * @param <R> 結果 (実際には、常にnull)
	 * @param <E> 処理中に発しうる例外
	 * @param action 処理を行う関数
	 * @return リザルト
	 */
	public <R, E extends Exception> Result<R, E> getResult(Acceptor<? super T, E> action) {
		return getResultOfTarget(action::normalize);
	}

	/**
	 * オブジェクトがnullでない場合のみ処理を行います。
	 * 
	 * <p>
	 * チェック例外が発生した時は、IllegalStateExceptionを送出します。
	 * 非チェック例外が発生した時は、そのまま送出されます。
	 * </p>
	 * 
	 * @param <R> 結果
	 * @param action 処理を行う関数
	 * @return 結果
	 */
	public <R> R uncheck(Processor<? super T, R, ?> action) {
		return uncheckAndGetValue(action::normalize);
	}

	/**
	 * オブジェクトがnullでない場合のみ処理を行います。
	 * 
	 * <p>
	 * チェック例外が発生した時は、IllegalStateExceptionを送出します。
	 * 非チェック例外が発生した時は、そのまま送出されます。
	 * </p>
	 * 
	 * @param action 処理を行う関数
	 */
	public void uncheck(Acceptor<? super T, ?> action) {
		uncheckAndGetValue(action::normalize);
	}

	/**
	 * オブジェクトがnullでない場合のみ処理を行います。
	 * 
	 * <p>
	 * 例外が発生した時は、結果がnullになります。
	 * </p>
	 * 
	 * @param <R> 結果
	 * @param action 処理を行う関数
	 * @return 結果
	 */
	public <R> R ignore(Processor<? super T, R, ?> action) {
		return ignoreAndGetValue(action::normalize);
	}

	/**
	 * オブジェクトがnullでない場合のみ処理を行います。
	 * 
	 * <p>
	 * 例外が発生しても無視します。
	 * </p>
	 * 
	 * @param action 処理を行う関数
	 */
	public void ignore(Acceptor<? super T, ?> action) {
		ignoreAndGetValue(action::normalize);
	}

	/**
	 * オブジェクトがnullでない場合のみ処理を行い、リザルトを生成します。
	 * 
	 * <p>
	 * 非チェック例外が発生した時は、そのまま送出されます。
	 * </p>
	 * 
	 * @param <R> 結果
	 * @param <E> 処理中に発しうる例外
	 * @param mapper オブジェクトをジェネレーターに変換する関数
	 * @return リザルト
	 */
	private <R, E extends Exception> Result<R, E> getResultOfTarget(Function<? super T, Generator<R, E>> mapper) {
		return optionalTarget
			.map(mapper)
			.map(Generator::getResult)
			.orElseGet(Result::empty);
	}

	/**
	 * オブジェクトがnullでない場合のみ処理を行います。
	 * 
	 * <p>
	 * チェック例外が発生した時は、IllegalStateExceptionを送出します。
	 * 非チェック例外が発生した時は、そのまま送出されます。
	 * </p>
	 * 
	 * @param <R> 結果
	 * @param mapper オブジェクトをジェネレーターに変換する関数
	 * @return 結果
	 */
	private <R> R uncheckAndGetValue(Function<? super T, Generator<R, ?>> mapper) {
		return optionalTarget
			.map(mapper)
			.map(Generator::uncheck)
			.orElse(null);
	}

	/**
	 * オブジェクトがnullでない場合のみ処理を行います。
	 * 
	 * <p>
	 * 例外が発生しても無視します。
	 * </p>
	 * 
	 * @param <R> 結果
	 * @param mapper オブジェクトをジェネレーターに変換する関数
	 * @return 結果
	 */
	private <R> R ignoreAndGetValue(Function<? super T, Generator<R, ?>> mapper) {
		return optionalTarget
			.map(mapper)
			.map(Generator::ignore)
			.orElse(null);
	}
}
