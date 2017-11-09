package jp.co.opst.java9.mod.exp3.core;

import java.util.Objects;

import jp.co.opst.java9.mod.exp1.Identifier;

public class Identified<I, V> implements Identifier<I> {

	private final I id;

	private V value;

	public Identified(I id) {
		this.id = Objects.requireNonNull(id, "id");
	}

	public Identified(I id, V value) {
		this(id);
		setValue(value);
	}

	@Override
	public final I getId() {
		return id;
	}

	public final V getValue() {
		return value;
	}

	public final void setValue(V value) {
		this.value = value;
	}

	@Override
	public final int hashCode() {
		return id.hashCode();
	}

	@Override
	public final boolean equals(Object object) {
		if (object == null) {
			return false;
		}

		if (object == this) {
			return true;
		}

		if (object instanceof Identified) {
			Identified<?, ?> other = (Identified<?, ?>) object;
			return other.canEqual(this) && id.equals(other.id);
		}

		return false;
	}

	protected boolean canEqual(Identified<?, ?> other) {
		return getClass().equals(other.getClass());
	}
}
