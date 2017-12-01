package jp.co.opst.java9.lib.function;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 処理の結果です。
 *
 * @param <R> 結果
 * @param <T> 例外
 */
public final class Result<R, T extends Throwable> {

	/**
	 * 結果が存在しないリザルトを作成します
	 * 
	 * @param <R> 結果
	 * @param <T> 例外
	 * @return リザルト
	 */
	public static <R, T extends Throwable> Result<R, T> empty() {
		return new Result<>(Optional.empty(), Optional.empty());
	}

	/**
	 * 例外が発生しなかった時のリザルトを作成します
	 * 
	 * @param <R> 結果
	 * @param <T> 例外
	 * @param result 結果
	 * @return リザルト
	 */
	public static <R, T extends Throwable> Result<R, T> success(R result) {
		return new Result<>(Optional.ofNullable(result), Optional.empty());
	}

	/**
	 * 例外が発生した時のリザルトを作成します
	 * 
	 * @param <R> 結果
	 * @param <T> 例外
	 * @param thrown 例外
	 * @return リザルト
	 */
	public static <R, T extends Throwable> Result<R, T> failure(T thrown) {
		return new Result<>(Optional.empty(), Optional.of(thrown));
	}

	/** 結果。 */
	private final Optional<R> optionalResult;

	/** 例外。 */
	private final Optional<T> optionalThrown;

	/**
	 * コンストラクター。
	 * 
	 * @param optionalResult 結果
	 * @param optionalThrown 例外
	 */
	private Result(Optional<R> optionalResult, Optional<T> optionalThrown) {
		this.optionalResult = optionalResult;
		this.optionalThrown = optionalThrown;
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
	public Optional<T> thrown() {
		return optionalThrown;
	}

	/**
	 * 結果が条件に合わない場合、結果を消します。
	 * 
	 * @param predicate 結果の条件
	 * @return リザルト
	 */
	public Result<R, T> filter(Predicate<? super R> predicate) {
		return new Result<>(optionalResult.filter(predicate), optionalThrown);
	}

	/**
	 * 結果が存在する時は、指定されたプロセッサーを実行します。
	 * 
	 * @param <RR> 結果
	 * @param <TT> 例外
	 * @param processor 結果が存在する時に実行するプロセッサー
	 * @return リザルト
	 */
	public <RR, TT extends Throwable> Result<RR, TT> map(ValueProcessor<R, RR, TT> processor) {
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
	public Result<R, T> ifPresent(Consumer<? super R> action) {
		optionalResult.ifPresent(action);
		return this;
	}

	/**
	 * 結果が存在しない場合に処理を行います。
	 * 
	 * @param action 結果を処理する関数
	 * @return このインスタンス自身
	 */
	public Result<R, T> ifAbsent(Runnable action) {
		optionalResult.ifPresentOrElse(result -> {}, action);
		return this;
	}

	/**
	 * 例外が存在する場合は、その例外を再送出します。
	 * 
	 * @return このインスタンス自身
	 * @throws T 例外が存在する場合
	 */
	public Result<R, T> rethrow() throws T {
		if (optionalThrown.isPresent()) {
			throw optionalThrown.get();
		}

		return this;
	}
}
