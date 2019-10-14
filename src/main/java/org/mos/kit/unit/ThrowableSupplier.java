package org.mos.kit.unit;

import java.util.function.Supplier;

@FunctionalInterface
interface ThrowableSupplier<T> extends Supplier<T> {

	@Override
	default T get() {
		try {
			return getThrowable();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	T getThrowable() throws Exception;
}
