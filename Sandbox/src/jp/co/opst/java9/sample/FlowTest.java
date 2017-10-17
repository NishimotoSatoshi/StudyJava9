package jp.co.opst.java9.sample;

import java.time.LocalTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Flow API の演習です。
 */
public class FlowTest {

	/**
	 * 自作のサブスクライバー。
	 */
	public class MySubscriber implements Flow.Subscriber<Integer> {

		/** 名前。 */
		private final String name;

		/** 終了シグナル。 */
		private final Runnable doneSignal;

		/** 購読対象。 */
		private Flow.Subscription subscription;

		/**
		 * コンストラクター。
		 * 
		 * @param name 名前
		 * @param doneSignal 終了シグナル
		 */
		public MySubscriber(String name, Runnable doneSignal) {
			this.name = name;
			this.doneSignal = doneSignal;
		}

		/**
		 * 購読開始時の処理。
		 * 
		 * @param subscription 購読対象
		 */
		@Override
		public void onSubscribe(Flow.Subscription subscription) {
			log(name, "onSubscribe");
			this.subscription = subscription;
			subscription.request(1);
		}

		/**
		 * アイテム受信時の処理。
		 * 
		 * @param item アイテム
		 */
		@Override
		public void onNext(Integer item) {
			log(name, "onNext", item);
			subscription.request(1);
		}

		/**
		 * 例外発生時の処理。
		 * 
		 * @param throwable 発生した例外
		 */
		@Override
		public void onError(Throwable throwable) {
			log(name, "onError", throwable);
			doneSignal.run();
		}

		/**
		 * 購読終了時の処理。
		 */
		@Override
		public void onComplete() {
			log(name, "onComplete");
			doneSignal.run();
		}
	}

	/** 購読させるサブスクライバーの個数。 */
	private static final int NUMBER_OF_SUBSCRIBERS = 2;

	/** 購読させるアイテムの個数。 */
	private static final int NUMBER_OF_ITEMS = 100;

	/**
	 * メイン。
	 * 
	 * @param args 使用されない
	 * @throws Throwable 例外が発生した場合
	 */
	public static void main(String[] args) throws Throwable {
		new FlowTest().test();
	}

	/**
	 * Flow API のサンプルを実行する。
	 * 
	 * @throws InterruptedException サブスクライバーの購読終了を待機している時に、割り込みが発生した場合
	 */
	public void test() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(NUMBER_OF_SUBSCRIBERS);

		try (SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>()) {
			IntStream.rangeClosed(1, NUMBER_OF_SUBSCRIBERS)
				.mapToObj(i -> new MySubscriber("No." + i, latch::countDown))
				.forEach(publisher::subscribe);

			IntStream.rangeClosed(1, NUMBER_OF_ITEMS)
				.forEach(publisher::submit);
		}

		log("all items submitted");
		latch.await();
	}

	/**
	 * コンソールにログを出力する。
	 * 
	 * @param values 出力する値
	 */
	private void log(Object... values) {
		LocalTime time = LocalTime.now();
		String message = Stream.of(values).map(Object::toString).collect(Collectors.joining(" "));
		System.out.println(String.format("[%1$tH:%1$tM:%1$tS.%1$tN] %2$s", time, message));
	}
}
