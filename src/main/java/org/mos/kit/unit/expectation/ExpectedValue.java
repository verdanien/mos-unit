package org.mos.kit.unit.expectation;

import org.hamcrest.core.IsEqual;

public class ExpectedValue<T> extends ExpectedMatcher<T> {

	public ExpectedValue(T expected) {
		super(IsEqual.equalTo(expected), it -> it.apply(expected));
	}

}
