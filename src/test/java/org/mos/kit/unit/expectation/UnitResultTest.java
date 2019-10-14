package org.mos.kit.unit.expectation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mos.kit.unit.AbstractTestCase;
import org.mos.kit.unit.TestCases;
import org.mos.kit.unit.TestCasesRegistry;

import junitparams.JUnitParamsRunner;
import junitparams.naming.TestCaseName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@RunWith(JUnitParamsRunner.class)
public class UnitResultTest {

	private static final String STRING_VALUE = "String";
	private static final Throwable EXCEPTION = new RuntimeException("Exception message");

	@Test
	@TestCases(GetValueTc.class)
	@TestCaseName(GetValueTc.TEST_CASE_NAME)
	public void testGetValue(GetValueTc tc) throws Exception {
		tc.executeVerify(() -> tc.unitResult().getValue());
	}

	private static class GetValueTc extends AbstractTc<GetValueTc> {
		@Override
		protected void createParams(TestCasesRegistry<Object, GetValueTc> tc) {
			tc.when().unitResult(new UnitResult<>(STRING_VALUE)).then().expects(STRING_VALUE);
			tc.when().unitResult(new UnitResult<>(EXCEPTION)).then().expects(null);
		}
	}

	@Test
	@TestCases(GetExceptionTc.class)
	@TestCaseName(GetExceptionTc.TEST_CASE_NAME)
	public void testGetException(GetExceptionTc tc) throws Exception {
		tc.executeVerify(() -> tc.unitResult().getException());
	}

	private static class GetExceptionTc extends AbstractTc<GetExceptionTc> {
		@Override
		protected void createParams(TestCasesRegistry<Object, GetExceptionTc> tc) {
			tc.when().unitResult(new UnitResult<>(STRING_VALUE)).then().expects(null);
			tc.when().unitResult(new UnitResult<>(EXCEPTION)).then().expects(EXCEPTION);
		}
	}

	@Test
	@TestCases(IsExceptionTc.class)
	@TestCaseName(IsExceptionTc.TEST_CASE_NAME)
	public void testIsException(IsExceptionTc tc) throws Exception {
		tc.executeVerify(() -> tc.unitResult().isException());
	}

	private static class IsExceptionTc extends AbstractTc<IsExceptionTc> {
		@Override
		protected void createParams(TestCasesRegistry<Object, IsExceptionTc> tc) {
			tc.when().unitResult(new UnitResult<>(STRING_VALUE)).then().expects(false);
			tc.when().unitResult(new UnitResult<>(EXCEPTION)).then().expects(true);
		}
	}

	@Accessors(chain = true, fluent = true)
	@Getter
	@Setter
	private static abstract class AbstractTc<TC extends AbstractTc<TC>> extends AbstractTestCase<Object, TC> {
		private UnitResult<?> unitResult;
	}

}