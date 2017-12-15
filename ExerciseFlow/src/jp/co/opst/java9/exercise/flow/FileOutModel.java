package jp.co.opst.java9.exercise.flow;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import jp.co.opst.java9.exercise.lib.exception.Try;
import jp.co.opst.java9.exercise.lib.flow.SubscriberModel;

/**
 * 購読した値をファイルに出力するモデルです。
 * 
 * @param <T> 購読した値
 */
public class FileOutModel<T> implements SubscriberModel<T> {

	/** 出力先のファイル。 */
	private final File file;

	/** 出力ストリーム。 */
	private OutputStream out;

	/** ライター。 */
	private BufferedWriter writer;

	/**
	 * コンストラクター。
	 * 
	 * @param file 出力先のファイル
	 */
	public FileOutModel(File file) {
		this.file = file;
	}

	/**
	 * 開始時の処理を行います。
	 * 
	 * <p>
	 * 出力先のファイルに対する出力ストリームおよびライターを生成します。
	 * </p>
	 * 
	 * @throws IOException 出力ストリームの生成に失敗した場合
	 */
	@Override
	public void begin() throws IOException {
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
	 * @param item 購読した値
	 * @throws IOException 書き込みに失敗した場合
	 */
	@Override
	public void accept(T item) throws IOException {
		writer.write(item.toString());
		writer.newLine();
	}

	/**
	 * 終了時の処理を行います。
	 * 
	 * <p>
	 * 出力ストリームをクローズして、終了シグナルを実行します。
	 * </p>
	 */
	@Override
	public void end() {
		Try.of(writer).ignore(BufferedWriter::flush);
		Try.of(out).ignore(OutputStream::close);
	}
}
