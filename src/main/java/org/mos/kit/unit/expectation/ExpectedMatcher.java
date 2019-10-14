package org.mos.kit.unit.expectation;

import java.util.function.Function;

import org.hamcrest.Matcher;
import org.junit.Assert;

public class ExpectedMatcher<T> implements Expectation<T> {

	private final Matcher<T> matcher;
	private final Function<Function<Object, String>, String> message;

	public ExpectedMatcher(Matcher<T> matcher, String message) {
		this(matcher, it -> it.apply(message));
	}

	public ExpectedMatcher(Matcher<T> matcher, Function<Function<Object, String>, String> message) {
		this.matcher = matcher;
		this.message = message;
	}

	@Override
	public void assertThat(UnitResult<T> unitResult) {
		if (unitResult.isException()) {
			unitResult.getException().printStackTrace();
			Assert.assertNull("An Exception is Thrown:", unitResult.getException());
		} else {
			Assert.assertThat("reason", unitResult.getValue(), matcher);
		}
	}

	@Override
	public String print(Function<Object, String> fn) {
		return message.apply(fn);
	}

}
