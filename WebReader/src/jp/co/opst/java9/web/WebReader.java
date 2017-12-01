package jp.co.opst.java9.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLConnection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Consumer;

import jp.co.opst.java9.lib.flow.FileOutSubscriber;
import jp.co.opst.java9.lib.flow.SystemOutSubscriber;
import jp.co.opst.java9.lib.function.Processor;

public class WebReader {

	public static void main(String[] args) {
		WebReader me = new WebReader();

		try {
			URI uri = URI.create("http://example.com/");
			File file = new File("response.txt");
			me.read(uri, file);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void read(URI uri, File file) throws IOException,  InterruptedException {
		CountDownLatch latch = new CountDownLatch(2);

		try (SubmissionPublisher<String> publisher = new SubmissionPublisher<>()) {
			publisher.subscribe(new SystemOutSubscriber<>(latch::countDown));
			publisher.subscribe(new FileOutSubscriber<>(file, latch::countDown));
			read(uri, publisher::submit);
		}

		latch.await();
	}

	private void read(URI uri, Consumer<? super String> acceptor) throws IOException {
		URLConnection connection = uri.toURL().openConnection();
		connection.setConnectTimeout(3000);
		connection.setReadTimeout(3000);

		try (InputStream in = connection.getInputStream()) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			Processor.of(reader::readLine).loopWhilePresent(acceptor);
		}
	}
}
