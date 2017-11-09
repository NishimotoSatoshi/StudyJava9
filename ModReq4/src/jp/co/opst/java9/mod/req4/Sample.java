package jp.co.opst.java9.mod.req4;

import java.util.ServiceLoader;

import jp.co.opst.java9.mod.exp4.NameServive;

public class Sample {

	public static void main(String[] args) {
		ServiceLoader.load(NameServive.class)
			.stream()
			.map(ServiceLoader.Provider::get)
			.map(NameServive::getName)
			.forEach(System.out::println);
	}
}
