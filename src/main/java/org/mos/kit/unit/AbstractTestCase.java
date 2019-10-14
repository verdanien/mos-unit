package org.mos.kit.unit;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Assume;
import org.mos.kit.unit.args.TestCaseParamIgnore;
import org.mos.kit.unit.expectation.Expectation;
import org.mos.kit.unit.expectation.UnitResult;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true, fluent=true)
@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractTestCase<T, TC extends AbstractTestCase<T, TC>> {
	public static final String TEST_CASE_NAME = "{0}";
	@TestCaseParamIgnore
	private Expectation<T> expectation;
	@TestCaseParamIgnore
	private String name;
	@TestCaseParamIgnore
	private String ignore;
	@TestCaseParamIgnore
	private int index;
	@TestCaseParamIgnore
	private TestCasesRegistry<T, TC> testCasesRegistry;
	@TestCaseParamIgnore
	private StackTraceElement stackElement;
	@TestCaseParamIgnore
	private TestCasesRegistry<T, TC>.TestCaseRegistryExpects when;

	void setExpectation(Expectation<T> expectation) {
		this.expectation = Objects.requireNonNull(expectation);
	}

	void setIndex(int index) {
		this.index = index;
	}

	void register(TestCasesRegistry<T, TC> testCasesRegistry, StackTraceElement stackElement) {
		this.stackElement = stackElement;
		this.testCasesRegistry = testCasesRegistry;
		index = getTotalCount();
	}

	public Expectation<T> getExpectation() {
		return expectation;
	}

	public int getIndex() {
		return index;
	}

	public TC name(String name) {
		this.name = name;
		return getThis();
	}

	public TC ignore(String reason) {
		this.ignore = reason;
		return getThis();
	}

	@SuppressWarnings("unchecked")
	private TC getThis() {
		return (TC) this;
	}

	private int getTotalCount() {
		return testCasesRegistry.getTestCases().size();
	}

	public void executeVerify(Supplier<T> action) {
		getExpectation().assertThat(execute(action));
	}

	public UnitResult<T> execute(Supplier<T> action) {
		Assume.assumeFalse(ignore, isIgnored());
		UnitResult<T> result;
		try {
			result = new UnitResult<T>(action.get());
		} catch (Exception e) {
			result = new UnitResult<T>(e);
		}
		return result;
	}

	public boolean isIgnored() {
		return ignore != null;
	}

	protected abstract void createParams(TestCasesRegistry<T, TC> tc);

	@Override
	public String toString() {
		String format = String.format("[%%%dd]:[%%s] : %%s", String.valueOf(getTotalCount()).length());
		return String.format(format, index, getLocation(), printName());
	}

	private String getLocation() {
		return String.valueOf(stackElement.getLineNumber());
	}

	private String printName() {
		return name == null ? String.format("expects[%s] for[%s]", printExpectation(), printArgs()) : name;
	}

	private String printArgs() {
		return testCasesRegistry.printArgs(getThis());
	}

	private String printExpectation() {
		Function<Object, String> fn = value -> testCasesRegistry.printValue(getThis(), value);
		return getExpectation().print(fn);
	}

	@SuppressWarnings("unchecked")
	protected TC create() {
		try {
			return (TC) getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public TestCasesRegistry<T, TC>.TestCaseRegistryExpects then() {
		return when;
	}

	void setWhen(TestCasesRegistry<T, TC>.TestCaseRegistryExpects when) {
		this.when = when;
	}

}
