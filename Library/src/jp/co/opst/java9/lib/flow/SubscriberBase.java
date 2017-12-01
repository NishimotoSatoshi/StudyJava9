package jp.co.opst.java9.lib.flow;

import java.util.concurrent.Flow;

/**
 * Flow APIのサブスクリプター基底クラスです。
 * 
 * @param <T> 購読する型
 */
public abstract class SubscriberBase<T> implements Flow.Subscriber<T> {

	/** リクエスト要求数。 */
	private final int demand;

	/** サブスクリプション。 */
	private Flow.Subscription subscription;

	/**
	 * コンストラクター。
	 * 
	 * @param demand リクエスト要求数
	 */
	protected SubscriberBase(int demand) {
		this.demand = demand;
	}

	/**
	 * 購読開始時の処理を行います。
	 * 
	 * <p>
	 * {@link #begin()}を呼び出します。
	 * </p>
	 * 
	 * @param サブスクリプション
	 */
	@Override
	public final void onSubscribe(Flow.Subscription subscription) {
		this.subscription = subscription;

		try {
			begin();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}

		subscription.request(demand);
	}

	/**
	 * 購読時の処理を行います。
	 * 
	 * <p>
	 * {@link #accept(Object)}を呼び出します。
	 * </p>
	 * 
	 * @param value 購読した値
	 */
	@Override
	public final void onNext(T value) {
		try {
			accept(value);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}

		subscription.request(demand);
	}

	/**
	 * 例外時の処理を行います。
	 * 
	 * <p>
	 * {@link #error(Throwable)}を呼び出した後、{@link #done()}を呼び出します。
	 * </p>
	 * 
	 * @param error 発生した例外
	 */
	@Override
	public final void onError(Throwable error) {
		try {
			error(error);
		} catch (Exception e) {
		}

		done();
	}

	/**
	 * 購読完了時の処理を行います。
	 * 
	 * <p>
	 * {@link #done()}を呼び出します。
	 * </p>
	 */
	@Override
	public final void onComplete() {
		done();
	}

	/**
	 * 購読開始時の処理を行います。
	 * 
	 * @throws Throwable 処理中に復帰不可能な例外が発生した場合
	 */
	protected abstract void begin() throws Throwable;

	/**
	 * 購読時の処理を行います。
	 * 
	 * @param value 購読した値
	 * @throws Throwable 処理中に復帰不可能な例外が発生した場合
	 */
	protected abstract void accept(T value) throws Throwable;

	/**
	 * 例外時の処理を行います。
	 * 
	 * @param error 発生した例外
	 */
	protected abstract void error(Throwable error);

	/**
	 * 購読完了時の処理を行います。
	 */
	protected abstract void done();
}
