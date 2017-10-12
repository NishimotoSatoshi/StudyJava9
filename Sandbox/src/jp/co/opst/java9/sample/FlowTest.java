package jp.co.opst.java9.sample;

import java.time.LocalTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Flow API �̉��K�ł��B
 */
public class FlowTest {

	/**
	 * ����̃T�u�X�N���C�o�[�B
	 */
	public class MySubscriber implements Flow.Subscriber<Integer> {

		/** ���O�B */
		private final String name;

		/** �I���V�O�i���̑����B */
		private final CountDownLatch doneSignal;

		/** �w�ǑΏہB */
		private Flow.Subscription subscription;

		/**
		 * �R���X�g���N�^�[�B
		 * 
		 * @param name ���O
		 * @param doneSignal �I���V�O�i���̑����
		 */
		public MySubscriber(String name, CountDownLatch doneSignal) {
			this.name = name;
			this.doneSignal = doneSignal;
		}

		/**
		 * �w�ǊJ�n���̏����B
		 * 
		 * @param subscription �w�ǑΏ�
		 */
		@Override
		public void onSubscribe(Flow.Subscription subscription) {
			log(name, "onSubscribe");
			this.subscription = subscription;
			subscription.request(1);
		}

		/**
		 * �A�C�e����M���̏����B
		 * 
		 * @param item �A�C�e��
		 */
		@Override
		public void onNext(Integer item) {
			log(name, "onNext", item);
			subscription.request(1);
		}

		/**
		 * ��O�������̏����B
		 * 
		 * @param throwable ����������O
		 */
		@Override
		public void onError(Throwable throwable) {
			log(name, "onError", throwable);
			doneSignal.countDown();
		}

		/**
		 * �w�ǏI�����̏����B
		 */
		@Override
		public void onComplete() {
			log(name, "onComplete");
			doneSignal.countDown();
		}
	}

	/**
	 * ���C���B
	 * 
	 * @param args �g�p����Ȃ�
	 * @throws Throwable ��O�����������ꍇ
	 */
	public static void main(String[] args) throws Throwable {
		new FlowTest().test();
	}

	/**
	 * Flow API �̃T���v�������s����B
	 * 
	 * @throws InterruptedException �T�u�X�N���C�o�[�̍w�ǏI����ҋ@���Ă��鎞�ɁA���荞�݂����������ꍇ
	 */
	public void test() throws InterruptedException {
		CountDownLatch doneSignal = new CountDownLatch(2);

		try (SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>()) {
			publisher.subscribe(new MySubscriber("No.1", doneSignal));
			publisher.subscribe(new MySubscriber("No.2", doneSignal));
			IntStream.range(0, 1000).forEach(publisher::submit);
		}

		log("all items submitted");
		doneSignal.await();
	}

	/**
	 * �R���\�[���Ƀ��O���o�͂���B
	 * 
	 * @param values �o�͂���l
	 */
	private void log(Object... values) {
		LocalTime time = LocalTime.now();
		String message = Stream.of(values).map(Object::toString).collect(Collectors.joining(" "));
		System.out.println(String.format("[%1$tH:%1$tM:%1$tS.%1$tN] %2$s", time, message));
	}
}
