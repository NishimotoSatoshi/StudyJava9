package jp.co.opst.java9.exercise.lib.function;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * チェック例外を考慮した関数です。
 *
 * @param <R> 処理した結果
 * @param <E> 発生したチェック例外
 */
@FunctionalInterface
public interface Processor<R, E extends Exception> {

	/**
	 * プロセッサーを作成します。
	 * 
	 * @param <R> 処理した結果
	 * @param <E> 発生したチェック例外
	 * @param processor プロセッサーの関数
	 * @return プロセッサー
	 */
	public static <R, E extends Exception> Processor<R, E> of(Processor<R, E> processor) {
		return processor;
	}

	/**
	 * 処理を行います。
	 * 
	 * @return 処理した結果
	 * @throws E 処理中にチェック例外が発生した場合
	 */
	public R process() throws E;

	/**
	 * 処理を行って、リザルトを生成します。
	 * 
	 * <p>
	 * 処理中に非チェック例外が発生した場合は、その例外を送出します。
	 * </p>
	 * 
	 * @return リザルト
	 */
	public default Result<R, E> invoke() {
		try {
			return Result.success(process());
		} catch (RuntimeException r) {
			throw r;
		} catch (Exception e) {
			@SuppressWarnings("unchecked") E exception = (E) e;
			return Result.failure(exception);
		}
	}

	/**
	 * 処理を無限に繰り返すストリームを作成します。
	 * 
	 * @return ストリーム
	 */
	public default Stream<Result<R, E>> stream() {
		return Stream.generate(this::invoke);
	}

	/**
	 * 処理の結果が条件に一致している間、アクションを繰り返します。
	 * 
	 * <p>
	 * ただし、結果が存在しない場合、または例外が発生した場合は、無条件に処理を終了します。
	 * </p>
	 * 
	 * @param condition 繰り返し条件（この条件を満たしている間、処理を繰り返す）
	 * @param action アクション
	 * @throws E 処理中に例外が発生した場合
	 */
	public default void loopWhile(Predicate<? super R> condition, Consumer<? super R> action) throws E {
		stream()
			.map(result -> result.filter(condition))
			.peek(result -> result.ifPresent(action))
			.filter(Result::isAbsent)
			.findFirst()
			.orElseGet(Result::empty)
			.rethrow();
	}

	/**
	 * 処理の結果が条件に一致するまで間、アクションを繰り返します。
	 * 
	 * <p>
	 * ただし、結果が存在しない場合、または例外が発生した場合は、無条件に処理を終了します。
	 * </p>
	 * 
	 * @param condition 終了条件（この条件を満たしていない間、処理を繰り返す）
	 * @param action アクション
	 * @throws E 処理中に例外が発生した場合
	 */
	public default void loopUntil(Predicate<? super R> condition, Consumer<? super R> action) throws E {
		loopWhile(condition.negate(), action);
	}

	/**
	 * 処理の結果が存在する間、アクションを繰り返します。
	 * 
	 * <p>
	 * ただし、例外が発生した場合は、無条件に処理を終了します。
	 * </p>
	 * 
	 * @param action アクション
	 * @throws E 処理中に例外が発生した場合
	 */
	public default void loopWhilePresent(Consumer<? super R> action) throws E {
		loopWhile(result -> true, action);
	}
}
