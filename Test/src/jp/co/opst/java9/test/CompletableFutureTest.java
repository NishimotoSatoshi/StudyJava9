package jp.co.opst.java9.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

/**
 * CompletableFutureの新メソッドを試します。
 * 
 * @see CompletableFuture
 */
public class CompletableFutureTest {

	/** テスト用一時フォルダ。 */
	public final TemporaryFolder tempFolder = new TemporaryFolder();

	/** trueの時は、実際にはファイルは存在しない。 */
	public boolean fileNotFound;

	/** trueの時は、読み込みを遅延させる。 */
	public boolean delayedReadingFile;

	/**
	 * 前処理。
	 * 
	 * @throws Throwable 前処理に失敗した場合
	 */
	@BeforeEach
	void setUp() throws Throwable {
		tempFolder.create();
		fileNotFound = false;
		delayedReadingFile = false;
	}

	/**
	 * 後処理。
	 */
	@AfterEach
	void tearDown() {
		tempFolder.delete();
	}

	/**
	 * 正常時。
	 * 
	 * @throws Throwable テストに失敗した場合
	 */
	@Test
	void testNormal() throws Throwable {
		assertEquals(100, executeCompletableFuture().get().intValue());
	}

	/**
	 * ファイルが存在しない場合。
	 * 
	 * @throws Throwable テストに失敗した場合
	 */
	@Test
	void testWhenFileNotFound() throws Throwable {
		fileNotFound = true;
		ExecutionException exception = assertThrows(ExecutionException.class, () -> executeCompletableFuture().get());
		assertTrue(Stream.iterate(exception, Objects::nonNull, Throwable::getCause).anyMatch(e -> e instanceof FileNotFoundException));
	}

	/**
	 * 処理がタイムアウトした場合。
	 * 
	 * @throws Throwable テストに失敗した場合
	 */
	@Test
	void testWhenTimeout() throws Throwable {
		delayedReadingFile = true;
		assertEquals(0, executeCompletableFuture().get().intValue());
	}

	/**
	 * 非同期処理を行う。
	 * 
	 * <p>
	 * 以下の処置を行う。
	 * </P>
	 * 
	 * <ol>
	 * <li>{@link #getFile()}</li>
	 * <li>{@link #readFirstLine(File)}</li>
	 * <li>{@link Integer#parseInt(String)}</li>
	 * </ol>
	 * 
	 * <p>
	 * また、処理全体で3秒以上掛かった場合は、処理をタイムアウトさせて、結果を0にする。
	 * </p>
	 * 
	 * @return 処理結果
	 */
	private CompletableFuture<Integer> executeCompletableFuture() {
		return CompletableFuture
			.supplyAsync(this::getFile)
			.thenApplyAsync(this::readFirstLine)
			.thenApplyAsync(Integer::parseInt)
			.completeOnTimeout(0, 3, TimeUnit.SECONDS);
	}

	/**
	 * 数値が書き込まれているファイルを取得する。
	 * 
	 * <p>
	 * {@link #fileNotFound} がtrueの時は、実際にはファイルは存在しない。
	 * </p>
	 * 
	 * @return 書き込んだファイル
	 */
	private File getFile() {
		if (fileNotFound) {
			return new File("dummy.txt");
		}

		try {
			File file = tempFolder.newFile();

			try (FileWriter writer = new FileWriter(file)) {
				writer.append("100");
			}

			return file;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * ファイルの1行目を読み込む。
	 * 
	 * <p>
	 * {@link #delayedReadingFile} がtrueの時は、読み込みを10秒遅延させる。
	 * </p>
	 * 
	 * @param file 読み込むファイル
	 * @return 読み込んだ内容
	 */
	private String readFirstLine(File file) {
		try (FileReader reader = new FileReader(file)) {
			if (delayedReadingFile) {
				try {
					Thread.sleep(10_000L);
				} catch (InterruptedException e) {
				}
			}

			BufferedReader buffered = new BufferedReader(reader);
			return buffered.readLine();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
