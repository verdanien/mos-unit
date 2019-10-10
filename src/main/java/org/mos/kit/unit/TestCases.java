package org.mos.kit.unit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import junitparams.custom.CustomParameters;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@CustomParameters(provider = TestCasesProvider.class)
public @interface TestCases {

	Class<? extends AbstractTestCase<?,?>> value();
}
