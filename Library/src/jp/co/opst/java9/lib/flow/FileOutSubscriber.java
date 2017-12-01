package jp.co.opst.java9.lib.flow;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * 購読した値をファイルに出力するサブスクライバーです。
 * 
 * @param <T> 購読する型
 */
public class FileOutSubscriber<T> extends SubscriberBase<T> {

	/** 出力先のファイル。 */
	private final File file;

	/** 終了シグナル。 */
	private final Runnable doneSignal;

	/** 出力ストリーム。 */
	private OutputStream out;

	/** ライター。 */
	private BufferedWriter writer;

	/**
	 * コンストラクター。
	 * 
	 * @param file 出力先のファイル
	 * @param doneSignal 終了シグナル
	 */
	public FileOutSubscriber(File file, Runnable doneSignal) {
		super(1);
		this.file = file;
		this.doneSignal = doneSignal;
	}

	/**
	 * コンストラクター。
	 * 
	 * <p>
	 * このコンストラクターは、終了シグナルを実行しません。
	 * </p>
	 * 
	 * @param file 出力先のファイル
	 */
	public FileOutSubscriber(File file) {
		this(file, () -> {});
	}

	/**
	 * 購読開始時の処理を行います。
	 * 
	 * <p>
	 * 出力先のファイルに対する出力ストリームおよびライターを生成します。
	 * </p>
	 * 
	 * @throws IOException 出力ストリームの生成に失敗した場合
	 */
	@Override
	protected void begin() throws IOException {
		out = new FileOutputStream(file);
		writer = new BufferedWriter(new OutputStreamWriter(out));
	}

	/**
	 * 購読時の処理を行います。
	 * 
	 * <p>
	 * 購読した値をファイルに書き込みます。
	 * </p>
	 * 
	 * @param value 購読した値
	 * @throws IOException 書き込みに失敗した場合
	 */
	@Override
	protected void accept(T value) throws IOException {
		writer.write(value.toString());
		writer.newLine();
	}

	/**
	 * 例外時の処理を行います。
	 * 
	 * <p>
	 * 標準エラーにスタックトレースを出力します。
	 * </p>
	 * 
	 * @param error 発生した例外
	 */
	@Override
	protected void error(Throwable error) {
		error.printStackTrace();
	}

	/**
	 * 購読完了時の処理を行います。
	 * 
	 * <p>
	 * 出力ストリームをクローズして、終了シグナルを実行します。
	 * </p>
	 */
	@Override
	protected void done() {
		if (writer != null) {
			try {
				writer.flush();
			} catch (Exception e) {
			}
		}

		if (out != null) {
			try {
				out.close();
			} catch (Exception e) {
			}
		}

		doneSignal.run();
	}
}
