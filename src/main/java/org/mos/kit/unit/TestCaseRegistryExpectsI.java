package org.mos.kit.unit;

import java.util.function.Function;

import org.hamcrest.Matcher;
import org.mos.kit.unit.expectation.Expectation;

public interface TestCaseRegistryExpectsI<IN, OUT> {

	OUT expectsException(Class<? extends Throwable> exceptionType);

	OUT expects(IN value);

	OUT expects(IN value, Matcher<IN> matcher);

	OUT expectsExpectation(Expectation<IN> expectation);

	OUT expects(Matcher<IN> matcher, String message);

	OUT expects(Matcher<IN> matcher, Function<Function<Object, String>, String> message);

}
