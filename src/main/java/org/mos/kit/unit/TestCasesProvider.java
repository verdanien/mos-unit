package org.mos.kit.unit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.runners.model.FrameworkMethod;

import junitparams.custom.ParametersProvider;

public class TestCasesProvider implements ParametersProvider<TestCases> {

	private TestCases testCases;

	@Override
	public void initialize(TestCases testCases, FrameworkMethod frameworkMethod) {
		this.testCases = testCases;

		// get other annotations
	}

	@Override
	public Object[] getParameters() {
		@SuppressWarnings("rawtypes")
		Class tc = testCases.value();
		@SuppressWarnings("unchecked")
		TestCasesRegistry<?, ?> tcRegistry = extracted(tc);
		return tcRegistry.getTestCases().stream().toArray();
	}

	private <T, TC extends AbstractTestCase<T, TC>> TestCasesRegistry<T, TC> extracted(Class<? extends TC> tc) {
		return new TestCasesRegistry<>(() -> createNewInstance(tc));
	}

	private <T, TC extends AbstractTestCase<T, TC>> TC createNewInstance(Class<? extends TC> tc) {
		try {
			Constructor<TC> defaultConstructor = getDefaultConstructorOrException(tc);
			return defaultConstructor.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(String.format("Cannot create the new instance of class [%s]", tc), e);
		}
	}

	private <T, TC extends AbstractTestCase<T, TC>> Constructor<TC> getDefaultConstructorOrException(
			Class<? extends TC> tc) {
		Constructor<?>[] constructors = tc.getDeclaredConstructors();
		if (constructors.length != 1) {
			throw new NullPointerException(String.format("Default constructor not exists for class [%s]", tc));
		}
		@SuppressWarnings("unchecked")
		Constructor<TC> defaultConstructor = (Constructor<TC>) constructors[0];
		defaultConstructor.setAccessible(true);
		return defaultConstructor;
	}

}
