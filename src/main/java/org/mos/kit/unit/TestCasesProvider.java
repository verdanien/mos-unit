package org.mos.kit.unit;

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
		TestCasesRegistry<T, TC> tcRegistry = new TestCasesRegistry<>();
		try {
			TC instance = tc.newInstance();
			instance.createParams(tcRegistry);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return tcRegistry;
	}

}
