package jp.co.opst.java9.exercise.lib.function;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 処理の結果です。
 *
 * @param <R> 結果
 * @param <E> 例外
 */
public final class Result<R, E extends Exception> {

	/**
	 * 結果が存在しないリザルトを作成します
	 * 
	 * @param <R> 結果
	 * @param <E> 例外
	 * @return リザルト
	 */
	public static <R, E extends Exception> Result<R, E> empty() {
		return new Result<>(Optional.empty(), Optional.empty());
	}

	/**
	 * 例外が発生しなかった時のリザルトを作成します
	 * 
	 * @param <R> 結果
	 * @param <E> 例外
	 * @param result 結果
	 * @return リザルト
	 */
	public static <R, E extends Exception> Result<R, E> success(R result) {
		return new Result<>(Optional.ofNullable(result), Optional.empty());
	}

	/**
	 * 例外が発生した時のリザルトを作成します
	 * 
	 * @param <R> 結果
	 * @param <E> 例外
	 * @param exception 例外
	 * @return リザルト
	 */
	public static <R, E extends Exception> Result<R, E> failure(E exception) {
		return new Result<>(Optional.empty(), Optional.of(exception));
	}

	/** 結果。 */
	private final Optional<R> optionalResult;

	/** 例外。 */
	private final Optional<E> optionalException;

	/**
	 * コンストラクター。
	 * 
	 * @param optionalResult 結果
	 * @param optionalException 例外
	 */
	private Result(Optional<R> optionalResult, Optional<E> optionalException) {
		this.optionalResult = optionalResult;
		this.optionalException = optionalException;
	}

	/**
	 * 結果を取得します。
	 * 
	 * @return 結果
	 */
	public Optional<R> result() {
		return optionalResult;
	}

	/**
	 * 例外を取得します。
	 * 
	 * @return 例外
	 */
	public Optional<E> exception() {
		return optionalException;
	}

	/**
	 * 結果が条件に合わない場合、結果を消します。
	 * 
	 * @param predicate 結果の条件
	 * @return リザルト
	 */
	public Result<R, E> filter(Predicate<? super R> predicate) {
		return new Result<>(optionalResult.filter(predicate), optionalException);
	}

	/**
	 * 結果が存在する時は、指定されたプロセッサーを実行します。
	 * 
	 * @param <RR> 結果
	 * @param <EE> 例外
	 * @param processor 結果が存在する時に実行するプロセッサー
	 * @return リザルト
	 */
	public <RR, EE extends Exception> Result<RR, EE> map(ProcessorWithArg<R, RR, EE> processor) {
		return optionalResult
			.map(processor::with)
			.map(Processor::invoke)
			.orElseGet(Result::empty);
	}

	/**
	 * 結果が存在することを判定します。
	 * 
	 * @return 結果が存在する場合はtrue
	 */
	public boolean isPresent() {
		return optionalResult.isPresent();
	}

	/**
	 * 結果が存在しないことを判定します。
	 * 
	 * @return 結果が存在しない場合はtrue
	 */
	public boolean isAbsent() {
		return !optionalResult.isPresent();
	}

	/**
	 * 結果が存在する場合に処理を行います。
	 * 
	 * @param action 結果を処理する関数
	 * @return このインスタンス自身
	 */
	public Result<R, E> ifPresent(Consumer<? super R> action) {
		optionalResult.ifPresent(action);
		return this;
	}

	/**
	 * 結果が存在しない場合に処理を行います。
	 * 
	 * @param action 結果を処理する関数
	 * @return このインスタンス自身
	 */
	public Result<R, E> ifAbsent(Runnable action) {
		optionalResult.ifPresentOrElse(result -> {}, action);
		return this;
	}

	/**
	 * 例外が存在する場合は、その例外を再送出します。
	 * 
	 * @return このインスタンス自身
	 * @throws E 例外が存在する場合
	 */
	public Result<R, E> rethrow() throws E {
		if (optionalException.isPresent()) {
			throw optionalException.get();
		}

		return this;
	}
}
