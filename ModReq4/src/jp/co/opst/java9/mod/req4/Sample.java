package jp.co.opst.java9.mod.req4;

import java.util.ServiceLoader;

import jp.co.opst.java9.mod.exp4.NameService;

public class Sample {

	public static void main(String[] args) {
		ServiceLoader.load(NameService.class)
			.stream()
			.parallel()
			.map(ServiceLoader.Provider::get)
			.map(NameService::getName)
			.forEach(System.out::println);
	}
}
