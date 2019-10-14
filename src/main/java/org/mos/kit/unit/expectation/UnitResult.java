package org.mos.kit.unit.expectation;

import java.util.Objects;

import lombok.ToString;

@ToString
public class UnitResult<T> {
	private final T value;
	private final Throwable exception;

	public UnitResult(Throwable exception) {
		this(null, Objects.requireNonNull(exception));
	}

	public UnitResult(T value) {
		this(value, null);
	}

	private UnitResult(T value, Throwable exception) {
		this.value = value;
		this.exception = exception;
	}

	public T getValue() {
		return value;
	}

	public Throwable getException() {
		return exception;
	}

	public boolean isException() {
		return exception != null;
	}

}
