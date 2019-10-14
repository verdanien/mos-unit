package org.mos.kit.unit;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;
import org.mos.kit.unit.args.TestCaseToString;
import org.mos.kit.unit.expectation.Expectation;
import org.mos.kit.unit.expectation.ExpectedException;
import org.mos.kit.unit.expectation.ExpectedMatcher;
import org.mos.kit.unit.expectation.ExpectedValue;

public final class TestCasesRegistry<T, TC extends AbstractTestCase<T, TC>> implements
//@formatter:off
		TestCaseRegistryExpectsI<T, TestCasesRegistry<T, TC>.TestCaseRegistryWhen>, 
		TestCaseRegistryWhenI<T, TC, TestCasesRegistry<T, TC>.TestCaseRegistryExpects>
//@formatter:on
{
	private static final int THREAD_STACK_NESTING_LEVEL = 2;
	private static final int STACK_METHOD = 2;
	private final List<TC> testCases;
	private final Supplier<TC> tcFactory;
	private TestCaseToString<TC> printer;

	public TestCasesRegistry(Supplier<TC> tcFactory) {
		this.tcFactory = Objects.requireNonNull(tcFactory);
		testCases = new LinkedList<>();
		create().createParams(this);
	}

	public List<TC> getTestCases() {
		return Collections.unmodifiableList(testCases);
	}
	
	protected TC create() {
		return tcFactory.get();
	}

	private void registerTestCase(TC tc, StackTraceElement stackElement) {
		if (printer == null) {
			printer = new TestCaseToString<>(tc.getClass());
		}
		tc.register(this, stackElement);
		testCases.add(tc);
	}

	String printArgs(TC instance) {
		return printer.toString(instance);
	}

	String printValue(TC instance, Object value) {
		return printer.toString(instance, value);
	}

	// FLUENT-CHAIN BUILDER METHODS
	public TC when() {
		TC tc = create();
		when(STACK_METHOD, tc);
		return tc;
	}
	
	@Override
	public TestCaseRegistryExpects when(TC tc) {
		return when(STACK_METHOD, tc);
	}

	@Override
	public TestCaseRegistryWhen expects(T value) {
		return expects(STACK_METHOD, new ExpectedValue<>(value));
	}

	@Override
	public TestCasesRegistry<T, TC>.TestCaseRegistryWhen expects(T value, Matcher<T> matcher) {
		return expects(STACK_METHOD, new ExpectedMatcher<>(matcher, it -> it.apply(value)));
	}

	@Override
	public TestCaseRegistryWhen expects(Matcher<T> matcher, String message) {
		return expects(STACK_METHOD, new ExpectedMatcher<>(matcher, message));
	}

	@Override
	public TestCasesRegistry<T, TC>.TestCaseRegistryWhen expects(Matcher<T> matcher, Function<Function<Object, String>, String> message) {
		return expects(STACK_METHOD, new ExpectedMatcher<>(matcher, message));
	}

	@Override
	public TestCaseRegistryWhen expectsExpectation(Expectation<T> expectation) {
		return expects(STACK_METHOD, expectation);
	}

	@Override
	public TestCasesRegistry<T, TC>.TestCaseRegistryWhen expectsException(Class<? extends Throwable> exceptionType) {
		return expects(STACK_METHOD, new ExpectedException<>(exceptionType));
	}

	private TestCaseRegistryExpects when(int stack, TC tc) {
		return new TestCaseRegistryExpects(getStack(stack), tc);
	}

	private TestCaseRegistryWhen expects(int stack, Expectation<T> expectation) {
		return new TestCaseRegistryWhen(getStack(stack), expectation);
	}

	private StackTraceElement getStack(int pos) {
		return Thread.currentThread().getStackTrace()[THREAD_STACK_NESTING_LEVEL + pos];
	}

	public final class TestCaseRegistryExpects extends AbstractTestCaseRegistryFlow implements TestCaseRegistryExpectsI<T, TC> {
		private final TC tc;

		private TestCaseRegistryExpects(StackTraceElement stackElement, TC tc) {
			super(stackElement);
			this.tc = tc;
			tc.setWhen(this);
		}

		@Override
		public TC expects(T value) {
			return expects(value, IsEqual.equalTo(value));
		}

		@Override
		public TC expects(T value, Matcher<T> matcher) {
			return expects(matcher, it -> it.apply(value));
		}

		@Override
		public TC expects(Matcher<T> matcher, String message) {
			return expectsExpectation(new ExpectedMatcher<>(matcher, message));
		}

		@Override
		public TC expects(Matcher<T> matcher, Function<Function<Object, String>, String> message) {
			return expectsExpectation(new ExpectedMatcher<>(matcher, message));
		}

		@Override
		public TC expectsException(Class<? extends Throwable> exceptionType) {
			return expectsExpectation(new ExpectedException<>(exceptionType));
		}

		@Override
		public TC expectsExpectation(Expectation<T> expectation) {
			return new TestCaseRegistryWhen(getStackElement(), expectation).when(tc);
		}

	}

	public final class TestCaseRegistryWhen extends AbstractTestCaseRegistryFlow implements TestCaseRegistryWhenI<T, TC, TC> {
		private final Expectation<T> expectation;

		private TestCaseRegistryWhen(StackTraceElement stackElement, Expectation<T> expectation) {
			super(stackElement);
			this.expectation = expectation;
		}

		@Override
		public TC when(TC tc) {
			tc.setExpectation(expectation);
			registerTestCase(tc, getStackElement());
			return tc;
		}

	}

}