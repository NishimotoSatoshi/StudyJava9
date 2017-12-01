package jp.co.opst.java9.lib.function;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 例外を考慮した関数です。
 *
 * @param <R> 処理した結果
 * @param <T> 発生した例外
 */
@FunctionalInterface
public interface Processor<R, T extends Throwable> {

	/**
	 * プロセッサーを作成します。
	 * 
	 * @param <R> 処理した結果
	 * @param <T> 発生した例外
	 * @param processor プロセッサーの関数
	 * @return プロセッサー
	 */
	public static <R, T extends Throwable> Processor<R, T> of(Processor<R, T> processor) {
		return processor;
	}

	/**
	 * 処理を行います。
	 * 
	 * @return 処理した結果
	 * @throws T 処理中に例外が発生した場合
	 */
	public R process() throws T;

	/**
	 * 処理を行って、リザルトを生成します。
	 * 
	 * <p>
	 * 処理中にランタイム例外が発生した場合は、発生したランタイム例外を送出します。
	 * </p>
	 * 
	 * @return リザルト
	 */
	public default Result<R, T> invoke() {
		try {
			return Result.success(process());
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable t) {
			@SuppressWarnings("unchecked") T thrown = (T) t;
			return Result.failure(thrown);
		}
	}

	/**
	 * 処理を無限に繰り返すストリームを作成します。
	 * 
	 * @return ストリーム
	 */
	public default Stream<Result<R, T>> stream() {
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
	 * @throws T 処理中に例外が発生した場合
	 */
	public default void loopWhile(Predicate<? super R> condition, Consumer<? super R> action) throws T {
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
	 * @throws T 処理中に例外が発生した場合
	 */
	public default void loopUntil(Predicate<? super R> condition, Consumer<? super R> action) throws T {
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
	 * @throws T 処理中に例外が発生した場合
	 */
	public default void loopWhilePresent(Consumer<? super R> action) throws T {
		loopWhile(result -> true, action);
	}
}
