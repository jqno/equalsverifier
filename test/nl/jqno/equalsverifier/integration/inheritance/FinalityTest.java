/*
 * Copyright 2010, 2014 Jan Ouwens
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.jqno.equalsverifier.integration.inheritance;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import nl.jqno.equalsverifier.testhelpers.points.Point;

import org.junit.Test;

public class FinalityTest extends IntegrationTestBase {
	private static final String BOTH_FINAL_OR_NONFINAL = "Finality: equals and hashCode must both be final or both be non-final";
	private static final String SUBCLASS = "Subclass";
	private static final String SUPPLY_AN_INSTANCE = "Supply an instance of a redefined subclass using withRedefinedSubclass";
	
	@Test
	public void fail_whenEqualsIsFinalButHashCodeIsNonFinal() {
		check(FinalEqualsNonFinalHashCode.class);
	}

	@Test
	public void fail_whenEqualsIsNotFinal_givenAClassThatIsNotFinal() {
		expectFailure(SUBCLASS, "equals is not final", SUPPLY_AN_INSTANCE, "if equals cannot be final");
		EqualsVerifier.forClass(Point.class)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsIsFinalButHashCodeIsNonFinal_givenWarningIsSuppressed() {
		EqualsVerifier.forClass(FinalEqualsNonFinalHashCode.class)
				.usingGetClass()
				.suppress(Warning.STRICT_INHERITANCE)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsIsNotFinal_givenAClassThatIsNotFinalAndWarningIsSuppressed() {
		EqualsVerifier.forClass(Point.class)
				.suppress(Warning.STRICT_INHERITANCE)
				.verify();
	}
	
	@Test
	public void fail_whenEqualsIsNonFinalButHashCodeIsFinal() {
		check(NonFinalEqualsFinalHashCode.class);
	}
	
	@Test
	public void fail_whenHashCodeIsNotFinal_givenAClassThatIsNotFinalAndAnEqualsMethodThatIsFinal() {
		expectFailure(SUBCLASS, "hashCode is not final", SUPPLY_AN_INSTANCE, "if hashCode cannot be final");
		EqualsVerifier.forClass(FinalEqualsPoint.class)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsIsNonFinalButHashCodeIsFinal_givenWarningsAreSuppressed() {
		EqualsVerifier.forClass(NonFinalEqualsFinalHashCode.class)
				.usingGetClass()
				.suppress(Warning.STRICT_INHERITANCE)
				.verify();
	}
	
	@Test
	public void succeed_whenHashCodeIsNotFinal_givenAClassThatIsNotFinalAndAnEqualsMethodThatIsFinalAndWarningIsSuppressed() {
		EqualsVerifier.forClass(FinalEqualsPoint.class)
				.suppress(Warning.STRICT_INHERITANCE)
				.verify();
	}
	
	private <T> void check(Class<T> type) {
		expectFailure(BOTH_FINAL_OR_NONFINAL);
		EqualsVerifier.forClass(type)
				.usingGetClass()
				.verify();
	}
	
	static class FinalEqualsNonFinalHashCode {
		private final int i;
		
		public FinalEqualsNonFinalHashCode(int i) { this.i = i; }
		
		@Override
		public final boolean equals(Object obj) {
			if (obj == null || obj.getClass() != getClass()) {
				return false;
			}
			FinalEqualsNonFinalHashCode other = (FinalEqualsNonFinalHashCode)obj;
			return other.i == i;
		}
		
		@Override public int hashCode() { return defaultHashCode(this); }
	}
	
	static class NonFinalEqualsFinalHashCode {
		private final int i;
		
		public NonFinalEqualsFinalHashCode(int i) { this.i = i; }
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null || obj.getClass() != getClass()) {
				return false;
			}
			NonFinalEqualsFinalHashCode other = (NonFinalEqualsFinalHashCode)obj;
			return other.i == i;
		}
		
		@Override public final int hashCode() { return defaultHashCode(this); }
	}

	static class FinalEqualsPoint extends Point {
		public FinalEqualsPoint(int x, int y) { super(x, y); }
		
		@Override
		public final boolean equals(Object obj) {
			return super.equals(obj);
		}
	}
}
