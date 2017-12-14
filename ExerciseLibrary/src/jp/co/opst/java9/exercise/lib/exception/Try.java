package jp.co.opst.java9.exercise.lib.exception;

/**
 * 例外処理のユーティリティです。
 */
public final class Try {

	/**
	 * 処理を行い、リザルトを生成します。
	 * 
	 * <p>
	 * 非チェック例外が発生した時は、そのまま送出されます。
	 * </p>
	 * 
	 * @param action 処理を行う関数
	 * @return リザルト
	 */
	public static <R, E extends Exception> Result<R, E> get(Generator<R, E> action) {
		return action.get();
	}

	/**
	 * 処理を行い、リザルトを生成します。
	 * 
	 * <p>
	 * 非チェック例外が発生した時は、そのまま送出されます。
	 * </p>
	 * 
	 * @param action 処理を行う関数
	 * @return リザルト
	 */
	public static <R, E extends Exception> Result<R, E> get(Invoker<E> action) {
		return action.<R>normalize().get();
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
		action.normalize().uncheck();
	}

	/**
	 * 処理を行います。
	 * 
	 * <p>
	 * 例外が発生した時は、結果がnullになります。
	 * </p>
	 * 
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
		action.normalize().ignore();
	}

	private Try() {
	}
}
