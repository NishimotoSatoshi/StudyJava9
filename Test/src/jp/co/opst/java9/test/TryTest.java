package jp.co.opst.java9.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

/**
 * try構文に関するテストです。
 */
public class TryTest {

	/** テスト用一時フォルダ。 */
	public final TemporaryFolder tempFolder = new TemporaryFolder();

	/**
	 * 前処理。
	 * 
	 * @throws Exception テスト用一時フォルダの作成に失敗した場合
	 */
	@BeforeEach
	void setUp() throws Exception {
		tempFolder.create();
	}

	/**
	 * 後処理。
	 */
	@AfterEach
	void tearDown() {
		tempFolder.delete();
	}

	/**
	 * try-with-resourcesにおいて、外部で宣言されたオブジェクトをリソースに指定できることをテストします。
	 * 
	 * @throws Exception テスト用一時ファイルの操作に失敗した場合
	 */
	@Test
	void testExternalResource() throws Exception {
		File file = tempFolder.newFile();
		FileWriter writer = new FileWriter(file);

		try (writer) {
			writer.append("OK?");
		}

		BufferedReader reader = new BufferedReader(new FileReader(file));

		try (reader) {
			assertEquals("OK?", reader.readLine());
		}
	}
}
