package jp.co.opst.java9.exercise.lib.exception;

/**
 * 例外処理のユーティリティです。
 */
public final class Try {

	/**
	 * 引数を受け取らず、結果を返さない関数。
	 */
	public interface WithoutArgAndReturn {

		/**
		 * 処理を行います。
		 * 
		 * @throws Throwable 処理中に例外が発生した場合
		 */
		public void execute() throws Throwable;
	}

	/**
	 * 引数を受け取り、結果を返さない関数。
	 * 
	 * @param <A> 引数
	 */
	public interface WithArg<A> {

		/**
		 * 処理を行います。
		 * 
		 * @param arg 引数
		 * @throws Throwable 処理中に例外が発生した場合
		 */
		public void execute(A arg) throws Throwable;
	}

	/**
	 * 引数を受け取らず、結果を返す関数。
	 * 
	 * @param <R> 結果
	 */
	public interface WithReturn<R> {

		/**
		 * 処理を行います。
		 * 
		 * @return 結果
		 * @throws Throwable 処理中に例外が発生した場合
		 */
		public R execute() throws Throwable;
	}

	/**
	 * 引数を受け取り、結果を返す関数。
	 * 
	 * @param <A> 引数
	 * @param <R> 結果
	 */
	public interface WithArgAndReturn<A, R> {

		/**
		 * 処理を行います。
		 * 
		 * @param arg 引数
		 * @return 結果
		 * @throws Throwable 処理中に例外が発生した場合
		 */
		public R execute(A arg) throws Throwable;
	}

	/**
	 * リソースを伴う例外処理です。
	 * 
	 * @param <A> リソース
	 */
	public interface WithResource<A extends AutoCloseable> {

		/**
		 * リソースを取得します。
		 * 
		 * @return リソース
		 * @throws Throwable リソースの取得に失敗した場合
		 */
		public A getResource() throws Throwable;

		/**
		 * チェック例外が発生したときは、IllegalStateExceptionでラッピングします。
		 * 
		 * @param <R> 結果
		 * @param action 処理
		 * @return 結果
		 */
		public default <R> R uncheck(WithArgAndReturn<A, R> action) {
			try (A resource = getResource()) {
				return action.execute(resource);
			} catch (RuntimeException r) {
				throw r;
			} catch (Throwable t) {
				throw new IllegalStateException(t);
			}
		}

		/**
		 * チェック例外が発生したときは、IllegalStateExceptionでラッピングします。
		 * 
		 * @param action 処理
		 */
		public default void uncheck(WithArg<A> action) {
			try (A resource = getResource()) {
				action.execute(resource);
			} catch (RuntimeException r) {
				throw r;
			} catch (Throwable t) {
				throw new IllegalStateException(t);
			}
		}

		/**
		 * 例外を無視します。
		 * 
		 * @param <R> 結果
		 * @param action 処理
		 * @return 結果 （例外が発生した場合はnull）
		 */
		public default <R> R ignore(WithArgAndReturn<A, R> action) {
			try (A resource = getResource()) {
				return action.execute(resource);
			} catch (Throwable t) {
				return null;
			}
		}

		/**
		 * 例外を無視します。
		 * 
		 * @param action 処理
		 */
		public default void ignore(WithArg<A> action) {
			try (A resource = getResource()) {
				action.execute(resource);
			} catch (Throwable t) {
			}
		}
	}

	/**
	 * チェック例外が発生したときは、IllegalStateExceptionでラッピングします。
	 * 
	 * @param <R> 結果
	 * @param action 処理
	 * @return 結果
	 */
	public static <R> R uncheck(WithReturn<R> action) {
		try {
			return action.execute();
		} catch (RuntimeException r) {
			throw r;
		} catch (Throwable t) {
			throw new IllegalStateException(t);
		}
	}

	/**
	 * チェック例外が発生したときは、IllegalStateExceptionでラッピングします。
	 * 
	 * @param action 処理
	 */
	public static void uncheck(WithoutArgAndReturn action) {
		try {
			action.execute();
		} catch (RuntimeException r) {
			throw r;
		} catch (Throwable t) {
			throw new IllegalStateException(t);
		}
	}

	/**
	 * 例外を無視します。
	 * 
	 * @param <R> 結果
	 * @param action 処理
	 * @return 結果 （例外が発生した場合はnull）
	 */
	public static <R> R ignore(WithReturn<R> action) {
		try {
			return action.execute();
		} catch (Throwable t) {
			return null;
		}
	}

	/**
	 * 例外を無視します。
	 * 
	 * @param action 処理
	 */
	public static void ignore(WithoutArgAndReturn action) {
		try {
			action.execute();
		} catch (Throwable t) {
		}
	}

	/**
	 * リソースを伴う例外処理を行います。
	 * 
	 * @param <A> リソース
	 * @param resourceSupplier リソースを取得する関数
	 * @return リソースを伴う例外処理
	 */
	public static <A extends AutoCloseable> WithResource<A> withResource(WithResource<A> resourceSupplier) {
		return resourceSupplier;
	}

	private Try() {
	}
}
