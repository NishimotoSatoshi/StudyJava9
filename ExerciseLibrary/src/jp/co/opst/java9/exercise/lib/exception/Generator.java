package jp.co.opst.java9.exercise.lib.exception;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * 結果を返す関数です。
 * 
 * @param <R> 結果
 * @param <E> 処理中に発しうる例外
 */
@FunctionalInterface
public interface Generator<R, E extends Exception> {

	/**
	 * 関数をジェネレーターにキャストします。
	 * 
	 * @param <R> 結果
	 * @param <E> 処理中に発しうる例外
	 * @param action 関数
	 * @return ジェネレーター
	 */
	public static <R, E extends Exception> Generator<R, E> of(Generator<R, E> action) {
		return action;
	}

	/**
	 * サプライヤーからジェネレーターを作成します。
	 * 
	 * @param <R> 結果
	 * @param supplier サプライヤー
	 * @return ジェネレーター
	 */
	public static <R> Generator<R, RuntimeException> from(Supplier<R> supplier) {
		return supplier::get;
	}

	/**
	 * 常に同じ値を返すジェネレーターを作成します。
	 * 
	 * @param <R> 結果
	 * @param <E> 処理中に発しうる例外（実際には発生しない）
	 * @param value 返す値
	 * @return 常に同じ値を返すジェネレーター
	 */
	public static <R, E extends Exception> Generator<R, E> fix(R value) {
		return () -> value;
	}

	/**
	 * 処理を行います。
	 * 
	 * @return 結果
	 * @throws E 処理中に例外が発生した場合
	 */
	public R generate() throws E;

	/**
	 * 処理を行い、リザルトを生成します。
	 * 
	 * <p>
	 * 非チェック例外が発生した時は、そのまま送出されます。
	 * </p>
	 * 
	 * @return リザルト
	 */
	public default Result<R, E> getResult() {
		try {
			return Result.success(generate());
		} catch (RuntimeException r) {
			throw r;
		} catch (Throwable t) {
			@SuppressWarnings("unchecked") E exception = (E) t;
			return Result.failure(exception);
		}
	}

	/**
	 * 処理を行います。
	 * 
	 * <p>
	 * チェック例外が発生した時は、IllegalStateExceptionを送出します。
	 * 非チェック例外が発生した時は、そのまま送出されます。
	 * </p>
	 * 
	 * @return 結果
	 */
	public default R uncheck() {
		try {
			return generate();
		} catch (RuntimeException r) {
			throw r;
		} catch (Throwable t) {
			throw new IllegalStateException(t);
		}
	}

	/**
	 * 処理を行います。
	 * 
	 * <p>
	 * 例外が発生した時は、結果がnullになります。
	 * </p>
	 * 
	 * @return 結果
	 */
	public default R ignore() {
		try {
			return generate();
		} catch (Throwable t) {
			return null;
		}
	}

	/**
	 * 処理を無限に繰り返すストリームを作成します。
	 * 
	 * <p>
	 * 非チェック例外が発生した時は、そのまま送出されます。
	 * </p>
	 * 
	 * @return リザルトを返すストリーム
	 */
	public default Stream<Result<R, E>> stream() {
		return Stream.generate(this::getResult);
	}

	/**
	 * 処理を無限に繰り返すストリームを作成します。
	 * 
	 * <p>
	 * チェック例外が発生した時は、IllegalStateExceptionを送出します。
	 * 非チェック例外が発生した時は、そのまま送出されます。
	 * </p>
	 * 
	 * @return 結果を返すストリーム
	 */
	public default Stream<R> streamUncheck() {
		return Stream.generate(this::uncheck);
	}

	/**
	 * 処理の結果が存在しなくなるまで、処理を繰り返します。
	 * 
	 * <p>
	 * 非チェック例外が発生した時は、そのまま送出されます。
	 * </p>
	 * 
	 * @param then 処理の結果が存在する時に実行する関数
	 * @return 処理の結果が存在しなくなった時のリザルト
	 */
	public default Result<R, E> whilePresent(Consumer<? super R> then) {
		return Stream.generate(this::getResult)
			.peek(result -> result.ifPresent(then))
			.filter(Result::isAbsent)
			.findFirst()
			.get();
	}

	/**
	 * 処理の結果が存在しなくなるまで、処理を繰り返します。
	 * 
	 * <p>
	 * チェック例外が発生した時は、IllegalStateExceptionを送出します。
	 * 非チェック例外が発生した時は、そのまま送出されます。
	 * </p>
	 * 
	 * @param then 処理の結果が存在する時に実行する関数
	 */
	public default void whilePresentUncheck(Consumer<? super R> then) {
		Stream.generate(this::uncheck)
			.takeWhile(Objects::nonNull)
			.forEach(then);
	}
}
