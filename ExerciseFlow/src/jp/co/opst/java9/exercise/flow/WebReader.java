package jp.co.opst.java9.exercise.flow;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import jp.co.opst.java9.exercise.lib.exception.Resource;
import jp.co.opst.java9.exercise.lib.flow.SimplePublisher;

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
	public void read(URL url, File file) throws Exception {
		SimplePublisher<String> publisher = SimplePublisher.<String>buider()
			.add(new FileOutModel<>(file))
			.add(new SystemOutModel<>())
			.build();

		try (publisher) {
			Resource.of(url::openStream)
				.map(InputStreamReader::new)
				.map(BufferedReader::new)
				.whilePresent(BufferedReader::readLine)
				.accept(publisher::publish);
		}

		publisher.await();
	}
}
