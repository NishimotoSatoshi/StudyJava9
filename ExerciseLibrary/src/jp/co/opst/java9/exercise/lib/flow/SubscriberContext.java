package jp.co.opst.java9.exercise.lib.flow;

import java.util.function.Consumer;

/**
 * サブスクライバーのコンテキストです。
 */
public class SubscriberContext {

	/** リクエスト要求数。 */
	private int demand;

	/** エラーハンドラー。 */
	private Consumer<Throwable> errorHandler;

	/** 終了シグナル。 */
	private Runnable doneSignal;

	/**
	 * リクエスト要求数を取得します。
	 * 
	 * @return リクエスト要求数
	 */
	public int getDemand() {
		return demand;
	}

	/**
	 * リクエスト要求数を設定します。
	 * 
	 * @param demand リクエスト要求数
	 */
	public void setDemand(int demand) {
		this.demand = demand;
	}

	/**
	 * エラーハンドラーを取得します。
	 * 
	 * @return エラーハンドラー
	 */
	public Consumer<Throwable> getErrorHandler() {
		return errorHandler;
	}

	/**
	 * エラーハンドラーを設定します。
	 * 
	 * @param errorHandler エラーハンドラー
	 */
	public void setErrorHandler(Consumer<Throwable> errorHandler) {
		this.errorHandler = errorHandler;
	}

	/**
	 * 終了シグナルを取得します。
	 * 
	 * @return 終了シグナル
	 */
	public Runnable getDoneSignal() {
		return doneSignal;
	}

	/**
	 * 終了シグナルを設定します。
	 * 
	 * @param doneSignal 終了シグナル
	 */
	public void setDoneSignal(Runnable doneSignal) {
		this.doneSignal = doneSignal;
	}
}
