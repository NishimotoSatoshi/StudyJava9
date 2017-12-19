package jp.co.opst.java9.exercise.lib.exception;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 処理の結果です。
 *
 * @param <R> 結果
 * @param <E> 例外
 */
public final class Result<R, E extends Exception> {

	/**
	 * 結果および例外が存在しないリザルトを作成します
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
	 * 結果が条件に合わない場合、結果を削除します。
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
	public <RR, EE extends Exception> Result<RR, EE> map(Processor<? super R, RR, EE> processor) {
		return optionalResult
			.map(processor::normalize)
			.map(Generator::getResult)
			.orElseGet(Result::empty);
	}

	/**
	 * 結果および例外が存在しないことを判定します。
	 * 
	 * @return 結果および例外が存在しない場合はtrue
	 */
	public boolean isEmpty() {
		return !optionalResult.isPresent() && !optionalException.isPresent();
	}

	/**
	 * 結果または例外が存在することを判定します。
	 * 
	 * @return 結果または例外が存在する場合はtrue
	 */
	public boolean isNotEmpty() {
		return optionalResult.isPresent() || optionalException.isPresent();
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
	 * 結果が存在し、かつ条件と一致していることを判定します。
	 * 
	 * @param predicate 条件
	 * @return 結果が存在し、かつ条件と一致している場合はtrue
	 */
	public boolean isResult(Predicate<? super R> predicate) {
		return optionalResult.filter(predicate).isPresent();
	}

	/**
	 * 結果が存在し、かつ条件と一致していないことを判定します。
	 * 
	 * @param predicate 条件
	 * @return 結果が存在し、かつ条件と一致していない場合はtrue
	 */
	public boolean isResultNot(Predicate<? super R> predicate) {
		return optionalResult.filter(predicate.negate()).isPresent();
	}

	/**
	 * 結果および例外が存在しない場合、処理を行います。
	 * 
	 * @param <EE> 処理する関数で発生しうる例外
	 * @param action 処理する関数
	 * @return このインスタンス自身
	 * @throws EE 処理する関数で例外が発生した場合
	 */
	public <EE extends Exception> Result<R, E> ifEmpty(Invoker<EE> action) throws EE {
		if (isEmpty()) {
			action.invoke();
		}

		return this;
	}

	/**
	 * 結果および例外が存在しない場合、処理を行います。
	 * 
	 * @param action 処理する関数
	 * @return このインスタンス自身
	 */
	public Result<R, E> ifEmpty(Runnable action) {
		return ifEmpty(Invoker.from(action));
	}

	/**
	 * 結果または例外が存在する場合、処理を行います。
	 * 
	 * @param <EE> 処理する関数で発生しうる例外
	 * @param action 処理する関数
	 * @return このインスタンス自身
	 * @throws EE 処理する関数で例外が発生した場合
	 */
	public <EE extends Exception> Result<R, E> ifNotEmpty(Acceptor<Result<? super R, ? super E>, EE> action) throws EE {
		if (isNotEmpty()) {
			action.accept(this);
		}

		return this;
	}

	/**
	 * 結果または例外が存在する場合、処理を行います。
	 * 
	 * @param action 処理する関数
	 * @return このインスタンス自身
	 */
	public Result<R, E> ifNotEmpty(Consumer<Result<? super R, ? super E>> action) {
		return ifNotEmpty(Acceptor.from(action));
	}

	/**
	 * 結果が存在する場合、処理を行います。
	 * 
	 * @param <EE> 処理する関数で発生しうる例外
	 * @param action 処理する関数
	 * @return このインスタンス自身
	 * @throws EE 処理する関数で例外が発生した場合
	 */
	public <EE extends Exception> Result<R, E> ifPresent(Acceptor<? super R, EE> action) throws EE {
		if (isPresent()) {
			action.accept(optionalResult.get());
		}

		return this;
	}

	/**
	 * 結果が存在する場合、処理を行います。
	 * 
	 * @param action 処理する関数
	 * @return このインスタンス自身
	 */
	public Result<R, E> ifPresent(Consumer<? super R> action) {
		return ifPresent(Acceptor.from(action));
	}

	/**
	 * 結果が存在しない場合、処理を行います。
	 * 
	 * @param <EE> 処理する関数で発生しうる例外
	 * @param action 処理する関数
	 * @return このインスタンス自身
	 * @throws EE 処理する関数で例外が発生した場合
	 */
	public <EE extends Exception> Result<R, E> ifAbsent(Invoker<EE> action) throws EE {
		if (isAbsent()) {
			action.invoke();
		}

		return this;
	}

	/**
	 * 結果が存在しない場合、処理を行います。
	 * 
	 * @param action 処理する関数
	 * @return このインスタンス自身
	 */
	public Result<R, E> ifAbsent(Runnable action) {
		return ifAbsent(Invoker.from(action));
	}

	/**
	 * 結果が存在し、かつ条件と一致している場合、処理を行います。
	 * 
	 * @param <EE> 処理する関数で発生しうる例外
	 * @param predicate 条件
	 * @param action 処理する関数
	 * @return このインスタンス自身
	 * @throws EE 処理する関数で例外が発生した場合
	 */
	public <EE extends Exception> Result<R, E> ifResult(Predicate<? super R> predicate, Acceptor<? super R, EE> action) throws EE {
		if (optionalResult.filter(predicate).isPresent()) {
			action.accept(optionalResult.get());
		}

		return this;
	}

	/**
	 * 結果が存在し、かつ条件と一致している場合、処理を行います。
	 * 
	 * @param predicate 条件
	 * @param action 処理する関数
	 * @return このインスタンス自身
	 */
	public Result<R, E> ifResult(Predicate<? super R> predicate, Consumer<? super R> action) {
		return ifResult(predicate, Acceptor.from(action));
	}

	/**
	 * 結果が存在し、かつ条件と一致していない場合、処理を行います。
	 * 
	 * @param <EE> 処理する関数で発生しうる例外
	 * @param predicate 条件
	 * @param action 処理する関数
	 * @return このインスタンス自身
	 * @throws EE 処理する関数で例外が発生した場合
	 */
	public <EE extends Exception> Result<R, E> ifResultNot(Predicate<? super R> predicate, Acceptor<? super R, EE> action) throws EE {
		return ifResult(predicate.negate(), action);
	}

	/**
	 * 結果が存在し、かつ条件と一致していない場合、処理を行います。
	 * 
	 * @param predicate 条件
	 * @param action 処理する関数
	 * @return このインスタンス自身
	 */
	public Result<R, E> ifResultNot(Predicate<? super R> predicate, Consumer<? super R> action) {
		return ifResult(predicate.negate(), action);
	}

	/**
	 * 例外が存在する場合、例外を再送出します。
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

	/**
	 * 例外が存在する場合、別の例外にラッピングして再送出します。
	 * 
	 * @param <EE> ラッピングする例外
	 * @param wrapper 別の例外にラッピングする関数
	 * @return このインスタンス自身
	 * @throws EE 例外が存在する場合
	 */
	public <EE extends Exception> Result<R, E> rethrow(Function<? super E, EE> wrapper) throws EE {
		if (optionalException.isPresent()) {
			throw optionalException.map(wrapper).get();
		}

		return this;
	}
}
