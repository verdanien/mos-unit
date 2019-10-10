package org.mos.kit.unit.expectation;

import java.util.function.Function;

public interface Expectation<T> {
	
	void assertThat(UnitResult<T> unitResult);

	String print(Function<Object, String> fn);

}
