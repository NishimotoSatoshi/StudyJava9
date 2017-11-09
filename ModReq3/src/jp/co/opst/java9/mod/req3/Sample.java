package jp.co.opst.java9.mod.req3;

import jp.co.opst.java9.mod.exp1.Identifier;
import jp.co.opst.java9.mod.exp3.Container;

public class Sample {

	public static void main(String[] args) {
		Container<String, Integer> container = new Container<>();
		container.set("foo", 1);
		container.set("bar", 2);
		System.out.println(container.get("foo"));
		System.out.println(container.get("bar"));
		System.out.println(container.get("baz"));

		Identifier<Integer> identifier = () -> 100;
		System.out.println(identifier.getId());
	}
}
