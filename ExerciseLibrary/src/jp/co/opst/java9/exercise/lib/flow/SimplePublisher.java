package jp.co.opst.java9.exercise.lib.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Consumer;

/**
 * 単純なパブリッシャーです。
 *
 * @param <T> 発行する値
 */
public class SimplePublisher<T> implements AutoCloseable {

	/**
	 * シンプルパブリッシャーのビルダーです。
	 *
	 * @param <T> 発行する値
	 */
	public static final class Builder<T> {

		/** コンテキスト。 */
		private final SubscriberContext context = new SubscriberContext();

		/** モデルリスト。 */
		private final List<SubscriberModel<T>> models = new ArrayList<>();

		/**
		 * コンストラクター。
		 */
		private Builder() {
			context.setDemand(1);
			context.setErrorHandler(Throwable::printStackTrace);
		}

		/**
		 * リクエスト要求数を設定します。
		 * 
		 * <p>
		 * 初期値は1です。
		 * </p>
		 * 
		 * @param demand リクエスト要求数
		 * @return このインスタンス自身
		 */
		public Builder<T> setDemand(int demand) {
			context.setDemand(demand);
			return this;
		}

		/**
		 * エラーハンドラーを設定します。
		 * 
		 * <p>
		 * 初期値は {@link Throwable#printStackTrace()} です。
		 * </p>
		 * 
		 * @param errorHandler エラーハンドラー
		 * @return このインスタンス自身
		 */
		public Builder<T> setErrorCatcher(Consumer<Throwable> errorHandler) {
			context.setErrorHandler(errorHandler);
			return this;
		}

		/**
		 * モデルを追加します。
		 * 
		 * @param model モデル
		 * @return このインスタンス自身
		 */
		public Builder<T> add(SubscriberModel<T> model) {
			models.add(model);
			return this;
		}

		/**
		 * シンプルパブリッシャーを作成します。
		 * 
		 * @return シンプルパブリッシャー
		 */
		public SimplePublisher<T> build() {
			SubmissionPublisher<T> publisher = new SubmissionPublisher<>();
			CountDownLatch latch = new CountDownLatch(models.size());
			context.setDoneSignal(latch::countDown);

			models.stream()
				.map(model -> new SubscriberBase<T>(context, model))
				.forEach(publisher::subscribe);

			return new SimplePublisher<T>(publisher, latch);
		}
	}

	/**
	 * シンプルパブリッシャーのビルダーを取得します。
	 * 
	 * @param <T> 発行する値
	 * @return シンプルパブリッシャーのビルダー
	 */
	public static <T> Builder<T> buider() {
		return new Builder<>();
	}

	/** パブリッシャー。 */
	private final SubmissionPublisher<T> publisher;

	/** 終了済サブスクライバーのカウントダウンラッチ。 */
	private final CountDownLatch latch;

	/**
	 * コンストラクター。
	 * 
	 * @param publisher パブリッシャー
	 * @param latch 終了済サブスクライバーのカウントダウンラッチ
	 */
	private SimplePublisher(SubmissionPublisher<T> publisher, CountDownLatch latch) {
		this.publisher = publisher;
		this.latch = latch;
	}

	/**
	 * 発行します。
	 * 
	 * @param item 発行する値
	 */
	public void publish(T item) {
		publisher.submit(item);
	}

	/**
	 * 発行を終了します。
	 */
	@Override
	public void close() {
		publisher.close();
	}

	/**
	 * 全てのサブスクライバーが終了するまで待機します。
	 * 
	 * @throws InterruptedException 割り込みが発生した場合
	 */
	public void await() throws InterruptedException {
		latch.await();
	}
}
