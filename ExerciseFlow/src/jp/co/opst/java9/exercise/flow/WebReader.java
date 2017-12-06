package jp.co.opst.java9.exercise.flow;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import jp.co.opst.java9.exercise.lib.flow.SimplePublisher;
import jp.co.opst.java9.exercise.lib.function.Processor;

/**
 * Flow APIを使って、ICP/IP通信をした結果を、標準出力とファイルに書き込む演習です。
 */
public class WebReader {

	/**
	 * メイン。
	 * 
	 * @param args 使用しません
	 */
	public static void main(String[] args) {
		WebReader me = new WebReader();

		try {
			me.read(new URL("http://example.com/"), new File("response.txt"));
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * ICP/IP通信をした結果を、標準出力とファイルに書き込みます。
	 * 
	 * @param url 通信先のURL
	 * @param file 書き込み先のファイル
	 * @throws IOException ICP/IP通信に失敗した場合
	 * @throws InterruptedException サブスクライバーの終了待機中に、割り込みが発生した場合
	 */
	public void read(URL url, File file) throws IOException, InterruptedException {
		SimplePublisher<String> publisher = SimplePublisher.<String>buider()
			.add(new FileOutModel<>(file))
			.add(new SystemOutModel<>())
			.build();

		try (publisher) {
			try (InputStream in = url.openStream()) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				Processor.of(reader::readLine).loopWhilePresent(publisher::publish).rethrow();
			}
		}

		publisher.await();
	}
}
