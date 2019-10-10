package org.mos.kit.unit;

import org.mos.kit.unit.AbstractTestCase.AbstractTestCaseBuilder;

public interface TestCaseRegistryWhenI<IN, TC extends AbstractTestCase<IN, TC>, OUT> {

	<TCB extends AbstractTestCaseBuilder<IN, TC, ? extends TC, ?>> OUT when(TCB tcb);
	
	OUT when(TC tc);

}
