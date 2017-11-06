package jp.co.opst.java9.mod.exp2;

import java.util.HashSet;
import java.util.Set;

import jp.co.opst.java9.mod.exp2.core.Identified;

public class Container<I, V> {

	private final Set<Identified<I, V>> set = new HashSet<>();

	public V get(I id) {
		return set.stream()
				.filter(e -> e.getId().equals(id))
				.findAny()
				.map(Identified::getValue)
				.orElse(null);
	}

	public Container<I, V> set(I id, V value) {
		set.add(new Identified<>(id, value));
		return this;
	}
}
