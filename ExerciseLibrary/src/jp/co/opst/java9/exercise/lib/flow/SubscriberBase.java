package jp.co.opst.java9.exercise.lib.flow;

import java.util.concurrent.Flow;

import jp.co.opst.java9.exercise.lib.exception.Try;

/**
 * サブスクライバーの基底クラスです。
 * 
 * @param <T> 購読する型
 */
public class SubscriberBase<T> implements Flow.Subscriber<T> {

	/** コンテキスト。 */
	private final SubscriberContext context;

	/** モデル。 */
	private final SubscriberModel<? super T> model;

	/** サブスクリプション。 */
	private Flow.Subscription subscription;

	/**
	 * コンストラクター。
	 * 
	 * @param context コンテキスト
	 * @param model モデル
	 */
	public SubscriberBase(SubscriberContext context, SubscriberModel<? super T> model) {
		this.context = context;
		this.model = model;
	}

	/**
	 * 購読開始時の処理を行います。
	 * 
	 * @param サブスクリプション
	 */
	@Override
	public final void onSubscribe(Flow.Subscription subscription) {
		Try.uncheck(model::begin);
		this.subscription = subscription;
		subscription.request(context.getDemand());
	}

	/**
	 * 購読時の処理を行います。
	 * 
	 * @param item 購読した値
	 */
	@Override
	public final void onNext(T item) {
		Try.of(item).uncheck(model::accept);
		subscription.request(context.getDemand());
	}

	/**
	 * 例外時の処理を行います。
	 * 
	 * @param error 発生した例外
	 */
	@Override
	public final void onError(Throwable error) {
		Try.of(error).ignore(context.getErrorHandler()::accept);
		Try.ignore(model::end);
		context.getDoneSignal().run();
	}

	/**
	 * 購読完了時の処理を行います。
	 */
	@Override
	public final void onComplete() {
		Try.ignore(model::end);
		context.getDoneSignal().run();
	}
}
