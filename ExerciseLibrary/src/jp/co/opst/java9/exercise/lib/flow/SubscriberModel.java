package jp.co.opst.java9.exercise.lib.flow;

/**
 * サブスクライバーから呼び出される処理モデルです。
 * 
 * @param <T> 購読した値
 */
public interface SubscriberModel<T> {

	/**
	 * 開始時の処理を行います。
	 * 
	 * @throws Throwable 処理に失敗した場合
	 */
	public void begin() throws Throwable;

	/**
	 * 購読時の処理を行います。
	 * 
	 * @param item 購読した値
	 * @throws Throwable 処理に失敗した場合
	 */
	public void accept(T item) throws Throwable;

	/**
	 * 終了時の処理を行います。
	 * 
	 * @throws Throwable 処理に失敗した場合
	 */
	public void end() throws Throwable;
}
