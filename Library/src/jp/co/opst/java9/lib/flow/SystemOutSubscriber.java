package jp.co.opst.java9.lib.flow;

/**
 * 購読した値を標準出力に送るサブスクライバーです。
 *
 * @param <T> 購読する型
 */
public class SystemOutSubscriber<T> extends SubscriberBase<T> {

	/** 終了シグナル。 */
	private final Runnable doneSignal;

	/**
	 * コンストラクター。
	 * 
	 * @param doneSignal 終了シグナル
	 */
	public SystemOutSubscriber(Runnable doneSignal) {
		super(1);
		this.doneSignal = doneSignal;
	}

	/**
	 * コンストラクター。
	 * 
	 * <p>
	 * このコンストラクターは、終了シグナルを実行しません。
	 * </p>
	 */
	public SystemOutSubscriber() {
		this(() -> {});
	}

	/**
	 * 購読開始時の処理を行います。
	 * 
	 * <p>
	 * 実際には何も行いません。
	 * </p>
	 */
	@Override
	protected void begin() {

	}

	/**
	 * 購読時の処理を行います。
	 * 
	 * <p>
	 * 購読した値を標準出力に出力します。
	 * </p>
	 * 
	 * @param value 購読した値
	 */
	@Override
	protected void accept(T value) {
		System.out.println(value);
	}

	/**
	 * 例外時の処理を行います。
	 * 
	 * <p>
	 * 標準エラーにスタックトレースを出力します。
	 * </p>
	 * 
	 * @param error 発生した例外
	 */
	@Override
	protected void error(Throwable error) {
		error.printStackTrace();
	}

	/**
	 * 購読完了時の処理を行います。
	 * 
	 * <p>
	 * 終了シグナルを実行します。
	 * </p>
	 */
	@Override
	protected void done() {
		doneSignal.run();
	}
}
