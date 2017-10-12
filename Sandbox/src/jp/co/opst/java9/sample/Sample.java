package jp.co.opst.java9.sample;

import jp.co.opst.java9.util.Identifier;

public class Sample {

	public static void main(String[] args) {
		Identifier<Integer> identifier = new Identifier<>() {
			@Override
			public Integer getId() {
				return 1;
			}
		};

		System.out.println(identifier.getId());
	}
}
