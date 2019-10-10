package org.mos.kit.unit;

import lombok.Getter;

public class AbstractTestCaseRegistryFlow {
	private final @Getter StackTraceElement stackElement;

	public AbstractTestCaseRegistryFlow(StackTraceElement stackElement) {
		this.stackElement = stackElement;
	}

}
