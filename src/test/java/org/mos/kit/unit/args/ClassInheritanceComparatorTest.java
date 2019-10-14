package org.mos.kit.unit.args;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mos.kit.unit.AbstractTestCase;
import org.mos.kit.unit.TestCases;
import org.mos.kit.unit.TestCasesRegistry;

import junitparams.JUnitParamsRunner;
import junitparams.naming.TestCaseName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@RunWith(JUnitParamsRunner.class)
public class ClassInheritanceComparatorTest {
	private static final int EQUALS = 0;

	private ClassInheritanceComparator comparator;

	@Before
	public void beforeTest() {
		comparator = new ClassInheritanceComparator();
	}

	@Test
	@TestCases(CompareTc.class)
	@TestCaseName(CompareTc.TEST_CASE_NAME)
	public void testCompare(CompareTc tc) throws Exception {
		tc.executeVerify(() -> comparator.compare(tc.clsA(), tc.clsB()));
	}

	@Accessors(fluent = true, chain = true)
	@Getter
	@Setter
	private static class CompareTc extends AbstractTestCase<Integer, CompareTc> {
		private Class<?> clsA;
		private Class<?> clsB;

		@Override
		protected void createParams(TestCasesRegistry<Integer, CompareTc> tc) {
			tc.when().then().expects(EQUALS);
			tc.when().clsA(Child.class).then().expects(1);
			tc.when().clsB(Child.class).then().expects(-1);
			tc.when().clsA(Child.class).clsB(Child.class).then().expects(EQUALS);
			tc.when().clsA(Child.class).clsB(Parent.class).then().expects(1);
			tc.when().clsA(Child.class).clsB(GrandParent.class).then().expects(2);
			tc.when().clsA(Parent.class).clsB(GrandParent.class).then().expects(1);
			tc.when().clsA(GrandParent.class).clsB(Child.class).then().expects(-2);
			tc.when().clsA(GrandParent.class).clsB(Parent.class).then().expects(-1);
			
			tc.when().clsA(Child.class).clsB(ChildOther.class).then().expects(0); //?
			tc.when().clsA(Child.class).clsB(ParentOther.class).then().expects(1); //?
			tc.when().clsA(Child.class).clsB(Other.class).then().expects(1); //?
			tc.when().clsA(ChildOther.class).clsB(Other.class).then().expects(1); //?
			tc.when().clsA(Parent.class).clsB(Other.class).then().expects(1); //?
			tc.when().clsA(GrandParent.class).clsB(Other.class).then().expects(1); //?
			tc.when().clsA(OtherB.class).clsB(Other.class).then().expects(1); //?
			
		}
	}

	private static class Child extends Parent {
	}
	private static class ChildOther extends Parent {
	}

	private static class Parent extends GrandParent {
	}
	private static class ParentOther extends GrandParent {
		
	}

	private static class GrandParent {
	}
	
	private static class Other {
		
	}
	private static class OtherB {
		
	}
}