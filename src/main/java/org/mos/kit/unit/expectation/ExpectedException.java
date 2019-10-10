package org.mos.kit.unit.expectation;

import java.util.function.Function;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Assert;

public class ExpectedException<T> implements Expectation<T> {

	private static final String AN_EXCEPTION_S_IS_THROWN = "An Exception [%s] is thrown";
	private final Matcher<Throwable> matcher;
	private final String name;

	public ExpectedException(Class<? extends Throwable> type) {
		this(type.getName(), CoreMatchers.instanceOf(type));
	}

	public ExpectedException(Matcher<Throwable> matcher) {
		this(null, matcher);
	}

	public ExpectedException(String name, Matcher<Throwable> matcher) {
		this.matcher = matcher;
		this.name = name;
	}

	@Override
	public void assertThat(UnitResult<T> unitResult) {
		if (unitResult.isException()) {
//			ThrowableCauseMatcher.hasCause(matcher)
//			ThrowableMessageMatcher.hasMessage(matcher)
//			StacktracePrintingMatcher<Throwable>.isThrowable(throwableMatcher)
//			CoreMatchers.allOf(first, second);
//			Assert.assertThat("reason", unitResult.getValue(), matcher);
			Assert.assertThat(formatName(), unitResult.getException(), matcher);
		} else {
			Assert.assertNotNull(formatName(), unitResult.getException());
		}
	}

	private String formatName() {
		return String.format(AN_EXCEPTION_S_IS_THROWN, name);
	}

	@Override
	public String print(Function<Object, String> fn) {
		return fn.apply(name);
	}

}
