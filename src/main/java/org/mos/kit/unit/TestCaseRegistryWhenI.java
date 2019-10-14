package org.mos.kit.unit;

public interface TestCaseRegistryWhenI<IN, TC extends AbstractTestCase<IN, TC>, OUT> {

	OUT when(TC tc);

}
