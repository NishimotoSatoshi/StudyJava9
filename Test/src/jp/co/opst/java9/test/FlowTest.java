package jp.co.opst.java9.test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

/**
 * Flow APIに関するテストです。
 */
public class FlowTest {

	/**
	 * サブスクライバー。
	 */
	public class MySubscriber implements Flow.Subscriber<Integer> {

		/** 名前。 */
		private final String name;

		/** 終了シグナル。 */
		private final Runnable doneSignal;

		/** 受信したアイテム。 */
		private final List<Integer> items = new ArrayList<>();

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
		 * 購読開始時の処理を行います。
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
		 * アイテム受信時の処理を行います。
		 * 
		 * @param item アイテム
		 */
		@Override
		public void onNext(Integer item) {
			log(name, "onNext", item);
			items.add(item);
			subscription.request(1);
		}

		/**
		 * 例外発生時の処理を行います。
		 * 
		 * @param throwable 発生した例外
		 */
		@Override
		public void onError(Throwable throwable) {
			log(name, "onError", throwable);
			doneSignal.run();
		}

		/**
		 * 購読終了時の処理を行います。
		 */
		@Override
		public void onComplete() {
			log(name, "onComplete");
			doneSignal.run();
		}

		/**
		 * 受信したアイテムを取得します。
		 * 
		 * @return 受信したアイテム
		 */
		public List<Integer> getItems() {
			return Collections.unmodifiableList(items);
		}
	}

	/**
	 * Flow APIのサンプルをテストします。
	 * 
	 * @throws InterruptedException サブスクライバーの購読終了を待機している時に、割り込みが発生した場合
	 */
	@Test
	void test() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(2);
		MySubscriber subscriber1 = new MySubscriber("No.1", latch::countDown);
		MySubscriber subscriber2 = new MySubscriber("No.2", latch::countDown);
		List<Integer> items = IntStream.rangeClosed(1, 100).boxed().collect(Collectors.toList());

		try (SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>()) {
			publisher.subscribe(subscriber1);
			publisher.subscribe(subscriber2);
			items.forEach(publisher::submit);
			log("all items submitted");
		}

		latch.await();
		assertEquals(items, subscriber1.getItems());
		assertEquals(items, subscriber2.getItems());
	}

	/**
	 * コンソールにログを出力します。
	 * 
	 * @param values 出力する値
	 */
	private void log(Object... values) {
		LocalTime time = LocalTime.now();
		String message = Stream.of(values).map(Object::toString).collect(Collectors.joining(" "));
		System.out.println(String.format("[%1$tH:%1$tM:%1$tS.%1$tN] %2$s", time, message));
	}
}
