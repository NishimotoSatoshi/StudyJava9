package jp.co.opst.java9.sample;

import java.time.LocalTime;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FlowTest {

	public class MySubscriber implements Flow.Subscriber<Integer> {

		private final String name;
		private Flow.Subscription subscription;

		public MySubscriber(String name) {
			this.name = name;
		}

		@Override
		public void onSubscribe(Flow.Subscription subscription) {
			log(name, "onSubscribe");
			this.subscription = subscription;
			subscription.request(1);
		}

		@Override
		public void onNext(Integer item) {
			log(name, "onNext", item);
			subscription.request(1);
		}

		@Override
		public void onError(Throwable throwable) {
			log(name, "onError", throwable.getMessage());
		}

		@Override
		public void onComplete() {
			log(name, "onComplete");
		}
	}

	public static void main(String[] args) throws Throwable {
		new FlowTest().test();
		Thread.sleep(1000);
	}

	public void test() {
		log("begin test");

		try (SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>()) {
			publisher.subscribe(new MySubscriber("No.1"));
			publisher.subscribe(new MySubscriber("No.2"));
			IntStream.range(0, 10000).forEach(publisher::submit);
		}

		log("end test");
	}

	private void log(Object... values) {
		LocalTime time = LocalTime.now();
		String message = Stream.of(values).map(Object::toString).collect(Collectors.joining(" "));
		System.out.println(String.format("[%1$tH:%1$tM:%1$tS.%1$tN] %2$s", time, message));
	}
}
