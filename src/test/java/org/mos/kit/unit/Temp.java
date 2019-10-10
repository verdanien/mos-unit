package org.mos.kit.unit;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.naming.TestCaseName;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@RunWith(JUnitParamsRunner.class)
public class Temp {

	@Test
	@TestCases(TC.class)
	@TestCaseName(TC.TEST_CASE_NAME)
	public void test(TC tc) {
		tc.executeVerify(() -> tc.a.equals(tc.b));
	}

	@NoArgsConstructor
	@SuperBuilder
	private static class TC extends AbstractTestCase<Boolean, TC> {

		private String a;
		private String b;

		@Override
		protected void createParams(TestCasesRegistry<Boolean, TC> tc) {
			tc.when(builder().a("a").b("a")).expects(true);
			tc.when(builder().a("a").b("b")).expects(false);
			tc.when(builder().a("a").b("")).expects(false);
			tc.when(builder().a("a")).expects(false);
			tc.when(builder().b("a")).expectsException(NullPointerException.class);
			tc.when(builder().a("a")).expectsException(NullPointerException.class);
			tc.when(builder().b("a")).expectsException(IllegalArgumentException.class);
		}

	}

}
