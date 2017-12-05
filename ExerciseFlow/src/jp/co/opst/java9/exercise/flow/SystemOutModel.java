package jp.co.opst.java9.exercise.flow;

import jp.co.opst.java9.exercise.lib.flow.SubscriberModel;

/**
 * 購読した値を標準出力に送るモデルです。
 *
 * @param <T> 購読した値
 */
public class SystemOutModel<T> implements SubscriberModel<T> {

	/**
	 * 開始時の処理を行います。
	 * 
	 * <p>
	 * 実際には何も行いません。
	 * </p>
	 */
	@Override
	public void begin() {
	}

	/**
	 * 購読時の処理を行います。
	 * 
	 * <p>
	 * 購読した値を標準出力に出力します。
	 * </p>
	 * 
	 * @param item 購読した値
	 */
	@Override
	public void accept(T item) {
		System.out.println(item);
	}

	/**
	 * 終了時の処理を行います。
	 * 
	 * <p>
	 * 実際には何も行いません。
	 * </p>
	 */
	@Override
	public void end() {
	}
}
