package jp.co.opst.java9.mod.req1;

import jp.co.opst.java9.mod.exp1.Identifier;

public class Sample {

	public static void main(String[] args) {
		Identifier<Integer> identifier = () -> 100;
		System.out.println(identifier.getId());
	}
}
