package jp.co.opst.java9.exercise.lib.function;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import jp.co.opst.java9.exercise.lib.exception.Try;

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
	 * 処理を行いますが、チェック例外が発生した場合は、非チェック例外にラッピングしてから送出します。
	 * 
	 * @return 処理した結果
	 */
	public default R processUncheck() {
		return Try.uncheck(this::process);
	}

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
	public default Stream<R> stream() {
		return Stream.generate(() -> processUncheck());
	}

	/**
	 * 処理を無限に繰り返すストリームを作成します。
	 * 
	 * @return ストリーム
	 */
	public default Stream<Result<R, E>> resultStream() {
		return Stream.generate(this::invoke);
	}

	/**
	 * 処理の結果が存在し、かつ条件に一致している間、アクションを繰り返します。
	 * 
	 * @param predicate 繰り返し条件 （この条件を満たしている間、処理を繰り返す）
	 * @param action アクション
	 * @return 繰り返し条件を満たさなかった、最初のリザルト
	 */
	public default Result<R, E> loopWhile(Predicate<? super R> predicate, Consumer<? super R> action) {
		return resultStream()
			.peek(result -> result.ifResult(predicate, action))
			.filter(result -> !result.isResult(predicate))
			.findFirst()
			.orElseGet(Result::empty);
	}

	/**
	 * 処理の結果が存在し、かつ条件に一致するまで間、アクションを繰り返します。
	 * 
	 * @param condition 終了条件 （この条件を満たしていない間、処理を繰り返す）
	 * @param action アクション
	 * @return 終了条件を満たした、最初のリザルト
	 */
	public default Result<R, E> loopUntil(Predicate<? super R> condition, Consumer<? super R> action) {
		return loopWhile(condition.negate(), action);
	}

	/**
	 * 処理の結果が存在する間、アクションを繰り返します。
	 * 
	 * @param action アクション
	 * @return 処理の結果が存在しなかった、最初のリザルト
	 */
	public default Result<R, E> loopWhilePresent(Consumer<? super R> action) {
		return resultStream()
			.peek(result -> result.ifPresent(action))
			.filter(Result::isAbsent)
			.findFirst()
			.orElseGet(Result::empty);
	}
}
